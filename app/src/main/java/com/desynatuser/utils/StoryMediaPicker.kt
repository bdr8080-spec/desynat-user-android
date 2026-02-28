package com.desynatuser.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import java.io.File
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.desynatuser.R

class StoryMediaPicker(
    private val fragment: Fragment,
    private val imageCameraLauncher: ActivityResultLauncher<Uri>,
    private val imageGalleryLauncher: ActivityResultLauncher<String>,
    private val callback: (Uri?, String) -> Unit
) {

    private var cameraUri: Uri? = null
    private enum class PendingAction {
        IMAGE_CAMERA,
        VIDEO_CAMERA,
        NONE
    }

    private var pendingAction = PendingAction.NONE


    init {
        fragment.parentFragmentManager.setFragmentResultListener(
            "camera_result",
            fragment.viewLifecycleOwner
        ) { _, bundle ->
            val uriString = bundle.getString("image_uri")
            Log.e("capture", uriString.toString() )

            uriString?.let {
                callback(Uri.parse(it), "image")
            }
        }
    }

    fun showImagePicker() {
        val options = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(fragment.requireActivity())
            .setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                //    0 -> openCameraImage()
                    0 -> openInAppCamera()
                    1 -> openGalleryImage()
                }
            }.show()
    }
    private fun openCameraImage() {
        cameraUri = createUri("IMG", "jpg")

        imageCameraLauncher.launch(cameraUri!!)
    }

    private fun openGalleryImage() {
        imageGalleryLauncher.launch("image/*")
    }

    private fun openInAppCamera() {
        fragment.findNavController()
            .navigate(R.id.inAppCameraFragment)
    }


    fun onImageCameraResult(success: Boolean) {
        if (success) callback(cameraUri, "image")
        else callback(null, "")
        cameraUri = null
    }

    fun onImageGalleryResult(uri: Uri?) {
        callback(uri, "image")
    }

    private fun createUri(prefix: String, ext: String): Uri {
        val file = File(fragment.requireActivity().cacheDir, "${prefix}_${System.currentTimeMillis()}.$ext")
        return FileProvider.getUriForFile(
            fragment.requireActivity(),
            "${fragment.requireActivity().packageName}.provider",
            file
        )
    }
}

