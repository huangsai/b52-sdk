package com.mobile.sdk.call.dagger

import androidx.lifecycle.ViewModel
import com.mobile.guava.android.mvvm.dagger.ViewModelKey
import com.mobile.sdk.call.CallViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class CallBinder {

    @Binds
    @IntoMap
    @ViewModelKey(CallViewModel::class)
    abstract fun bindSisterViewModel(it: CallViewModel): ViewModel
}