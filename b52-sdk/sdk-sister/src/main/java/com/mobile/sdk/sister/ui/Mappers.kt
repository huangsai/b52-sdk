package com.mobile.sdk.sister.ui

import com.mobile.sdk.sister.data.file.AppPreferences
import com.mobile.sdk.sister.data.http.ApiMessage
import com.mobile.sdk.sister.data.http.ApiSimpleMessage

fun ApiMessage.asSimple(): ApiSimpleMessage {
    return ApiSimpleMessage(id,type, content, toUserId)
}

fun ApiSimpleMessage.asNormal(): ApiMessage {
    return ApiMessage(
        id,
        content,
        toUserId,
        type,
        System.currentTimeMillis(),
        AppPreferences.userProfile,
        AppPreferences.username,
        AppPreferences.userId,
        2
    )
}