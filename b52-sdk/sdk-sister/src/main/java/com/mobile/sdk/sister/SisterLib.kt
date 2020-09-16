package com.mobile.sdk.sister

import android.app.Application
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
import com.mobile.sdk.sister.socket.AppWebSocket
import com.mobile.sdk.sister.ui.MainDialogFragment
import timber.log.Timber

object SisterLib {

    @get:JvmName("component")
    lateinit var component: SisterComponent
        private set

    fun setup(app: Application, isDebug: Boolean) {
        AndroidX.setup(app)
        Guava.isDebug = isDebug
        Guava.timber = AppTimber()

        component = DaggerSisterComponent.factory().create(app, AppContext(), createRoomDatabase())
        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        }

        AppWebSocket.toString()
        AppManager.initialize()
    }

    private fun createRoomDatabase(): RoomAppDatabase {
        return Room.databaseBuilder(AndroidX.myApp, RoomAppDatabase::class.java, AndroidX.SQL_DB3)
            .addCallback(RoomAppDatabase.DbCallback())
            .build()
    }

    fun show(activity: FragmentActivity, cancelable: Boolean) {
        require(::component.isInitialized){ "SisterLib is not setup yet "}
        activity.showDialogFragment(MainDialogFragment.newInstance(cancelable))
    }
}