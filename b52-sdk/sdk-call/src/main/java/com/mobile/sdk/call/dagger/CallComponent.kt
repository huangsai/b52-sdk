package com.mobile.sdk.call.dagger

import android.app.Application
import com.mobile.guava.android.mvvm.dagger.ViewModelFactoryComponent
import com.mobile.guava.data.DataComponent
import com.mobile.guava.data.PlatformContext
import com.mobile.sdk.sister.data.db.AppDatabase
import com.mobile.sdk.sister.data.file.PlatformPrefs
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [CallModule::class])
@Singleton
interface CallComponent : DataComponent, ViewModelFactoryComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance app: Application,
            @BindsInstance platformContext: PlatformContext,
            @BindsInstance appDatabase: AppDatabase,
            @BindsInstance platformPrefs: PlatformPrefs
        ): CallComponent
    }
}