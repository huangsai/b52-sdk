package com.mobile.sdk.sister.base

import androidx.core.content.FileProvider

/**
 * 模块FileProvider，避免不同app的FileProvider冲突
 */
class SisterFileProvider : FileProvider()