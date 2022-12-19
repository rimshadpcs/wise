package com.intractable.simm.view.fragments


import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.camera.core.*
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider

import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R
import com.intractable.simm.databinding.FragmentCameraBinding
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FragmentCamera : Fragment() {

    private lateinit var viewBinding: FragmentCameraBinding

    private var imageCapture: ImageCapture? = null

    private val viewModel: SessionViewModel by activityViewModels()

    private lateinit var cameraExecutor: ExecutorService

    var flag = 0

    val eventPrefix: String
        get() {
            return "simm_" + this.javaClass.simpleName
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentCameraBinding.inflate(inflater, container, false)
        val view = viewBinding.root

        viewModel.startOperationData()

        viewModel.getEvent().observe(context as SessionActivity){
            when (it) {
                SessionViewModel.EVENT_START_CAMERA -> {
                    startCamera()
                    viewModel.eventlivedata.value = SessionViewModel.EVENT_NONE
                }
                SessionViewModel.EVENT_CAPTURE_IMAGE -> {
                    if (flag == 1){
                        flag = 2
                        takePhoto()
                    }
                }
                SessionViewModel.EVENT_SHOW_SKIP_BUTTON -> showSkipButton()
            }
        }

        viewModel.checkCameraPermission()

        viewModel.operationDataLiveData.observe(context as SessionActivity){
            if (it != null){
                viewBinding.tvTxt.text = it.text
                viewBinding.ivCharacter.setImageResource(it.img)
                viewBinding.tvTxt2.text = it.text
            }
        }

        viewBinding.btContinue.setOnTouchListener { _, motionEvent: MotionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    viewBinding.btContinue.setBackgroundResource(R.drawable.no_shadow_bg_green)
                    val params =
                        viewBinding.btContinue.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin - 15
                    viewBinding.btContinue.layoutParams = params
                }

                MotionEvent.ACTION_UP -> {
                    viewBinding.btContinue.setBackgroundResource(R.drawable.shadow_button_bg)
                    val params =
                        viewBinding.btContinue.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin + 15
                    viewBinding.btContinue.layoutParams = params
                }
            }
            false
        }


        viewBinding.btContinue.setOnClickListener {
            viewModel.nextOperationPage()
            viewBinding.layOut1.visibility = View.GONE
            viewBinding.layout2.visibility = View.VISIBLE
            val animationFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
            //starting the animation
            viewBinding.tvTxt2.startAnimation(animationFadeIn)
                lifecycleScope.launch {
                    delay(2000)
                    viewBinding.btComplete.visibility = View.VISIBLE
                }
        }

        viewBinding.btSkip.setOnTouchListener { _, motionEvent: MotionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    viewBinding.btSkip.setBackgroundResource(R.drawable.no_shadow_blue_button)
                    val params =
                        viewBinding.btSkip.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin - 15
                    viewBinding.btSkip.layoutParams = params
                }

                MotionEvent.ACTION_UP -> {
                    viewBinding.btSkip.setBackgroundResource(R.drawable.shadow_button_blue)
                    val params =
                        viewBinding.btSkip.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin + 15
                    viewBinding.btSkip.layoutParams = params
                }
            }
            false
        }

        viewBinding.btSkip.setOnClickListener {
            viewModel.checkForNextQuestion(false)
        }

        viewBinding.btComplete.setOnTouchListener { _, motionEvent: MotionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    viewBinding.btComplete.setBackgroundResource(R.drawable.no_shadow_bg_green)
                    val params =
                        viewBinding.btComplete.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin - 15
                    viewBinding.btComplete.layoutParams = params
                }

                MotionEvent.ACTION_UP -> {
                    viewBinding.btComplete.setBackgroundResource(R.drawable.shadow_button_bg)
                    val params =
                        viewBinding.btComplete.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin + 15
                    viewBinding.btComplete.layoutParams = params
                }
            }
            false
        }



        viewBinding.btComplete.setOnClickListener {
            if (flag == 0) {
                flag = 1
                setExercise2()
            }
            else{
                Firebase.analytics.logEvent(eventPrefix + "_completed", null)
                viewModel.checkForNextQuestion(true)
                viewModel.changeProgress()
            }
        }

        viewBinding.ivPress.setOnClickListener {
            takePhoto()
            viewBinding.ivPress.isClickable = false
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        Firebase.analytics.logEvent(eventPrefix + "_started", null)

        return view
    }

    private fun showSkipButton() {
        viewBinding.tvTxt2.text = viewModel.skipTextFlow
        viewBinding.btSkip.visibility = View.VISIBLE
        viewBinding.layout2.visibility = View.VISIBLE
        viewBinding.layOut1.visibility = View.GONE
        viewModel.eventlivedata.value = SessionViewModel.EVENT_NONE
    }

    private fun setExercise2() {
        viewModel.nextOperationPage()
        viewBinding.btComplete.visibility = View.INVISIBLE
        viewBinding.ivPress.visibility = View.GONE
        viewBinding.ivCapturedImage.visibility = View.GONE
        viewBinding.preview.visibility = View.VISIBLE
        viewBinding.layout2.visibility = View.GONE
        viewBinding.layOut1.visibility = View.VISIBLE
        viewBinding.btContinue.visibility = View.INVISIBLE
    }



    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()


            // Preview
            val preview = Preview.Builder()
                .build()
                .also {

                    it.setSurfaceProvider(viewBinding.preview.surfaceProvider)
                }



            imageCapture = ImageCapture.Builder()
                .build()

//            val imageAnalyzer = ImageAnalysis.Builder()
//                .build()
//                .also {
//                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
//                        Log.d(TAG, "Average luminosity: $luma")
//                    })
//                }


            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

//        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

//        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()



        imageCapture.takePicture(outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    viewModel.nextOperationPage()
                    var uri = output.savedUri
                    val bitmap = getBitmap(requireActivity().contentResolver,uri)
                    viewBinding.ivCapturedImage.setImageBitmap(bitmap)
                    viewBinding.preview.visibility = View.GONE
                    viewBinding.ivCapturedImage.visibility = View.VISIBLE
                    viewBinding.btContinue.visibility = View.VISIBLE

                }
            }
        )
    }

    fun getBitmap(contentResolver: ContentResolver, fileUri: Uri?): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, fileUri!!))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
            }
        } catch (e: Exception){
            null
        }
    }


    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

}