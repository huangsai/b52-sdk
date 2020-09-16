package com.mobile.sdk.sister.dagger

import com.mobile.guava.android.mvvm.dagger.SingletonScopeViewModelFactoryBinder
import com.mobile.guava.https.HttpsModule
import com.mobile.guava.https.SimpleHttpsModule
import com.mobile.sdk.sister.data.http.DataService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.LoggingEventListener
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@Module(includes = [SisterBinder::class, SingletonScopeViewModelFactoryBinder::class])
class SisterModule : HttpsModule {

    private val delegate = SimpleHttpsModule()

    @Provides
    @Singleton
    override fun provideHttpLoggingInterceptor(
        httpLoggingInterceptorLogger: HttpLoggingInterceptor.Logger
    ): HttpLoggingInterceptor {
        return delegate.provideHttpLoggingInterceptor(httpLoggingInterceptorLogger)
    }

    @Provides
    @Singleton
    override fun provideHttpLoggingInterceptorLogger(): HttpLoggingInterceptor.Logger {
        return delegate.provideHttpLoggingInterceptorLogger()
    }

    @Provides
    @Singleton
    override fun provideJson(): Moshi {
        return delegate.provideJson()
    }

    @Provides
    @Singleton
    override fun providePoorSSLContext(x509TrustManager: X509TrustManager): SSLContext {
        return delegate.providePoorSSLContext(x509TrustManager)
    }

    @Provides
    @Singleton
    override fun providePoorX509TrustManager(): X509TrustManager {
        return delegate.providePoorX509TrustManager()
    }

    @Provides
    @Singleton
    override fun provideRetrofit(okHttpClient: OkHttpClient, json: Moshi): Retrofit {
        return delegate.provideRetrofit(okHttpClient, json)
    }

    @Provides
    @Singleton
    override fun provideOkHttpClient(
        x509TrustManager: X509TrustManager,
        sslContext: SSLContext,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        httpLoggingInterceptorLogger: HttpLoggingInterceptor.Logger
    ): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(httpLoggingInterceptor)
            .eventListenerFactory(LoggingEventListener.Factory(httpLoggingInterceptorLogger))
            .sslSocketFactory(sslContext.socketFactory, x509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .pingInterval(180 * 1000, TimeUnit.MILLISECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideDataService(retrofit: Retrofit): DataService {
        return retrofit.create(DataService::class.java)
    }
}