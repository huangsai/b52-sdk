package come.mobile.sdk.push.huawei

import android.content.Intent
import com.huawei.hms.push.HmsMessageService
import come.mobile.sdk.push.PushX
import timber.log.Timber

class HmsPushService : HmsMessageService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag(PushX.TAG).d("receive token:$token")
        sendTokenToDisplay(token)
    }

    private fun sendTokenToDisplay(token: String) {
        val intent = Intent("com.huawei.push.codelab.ON_NEW_TOKEN")
        intent.putExtra("token", token)
        sendBroadcast(intent)
    }
}