# b52-sdk

针对外部项目组输出sdk


## 客服sdk

配置kotlin语言、增加内部maven地址 

```
buildscript {

    ext {
        kotlin_version = '1.4.10'
    }

    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.kotlin}"
    }
}

allprojects { project ->

    repositories {
        maven { url "http://172.31.9.72:8081/repository/maven-releases/" }
    }
}
```

依赖sister-sdk库

```
implementation 'com.mobile.sdk:sdk-sister:0.1.1'
```

使用sister-sdk库
```
// init sister-sdk in your app
class MyApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        SisterX.INSTANCE.setup(this, BuildConfig.DEBUG)
    }
}

// set servers
SisterX.INSTANCE.setServers(server: String)

// login or switch user
SisterX.INSTANCE.setUsername("test001")

// logout
SisterX.INSTANCE.setUsername("")

// show and dismiss
DialogFragment dialog = SisterX.INSTANCE.show(activity: FragmentActivity, cancelable: Boolean);
dialog.dismiss();
```

