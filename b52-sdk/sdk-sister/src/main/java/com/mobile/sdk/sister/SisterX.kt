package com.mobile.sdk.sister

import android.app.Application
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.mvvm.AppContext
import com.mobile.guava.android.mvvm.AppTimber
import com.mobile.guava.android.mvvm.showDialogFragment
import com.mobile.guava.jvm.Guava
import com.mobile.sdk.sister.base.AppManager
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
import timber.log.Timber

object SisterX {

    const val BUS_MSG_STATUS = 20049
    const val BUS_MSG_NEW = 20050

    var toUserId = 0L

    lateinit var component: SisterComponent
        private set

    fun setup(app: Application, isDebug: Boolean) {
        AndroidX.setup(app)
        Guava.isDebug = isDebug
        Guava.timber = AppTimber()
        component = DaggerSisterComponent.factory().create(
            app,
            AppContext(),
            createRoomDatabase(),
            AppPreferences
        )
        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        }

        AppWebSocket.toString()
        AppManager.initialize()
    }

    fun setUsername(username: String) {
        GlobalScope.launch(Dispatchers.IO) {
            component.sisterRepository().user(username).also {
                SocketUtils.postLogin()
                AndroidX.notifyLogin()
            }
        }
    }

    fun isLoginUser(): Boolean {
        return AppPreferences.token.isNotEmpty() && AppPreferences.token.length > 8
    }

    fun show(activity: FragmentActivity, cancelable: Boolean): DialogFragment {
        return MainDialogFragment.newInstance(cancelable).also {
            activity.showDialogFragment(it)
        }
    }

    private fun createRoomDatabase(): RoomAppDatabase {
        return Room.databaseBuilder(AndroidX.myApp, RoomAppDatabase::class.java, AndroidX.SQL_DB3)
            .addCallback(RoomAppDatabase.DbCallback())
            .build()
    }
}