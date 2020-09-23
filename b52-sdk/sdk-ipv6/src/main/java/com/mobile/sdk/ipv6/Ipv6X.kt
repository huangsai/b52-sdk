package com.mobile.sdk.ipv6

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.mvvm.AppContext
import com.mobile.guava.android.mvvm.AppManager
import com.mobile.sdk.ipv6.dagger.DaggerIpv6Component
import com.mobile.sdk.ipv6.dagger.Ipv6Component
import com.mobile.sdk.ipv6.data.api.ApiConfig
import com.mobile.sdk.ipv6.db.RoomAppDatabase
import com.mobile.sdk.ipv6.socket.AppWebSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Ipv6X {

    const val TAG = "Ipv6X"

    lateinit var component: Ipv6Component
        private set

    var httpHosts: List<ApiConfig.Ip> = emptyList()
        private set

    var socketHost = ApiConfig.Ip("", "", "", 1)
        private set

    val isSocketConnected: MutableLiveData<Boolean> = MutableLiveData()

    fun setup(app: Application, isDebug: Boolean) = GlobalScope.launch(Dispatchers.IO) {
        AndroidX.setup(app, isDebug)
        component = DaggerIpv6Component.factory().create(
            app,
            AppContext(),
            createRoomDatabase()
        )
        component.ipv6Repository().loadIp().let { list ->
            if (list.isNotEmpty()) {
                httpHosts = list.filter { it.urlType == 0 }
                socketHost = list.filter { it.urlType == 1 }[0]
            }
        }
        component.ipv6Repository().config().dataOrNull()?.let { list ->
            if (list.isNotEmpty()) {
                httpHosts = list.filter { it.urlType == 0 }
                socketHost = list.filter { it.urlType == 1 }[0]
            }
        }

        withContext(Dispatchers.Main) {
            AppWebSocket.toString()
            AppManager.initialize()
        }
    }

    private fun createRoomDatabase(): RoomAppDatabase {
        return Room.databaseBuilder(AndroidX.myApp, RoomAppDatabase::class.java, "sdk_ipv6.db3")
            .addCallback(RoomAppDatabase.DbCallback())
            .build()
    }
}

