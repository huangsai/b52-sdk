package com.mobile.sdk.call

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.mvvm.AppContext
import com.mobile.guava.android.mvvm.AppManager
import com.mobile.sdk.call.dagger.CallComponent
import com.mobile.sdk.call.dagger.DaggerCallComponent
import com.mobile.sdk.call.socket.AppWebSocket
import com.mobile.sdk.sister.data.db.RoomAppDatabase
import com.mobile.sdk.sister.data.file.AppPrefs

object CallX {

    const val BUS_CALL_STATE = 10000

    const val TAG = "CallX"

    internal val isSocketConnected = MutableLiveData<Boolean>()

    var socketServer = "wss://java.cg.xxx:30306/voice?username=B"
        private set

    var httpServer = ""
        private set

    lateinit var component: CallComponent
        private set

    private fun createRoomDatabase(): RoomAppDatabase {
        return Room.databaseBuilder(AndroidX.myApp, RoomAppDatabase::class.java, "sdk_call.db3")
            .addCallback(RoomAppDatabase.DbCallback())
            .build()
    }

    fun setup(app: Application, isDebug: Boolean) {
        if (::component.isInitialized) {
            return
        }

        AndroidX.setup(app, isDebug)
        component = DaggerCallComponent.factory().create(
            app,
            AppContext(),
            createRoomDatabase(),
            AppPrefs
        )

        AppWebSocket.toString()
        AppManager.initialize()
    }

    fun setServers(server: String) {
        socketServer = "wss://${server}/ws/csms?from=android"
        httpServer = "https://${server}/"
    }
}