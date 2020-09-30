package com.mobile.sdk.ipv6.dagger

import android.app.Application
import com.mobile.guava.data.DataComponent
import com.mobile.guava.data.PlatformContext
import com.mobile.sdk.ipv6.data.Ipv6Repository
import com.mobile.sdk.ipv6.db.AppDatabase
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [Ipv6Module::class])
@Singleton
interface Ipv6Component : DataComponent {

    fun ipv6Repository(): Ipv6Repository

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance app: Application,
            @BindsInstance platformContext: PlatformContext,
            @BindsInstance appDatabase: AppDatabase
        ): Ipv6Component
    }
}