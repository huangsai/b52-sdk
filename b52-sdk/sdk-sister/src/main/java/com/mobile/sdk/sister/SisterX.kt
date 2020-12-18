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
import com.mobile.guava.android.postToMainThread
import com.mobile.sdk.sister.bubble.Bubble
import com.mobile.sdk.sister.dagger.DaggerSisterComponent
import com.mobile.sdk.sister.dagger.SisterComponent
import com.mobile.sdk.sister.data.db.RoomAppDatabase
import com.mobile.sdk.sister.data.file.AppPrefs
import com.mobile.sdk.sister.socket.AppWebSocket
import com.mobile.sdk.sister.socket.SocketUtils
import com.mobile.sdk.sister.ui.SisterDialogFragment
import com.mobile.sdk.sister.ui.items.MsgItem
import com.mobile.sdk.sister.ui.views.MyKeyboardHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object SisterX {

    const val TAG = "SisterX"

    const val BUS_MSG_AUDIO_PLAYING = 20048
    const val BUS_MSG_STATUS = 20049
    const val BUS_MSG_NEW = 20050
    const val BUS_MSG_AUTO_REPLY = 20051

    const val BUS_CLICK_ALIPAY = 20052
    const val BUS_CLICK_WECHAT = 20053
    const val BUS_CLICK_UNION = 20054

    internal val isSocketConnected = MutableLiveData<Boolean>()
    internal val isUiPrepared = MutableLiveData<Boolean>()
    internal val isLogin = MutableLiveData<Boolean>()
    internal val hasBufferMsgItems = MutableLiveData<Boolean>()

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

    /**
     * 充值会话
     */
    internal fun resetChatSession() {
        sisterUserId = "0"
        chatId = 0L
        postToMainThread {
            Runnable {
                Bubble.hide()
            }
        }
    }

    /**
     * 初始化客服SDK
     * @param app Application
     * @param isDebug 是否是debug模式
     */
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
        MyKeyboardHelper.toString()
        AppManager.initialize()
    }

    /**
     * 设置服务器地址
     * @param server 服务器地址
     */
    fun setServers(server: String) {
        socketServer = "wss://${server}/ws/csms?from=android"
        httpServer = "https://${server}/"
    }

    /**
     * 设置用户
     * @param _loginName 用户名称
     */
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

    /**
     * 打开客服弹窗
     * @param activity FragmentActivity
     * @param cancelable 是否可以取消弹窗
     */
    fun show(activity: FragmentActivity, cancelable: Boolean): DialogFragment {
        return SisterDialogFragment.newInstance(cancelable).also {
            activity.showDialogFragment(it)
        }
    }
}