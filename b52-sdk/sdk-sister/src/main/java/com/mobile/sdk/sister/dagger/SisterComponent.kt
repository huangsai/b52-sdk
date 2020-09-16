package com.mobile.sdk.sister.dagger

import android.app.Application
import com.mobile.guava.android.mvvm.dagger.ViewModelFactoryComponent
import com.mobile.guava.https.HttpsComponent
import com.mobile.guava.https.PlatformContext
import com.mobile.sdk.sister.data.db.AppDatabase
import com.mobile.sdk.sister.data.http.DataService
import com.mobile.sdk.sister.data.SisterRepository
import com.mobile.sdk.sister.data.file.PlatformPreferences
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [SisterModule::class])
@Singleton
interface SisterComponent : HttpsComponent, ViewModelFactoryComponent {

    fun dataService(): DataService

    fun sisterRepository(): SisterRepository

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance app: Application,
            @BindsInstance platformContext: PlatformContext,
            @BindsInstance appDatabase: AppDatabase,
            @BindsInstance platformPreferences: PlatformPreferences
        ): SisterComponent
    }
}