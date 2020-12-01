package com.mobile.sdk.call

import androidx.lifecycle.ViewModel
import com.mobile.sdk.sister.data.CallRepository
import javax.inject.Inject

class CallViewModel @Inject constructor(
    private val callRepository: CallRepository
) : ViewModel() {


}