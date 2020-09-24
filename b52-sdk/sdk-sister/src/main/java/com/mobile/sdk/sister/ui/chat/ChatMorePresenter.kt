package com.mobile.sdk.sister.ui.chat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.mobile.sdk.sister.R
import com.mobile.sdk.sister.databinding.SisterFragmentChatBinding
import com.mobile.sdk.sister.ui.SisterViewModel
import com.pacific.adapter.AdapterViewHolder
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.createBalloon
import java.io.File

class ChatMorePresenter(
    fragment: ChatFragment,
    binding: SisterFragmentChatBinding,
    model: SisterViewModel,
) : BaseChatPresenter(fragment, binding, model) {

    private val authorities = "com.mobile.sdk.sister.provider"
    private var cameraImageFile: File? = null
    private val requestImage = fragment.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            if (cameraImageFile == null) {
                it.data?.data?.let { uri: Uri -> fragment.chatListPresenter.postImage(uri) }
            } else {
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(
                        fragment.requireContext(),
                        authorities,
                        cameraImageFile!!
                    )
                } else {
                    Uri.fromFile(cameraImageFile!!)
                }
                fragment.chatListPresenter.postImage(uri)
            }
        }
    }

    private var balloon: Balloon? = null

    fun showPop() {
        if (balloon != null) {
            balloon!!.showAlignTop(binding.layoutInput, 0, 10)
            return
        }
        val popWidth = binding.layoutInput.width
        balloon = createBalloon(fragment.requireContext()) {
            setLayout(R.layout.sister_popup_chat_more)
            cornerRadius = 0f
            arrowVisible = false
            width = popWidth
            setBackgroundColorResource(R.color.sister_color_black_transparent)
        }
        balloon!!.showAlignTop(binding.layoutInput, 0, 10)
        balloon!!.getContentView().findViewById<ImageView>(R.id.iv_picture).setOnClickListener(this)
        balloon!!.getContentView().findViewById<ImageView>(R.id.iv_camera).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_picture -> {
                requestImage.launch(requestGalleryIntent())
            }
            R.id.iv_camera -> {
                requestImage.launch(requestCameraIntent())
            }
        }
        balloon!!.dismiss()
    }

    private fun requestGalleryIntent(): Intent {
        cameraImageFile = null
        return Intent().apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            action = Intent.ACTION_PICK
        }
    }

    private fun requestCameraIntent(): Intent {
        cameraImageFile = null
        val tempFile = File(
            fragment.requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            System.currentTimeMillis().toString() + ".jpeg"
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(
                fragment.requireContext(),
                authorities,
                tempFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile))
        }
        cameraImageFile = tempFile
        return intent
    }

    override fun load() {
        TODO("Not yet implemented")
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        cameraImageFile = null
    }
}