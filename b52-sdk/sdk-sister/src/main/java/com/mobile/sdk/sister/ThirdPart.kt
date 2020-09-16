package com.mobile.sdk.sister

import androidx.annotation.DrawableRes

data class ThirdPart(
    val username: String,
    val userImage: String,
    val userId: String,
    val token: String,
    @DrawableRes val userImageRes: Int
)