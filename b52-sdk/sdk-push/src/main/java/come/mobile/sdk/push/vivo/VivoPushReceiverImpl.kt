package come.mobile.sdk.push.vivo

import android.content.Context
import com.vivo.push.model.UPSNotificationMessage
import com.vivo.push.sdk.OpenClientPushMessageReceiver

class VivoPushReceiverImpl : OpenClientPushMessageReceiver() {

    /***
     * 当通知被点击时回调此方法
     * @param context 应用上下文
     * @param msg 通知详情，详细信息见API接入文档
     */
    override fun onNotificationMessageClicked(context: Context?, msg: UPSNotificationMessage?) {
        TODO("Not yet implemented")
    }

    /***
     * 当首次turnOnPush成功或regId发生改变时，回调此方法
     * 如需获取regId，请使用PushClient.getInstance(context).getRegId()
     * @param context 应用上下文
     * @param regId 注册id
     */
    override fun onReceiveRegId(context: Context?, regId: String?) {
        TODO("Not yet implemented")
    }
}