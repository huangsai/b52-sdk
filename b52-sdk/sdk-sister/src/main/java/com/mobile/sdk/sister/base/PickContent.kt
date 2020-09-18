package com.mobile.sdk.sister.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import java.io.File


class PickContent(private val context: Context) : ActivityResultContract<Void, File>() {

    override fun createIntent(context: Context, input: Void?): Intent {
        return Intent(Intent.ACTION_PICK).setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): File? {
        return if (intent == null || resultCode != Activity.RESULT_OK) {
            null
        } else {
            val images = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(intent.data!!, images, null, null, null)
            val index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            val imgUrl = cursor?.getString(index!!)
            File(imgUrl)
        }

    }
}