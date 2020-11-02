package com.mobile.sdk.sister

import android.app.Application
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.mvvm.AppContext
import com.mobile.guava.android.mvvm.AppManager
import com.mobile.guava.android.mvvm.showDialogFragment
import com.mobile.sdk.sister.dagger.DaggerSisterComponent
import com.mobile.sdk.sister.dagger.SisterComponent
import com.mobile.sdk.sister.data.db.RoomAppDatabase
import com.mobile.sdk.sister.data.file.AppPrefs
import com.mobile.sdk.sister.socket.AppWebSocket
import com.mobile.sdk.sister.socket.SocketUtils
import com.mobile.sdk.sister.ui.MainDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object SisterX {

    internal const val TAG = "SisterX"

    internal const val BUS_MSG_AUDIO_PLAYING = 20048
    internal const val BUS_MSG_STATUS = 20049
    internal const val BUS_MSG_NEW = 20050

    internal var sisterUserId = "0"
    internal var chatId = 0L

    internal var socketServer = ""
    internal var httpServer = ""

    internal lateinit var component: SisterComponent
        private set

    internal val isSocketConnected: MutableLiveData<Boolean> = MutableLiveData()

    internal val isChatLogin: MutableLiveData<Boolean> = MutableLiveData()

    internal val sisterState: MutableLiveData<Int> = MutableLiveData()

    fun setup(app: Application, isDebug: Boolean) {
        if (::component.isInitialized) {
            return
        }

        AndroidX.setup(app, isDebug)
        component = DaggerSisterComponent.factory().create(
            app,
            AppContext(),
            createRoomDatabase(),
            AppPrefs
        )

        AppWebSocket.toString()
        AppManager.initialize()
    }

    fun setServers(_socketServer: String, _httpServer: String) {
        require(_socketServer.isNotEmpty())
        require(_httpServer.isNotEmpty())
        socketServer = _socketServer
        httpServer = _httpServer
    }

    fun setUsername(username: String) {
        isChatLogin.value = false

        GlobalScope.launch(Dispatchers.IO) {
            AppPrefs.userId = ""
            AppPrefs.loginName = ""
            component.sisterRepository().user(username).let {
                AndroidX.notifyLogin()
                SocketUtils.postLogin()
            }
            isChatLogin.postValue(true)
        }
    }

    fun isLoginUser(): Boolean {
        return AppPrefs.loginName.isNotEmpty() && AppPrefs.token.isNotEmpty()
    }

    fun show(activity: FragmentActivity, cancelable: Boolean): DialogFragment {
        return MainDialogFragment.newInstance(cancelable).also {
            activity.showDialogFragment(it)
        }
    }

    private fun createRoomDatabase(): RoomAppDatabase {
        return Room.databaseBuilder(AndroidX.myApp, RoomAppDatabase::class.java, "sdk_sister.db3")
            .addCallback(RoomAppDatabase.DbCallback())
            .build()
    }
}