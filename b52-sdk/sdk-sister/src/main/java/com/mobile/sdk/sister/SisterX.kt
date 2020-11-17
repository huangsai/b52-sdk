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
import com.mobile.sdk.sister.ui.items.MsgItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object SisterX {

    const val TAG = "SisterX"

    const val BUS_MSG_AUDIO_PLAYING = 20048
    const val BUS_MSG_STATUS = 20049
    const val BUS_MSG_NEW = 20050
    const val BUS_MSG_AUTO_REPLY = 20051

    internal val isSocketConnected = MutableLiveData<Boolean>()
    internal val isUiPrepared = MutableLiveData<Boolean>()
    internal val isLogin = MutableLiveData<Boolean>()

    internal val bufferMsgItems = ArrayList<MsgItem>()

    internal var sisterUserId = "0"
    internal var chatId = 0L
    internal var isForceLogout = false

    var hasUser = false
        private set

    var socketServer = ""
        private set

    var httpServer = ""
        private set

    lateinit var component: SisterComponent
        private set

    init {
        isSocketConnected.observeForever {
            if (it) {
                isForceLogout = false
                require(hasUser)
                SocketUtils.postLogin()
            } else {
                resetChatSession()
                isLogin.value = false
            }
        }
    }

    private fun createRoomDatabase(): RoomAppDatabase {
        return Room.databaseBuilder(AndroidX.myApp, RoomAppDatabase::class.java, "sdk_sister.db3")
            .addCallback(RoomAppDatabase.DbCallback())
            .build()
    }

    internal fun hasSister(): Boolean = chatId > 0 && sisterUserId != "0"

    internal fun resetChatSession() {
        sisterUserId = "0"
        chatId = 0L
    }

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
        socketServer = "ws://${_socketServer}:31301/ws/csms?from=android"
        httpServer = "http://${_httpServer}:31301/"
    }

    fun setUser(_loginName: String) = GlobalScope.launch(Dispatchers.IO) {
        if (true == isLogin.value && _loginName == AppPrefs.loginName) {
            return@launch
        }

        resetChatSession()
        isForceLogout = false
        hasUser = false
        isLogin.postValue(false)
        AppPrefs.userId = ""
        AppPrefs.loginName = ""
        component.sisterRepository().user(_loginName).let {
            hasUser = true
            AppWebSocket.reconnect()
        }
    }

    fun show(activity: FragmentActivity, cancelable: Boolean): DialogFragment {
        return MainDialogFragment.newInstance(cancelable).also {
            activity.showDialogFragment(it)
        }
    }
}