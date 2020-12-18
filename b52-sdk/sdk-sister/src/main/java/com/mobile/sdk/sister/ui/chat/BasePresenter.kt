package com.mobile.sdk.sister.ui.chat

import android.view.View
import com.mobile.guava.android.mvvm.lifecycle.SimplePresenter
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.SisterDialogFragment
import com.mobile.sdk.sister.ui.SisterViewModel
import com.pacific.adapter.AdapterImageLoader

abstract class BasePresenter(
    protected val fragment: SisterDialogFragment.MyFragment,
    protected val binding: SisterFragmentChatBinding,
    protected val model: SisterViewModel
) : SimplePresenter(), View.OnClickListener, AdapterImageLoader {

    abstract fun load()
}