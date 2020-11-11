package come.mobile.sdk.push.vivo

import android.content.Context
import com.vivo.push.PushClient
import com.vivo.push.ups.VUpsManager
import come.mobile.sdk.push.PushX
import timber.log.Timber

object PushVivo {

    private const val AppId = ""

    private const val AppKey = ""

    private const val AppSecret = ""

    fun init(context: Context) {
        PushClient.getInstance(context).initialize()
        //打开push
        PushClient.getInstance(context).turnOnPush {
            if (it == 0)
                Timber.tag(PushX.TAG).d("VIVO push打开成功")
            else
                Timber.tag(PushX.TAG).d("VIVO push打开失败")
        }
        //注册push
        VUpsManager.getInstance().registerToken(context, AppId, AppKey, AppSecret)
        { tokenResult ->
            if (tokenResult.returnCode == 0) {
                Timber.tag(PushX.TAG).d("注册成功 regID = $tokenResult.token")
            } else {
                Timber.tag(PushX.TAG).d("注册失败")
            }
        }
    }
}