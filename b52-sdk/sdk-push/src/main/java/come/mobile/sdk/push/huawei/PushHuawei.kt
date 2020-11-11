package come.mobile.sdk.push.huawei

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import come.mobile.sdk.push.PushX
import timber.log.Timber

object PushHuawei {

    fun init(context: Context) {
        registerReceiver(context)
    }

    private fun registerReceiver(context: Context) {
        val receiver = MyReceiver()
        val filter = IntentFilter()
        filter.addAction("com.huawei.codelabpush.ON_NEW_TOKEN")
        context.registerReceiver(receiver, filter)
    }

    class MyReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            if ("com.huawei.codelabpush.ON_NEW_TOKEN" == intent.action) {
                val token = intent.getStringExtra("token")
                Timber.tag(PushX.TAG).d("huawei token : $token")
            }
        }
    }
}