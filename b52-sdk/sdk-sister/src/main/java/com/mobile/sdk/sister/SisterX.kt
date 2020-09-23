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
import com.mobile.sdk.sister.data.file.AppPreferences
import com.mobile.sdk.sister.socket.AppWebSocket
import com.mobile.sdk.sister.socket.SocketUtils
import com.mobile.sdk.sister.ui.MainDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object SisterX {

    const val TAG = "SisterX"

    const val BUS_MSG_AUDIO_PLAYING = 20048
    const val BUS_MSG_STATUS = 20049
    const val BUS_MSG_NEW = 20050

    var toUserId = ""

    lateinit var component: SisterComponent
        private set

    val isSocketConnected: MutableLiveData<Boolean> = MutableLiveData()

    fun setup(app: Application, isDebug: Boolean) {
        AndroidX.setup(app, isDebug)
        component = DaggerSisterComponent.factory().create(
            app,
            AppContext(),
            createRoomDatabase(),
            AppPreferences
        )
        AppPreferences.username = ""

        AppWebSocket.toString()
        AppManager.initialize()
    }

    fun setUsername(username: String) {
        GlobalScope.launch(Dispatchers.IO) {
            component.sisterRepository().user(username).let {
                AndroidX.notifyLogin()
                SocketUtils.postLogin()
            }
        }
    }

    fun isLoginUser(): Boolean {
        return AppPreferences.username.isNotEmpty() && AppPreferences.token.isNotEmpty()
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