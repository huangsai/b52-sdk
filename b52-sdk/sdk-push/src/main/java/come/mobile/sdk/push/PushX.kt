package come.mobile.sdk.push

import android.app.Application
import android.content.Context
import android.os.Build
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.mvvm.AppManager
import come.mobile.sdk.push.huawei.PushHuawei
import come.mobile.sdk.push.oppo.PushOppo
import come.mobile.sdk.push.vivo.PushVivo
import come.mobile.sdk.push.xiaomi.PushXiaomi
import java.util.*

object PushX {

    internal const val TAG = "PushX"

    fun setup(app: Application, isDebug: Boolean) {
        AndroidX.setup(app, isDebug)
        AppManager.initialize()
        initSDK(app)
    }

    private fun initSDK(context: Context) {
        if (isHuawei())
            PushHuawei.init(context)
        if (isOppo())
            PushOppo.init(context)
        if (isVivo())
            PushVivo.init(context)
        if (isXiaomi())
            PushXiaomi.init(context)
    }

    private fun isHuawei(): Boolean {
        return Build.BRAND.contains("huawei") || Build.BRAND.contains("honor")
    }

    private fun isOppo(): Boolean {
        return Build.BRAND.contains("oppo")
    }

    private fun isVivo(): Boolean {
        return Build.BRAND.contains("vivo") || Build.BRAND.contains("bbk")
    }

    private fun isXiaomi(): Boolean {
        return Build.MANUFACTURER.toLowerCase(Locale.getDefault()) == "xiaomi"
    }

}