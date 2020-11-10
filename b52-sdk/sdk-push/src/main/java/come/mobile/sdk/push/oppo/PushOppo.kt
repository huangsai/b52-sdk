package come.mobile.sdk.push.oppo

import android.content.Context
import com.heytap.msp.push.HeytapPushManager
import com.heytap.msp.push.callback.ICallBackResultService
import com.heytap.msp.push.mode.ErrorCode
import com.mobile.guava.jvm.Guava
import come.mobile.sdk.push.PushX
import timber.log.Timber

object PushOppo {

    private const val AppId = ""

    private const val AppKey = ""

    private const val AppSecret = ""

    fun init(context: Context) {
        HeytapPushManager.init(context, Guava.isDebug)
        if (HeytapPushManager.isSupportPush()) {
            HeytapPushManager.register(context, AppKey, AppSecret, mPushCallback)
            HeytapPushManager.requestNotificationPermission()
        }
    }

    /*
    1、ERROR = -2;  //initvalue初始值
    2、SERVICE_CURRENTLY_UNAVAILABLE = -1;  //Service Currently Unavailable  服务不可用，此时请开发者稍候再试
    3、SUCCESS = 0;  //成功，只表明接口调用成功
    4、INSUFFICIENT_ISV_PERMISSIONS = 11;  //Insufficient ISV Permissions 无此API调用权限，开发者权限不足
    5、HTTP_ACTION_NOT_ALLOWED = 12;  //Http Action Not Allowed HTTP 方法不正确
    6、APP_CALL_LIMITED = 13;  //App Call Limited 应用调用次数超限，包含调用频率超限
    7、INVALID_APP_KEY = 14;  // Invalid App Key 无效的AppKey参数
    8、MISSING_APP_KEY = 15;  //Missing App Key 缺少AppKey参数
    9、INVALID_SIGNATURE_SIGN = 16;  //Invalid Signature sign校验不通过，无效签名
    10、MISSING_SIGNATURE = 17;  //Missing Signature 缺少签名参数
    11、MISSING_TIMESTAMP = 18;  //Missing Timestamp 缺少时间戳参数
    12、NVALID_TIMESTAMP = 19;  //Invalid Timestamp 非法的时间戳参数
    13、INVALID_METHOD = 20;  //Invalid Method 不存在的方法名
    14、MISSING_METHOD = 21;  //Missing Method 缺少方法名参数
    15、MISSING_VERSION = 22;  //Missing Version 缺少版本参数
    16、INVALID_VERSION = 23;  //Invalid Version 非法的版本参数，用户传入的版本号格式错误，必需为数字格式
    17、UNSUPPORTED_VERSION = 24;  //Unsupported Version 不支持的版本号，用户传入的版本号没有被提供
    18、INVALID_ENCODING = 25;  //Invalid encoding 编码错误，一般是用户做http请求的时候没有用UTF-8编码请求造成IP_BLACK_LIST = 26;//IP Black List IP黑名单
    19、MISSING_REQUIRED_ARGUMENTS = 40;  //Missing Required Arguments 缺少必选参数 ，API文档中设置为必选的参数是必传的，请仔细核对文档
    20、INVALID_ARGUMENTS = 41;  //Invalid Arguments 参数错误，一般是用户传入参数非法引起的，请仔细检查入参格式、范围是否一一对应
     */
    private val mPushCallback = object : ICallBackResultService {

        //注册的结果,如果注册成功,registerID就是客户端的唯一身份标识
        override fun onRegister(responseCode: Int, registerID: String?) {
            if (responseCode == ErrorCode.SUCCESS)
                Timber.tag(PushX.TAG).d("OPPO注册成功: $registerID")
        }

        //反注册的结果
        override fun onUnRegister(responseCode: Int) {
            TODO("Not yet implemented")
        }

        //获取设置推送时间的执行结果
        override fun onSetPushTime(responseCode: Int, pushTime: String?) {
            TODO("Not yet implemented")
        }

        //获取当前的push状态返回,根据返回码判断当前的push状态,返回码具体含义可以参考[错误码]
        override fun onGetPushStatus(responseCode: Int, status: Int) {
            TODO("Not yet implemented")
        }

        //获取当前通知栏状态，返回码具体含义可以参考[错误码]
        override fun onGetNotificationStatus(responseCode: Int, status: Int) {
            TODO("Not yet implemented")
        }

    }
}