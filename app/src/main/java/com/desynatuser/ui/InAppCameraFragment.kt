package com.desynatuser.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class InAppCameraFragment : Fragment() {

    private lateinit var previewView: PreviewView
    private lateinit var tvToolbar: AppCompatTextView
    private var imageCapture: ImageCapture? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_in_app_camera, container, false)
        previewView = view.findViewById(R.id.previewView)
        tvToolbar = view.findViewById(R.id.tvToolbar)

        view.findViewById<RelativeLayout>(R.id.btnCapture).setOnClickListener {
            takePhoto()
        }

        startCamera()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvToolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {

        val imageCapture = imageCapture ?: return

        val file = File(
            requireContext().cacheDir,
            "IMG_${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                    val uri = Uri.fromFile(file)

                    parentFragmentManager.setFragmentResult(
                        "camera_result",
                        bundleOf("image_uri" to uri.toString())
                    )

                    findNavController().popBackStack()
                }

                override fun onError(exception: ImageCaptureException) {
                 //   Toast.makeText(requireContext(), "Capture failed", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}