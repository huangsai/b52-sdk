package come.mobile.sdk.push

import android.app.Application
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.mvvm.AppManager

object PushX {

    internal const val TAG = "PushX"

    fun setup(app: Application, isDebug: Boolean) {
        AndroidX.setup(app, isDebug)
        AppManager.initialize()
    }

}