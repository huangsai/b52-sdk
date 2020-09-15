package com.mobile.sdk.sister.data.http

import com.mobile.guava.https.PlatformContext
import com.mobile.sdk.sister.data.db.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SisterRepository @Inject constructor(
    private val dataService: DataService,
    private val platformContext: PlatformContext,
    private val appDatabase: AppDatabase
)