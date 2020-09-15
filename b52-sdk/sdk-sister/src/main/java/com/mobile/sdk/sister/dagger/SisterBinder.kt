package com.mobile.sdk.sister.dagger

import androidx.lifecycle.ViewModel
import com.mobile.guava.android.mvvm.dagger.ViewModelKey
import com.mobile.sdk.sister.ui.SisterViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SisterBinder {

    @Binds
    @IntoMap
    @ViewModelKey(SisterViewModel::class)
    abstract fun bindSisterViewModel(it: SisterViewModel): ViewModel
}