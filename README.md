# b52-sdk

针对外部项目组输出sdk


## 客服sdk

配置工程支持kotlin语言、增加内部maven地址 

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
implementation com.mobile.sdk:sdk-sister:${current_version}
```

使用sister-sdk库
```
// in your App init 
class MyApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        SisterX.setup(this, BuildConfig.DEBUG)
    }
}

// login or switch user
SisterX.setUsername("barry123")

// logout
SisterX.setUsername("")
```

