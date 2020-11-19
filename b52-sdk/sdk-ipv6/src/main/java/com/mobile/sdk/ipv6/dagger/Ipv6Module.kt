package com.mobile.sdk.ipv6.dagger

import com.mobile.guava.data.DataModule
import com.mobile.guava.data.SimpleDataModule
import com.mobile.sdk.ipv6.data.ApiConverterFactory
import com.mobile.sdk.ipv6.data.DataService
import com.mobile.sdk.ipv6.data.HostSelectionInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.LoggingEventListener
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@Module
class Ipv6Module : DataModule {

    private val delegate = SimpleDataModule()

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
    override fun provideOkHttpClient(
        x509TrustManager: X509TrustManager,
        sslContext: SSLContext,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        httpLoggingInterceptorLogger: HttpLoggingInterceptor.Logger
    ): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(HostSelectionInterceptor())
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
    override fun provideRetrofit(okHttpClient: OkHttpClient, json: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://172.31.8.110:30400")
            .client(okHttpClient)
            .addConverterFactory(ApiConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(json))
            .build()
    }

    @Provides
    @Singleton
    fun provideDataService(retrofit: Retrofit): DataService {
        return retrofit.create(DataService::class.java)
    }
}