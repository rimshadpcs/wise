package com.intractable.simm.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler
import androidx.webkit.WebViewClientCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R
import com.intractable.simm.databinding.FragmentComicBinding
import com.intractable.simm.model.ComicDetails
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel


class FragmentComic : Fragment(),RightBottomSheetDialog.RightBottomSheetListener,
    WrongBottomSheetDialog.WrongBottomSheetListener ,View.OnClickListener {

    private val TAG = "FragmentComic"
    private lateinit var viewBinding: FragmentComicBinding
    private val viewModel: SessionViewModel by activityViewModels()
    private var currentQuestion = 0
    lateinit var mp : MediaPlayer
    private var fragmentCompletedSuccessfully = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        mp = MediaPlayer.create(requireContext(), R.raw.soft_notification)
        enterTransition = inflater.inflateTransition(R.transition.transition_right)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentComicBinding.inflate(inflater, container, false)
        val view = viewBinding.root

        val lifecycleOwner: LifecycleOwner = this

        viewModel.comicData.observe(context as SessionActivity) {
            loadWebView(it.sceneOption, it.sessionOption, it.comicDetails, lifecycleOwner)
            Firebase.analytics.logEvent("simm_comic_started", null)
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

        viewBinding.btComplete.setOnClickListener{
            viewBinding.vWebViewCover.visibility = View.VISIBLE

            viewBinding.btComplete.visibility = View.INVISIBLE
            viewBinding.btCompletes.visibility = View.VISIBLE

            (viewBinding.wvWebView.parent as ViewGroup).removeView(viewBinding.wvWebView)
            viewBinding.wvWebView.removeAllViews()
            viewBinding.wvWebView.destroy()

            viewModel.checkForNextQuestion(fragmentCompletedSuccessfully)

            Firebase.analytics.logEvent("simm_comic_completed", null)
        }

        Firebase.analytics.logEvent("simm_comic_started", null)

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FragmentComic().apply {
                arguments = Bundle().apply {
                }
            }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView(sceneOption: Int, shapeType: Int, comicDetails: MutableList<ComicDetails>, lifecycleOwner: LifecycleOwner){

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", AssetsPathHandler(this.requireContext()))
            .build()

        viewBinding.wvWebView.webViewClient = object : WebViewClientCompat() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }

            // for API < 21
            @Deprecated("Deprecated in Java",
                ReplaceWith("assetLoader.shouldInterceptRequest(Uri.parse(url))")
            )
            override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(Uri.parse(url))
            }
        }

        val webViewSettings: WebSettings = viewBinding.wvWebView.settings
        // Keeping these off is less critical but still a good idea, especially if your app is not
        // using file:// or content:// URLs.
        webViewSettings.allowFileAccess = false
        webViewSettings.allowContentAccess = false

        webViewSettings.javaScriptEnabled = true


        viewBinding.wvWebView.webChromeClient = object: WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                if("sendToAndroid" in consoleMessage.message()) {
                    val msg = consoleMessage.message().split("&")
                    Log.i("WebViewConsoleMessages", msg[1])
                    if(msg[1] == "game won" || msg[1].endsWith("ActivityComplete") || msg[1].contains("FinalPage") || msg[1].contains("ResultsPage")){
                        viewBinding.btComplete.visibility = View.VISIBLE
                        viewModel.changeProgress()
                    }
                    else if(msg[1].contains("GameFailed")){
                        fragmentCompletedSuccessfully = false
                    }
                    else if(msg[1].contains("PreviousPages")){
                        viewBinding.btComplete.visibility = View.INVISIBLE
                    }
                    else if(msg[1] == "Startup Complete"){
                        if(sceneOption == 3) {
                            viewModel.userInputName.observe(lifecycleOwner) {
                                var comicString = turnComicDataToString(comicDetails)
                                var userName = it ?: ""
                                if (userName != "") {
                                    userName = " $userName"
                                }
                                comicString = comicString.replace("%user_name", userName)
                                // need to use escape characters
                                for (i in comicString.length - 1 downTo 0) {
                                    if (comicString[i] == '\'') {
                                        comicString = comicString.replaceRange(i, i, "\\")
                                    }
                                }

                                viewBinding.wvWebView.loadUrl("javascript:init('$comicString')")
                            }
                        }
                        else{
                            viewBinding.vWebViewCover.visibility = View.INVISIBLE
                        }
                    }
                    else if(msg[1].contains("ComicLoaded")){
                        viewBinding.vWebViewCover.visibility = View.INVISIBLE
                    }
                    else if(msg[1].contains("SectionCompleted")){
                        // play sound effect
                        if(mp.isPlaying) {
                            mp.pause() // Pause the current track
                        }
                        mp.release()
                        mp = MediaPlayer.create(context, R.raw.minigame_success)
                        mp.start()  // play sound
                    }
                    else if(msg[1].contains("SectionFailed")){
                        // play sound effect
                        if(mp.isPlaying) {
                            // if it just played a success sound, don't play a fail sound? only applies to complex shape?
                        }
                        else{
                            mp.release()
                            mp = MediaPlayer.create(context, R.raw.minigame_fail)
                            mp.start()  // play sound
                        }
                    }
                    else if(msg[1].contains("AllCheckpoints") || msg[1].contains("ActivityComplete") || msg[1].contains("FinalPage")){
                        // haptics
                        val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val vibratorManager =
                                context?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                            vibratorManager.defaultVibrator
                        } else {
                            @Suppress("DEPRECATION")
                            context?.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
                        }

                        // this type of vibration requires API 29
                        val vibrationEffect: VibrationEffect
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val singleClickPattern = longArrayOf(0, 10)
                            val singleClickAmplitude = intArrayOf(0, 180)

                            vibrationEffect = VibrationEffect.createWaveform(singleClickPattern, singleClickAmplitude, -1)

                            // it is safe to cancel other vibrations currently taking place
                            vib.cancel()
                            vib.vibrate(vibrationEffect)
                        }
                    }

                    Log.i(TAG, "state ${msg[1]}")
                    viewModel.updateState(msg[1])
                }
                Log.i(TAG, "console message: ${consoleMessage.message()}")
                return true
            }
        }

        // Assets are hosted under http(s)://appassets.androidplatform.net/assets/... .
        // If the application's assets are in the "main/assets" folder this will read the file
        // from "main/assets/www/index.html" and load it as if it were hosted on:
        // https://appassets.androidplatform.net/assets/www/index.html
        // 0 = trace shape, 1 = pinch & zoom, 2 = Gesture Navigation, 3 = comic,
        // 4 = Gesture home screen, 5 = Gesture previous screen
        val userId = sceneOption
        val sessionId = shapeType  // 0 = square, 1 = triangle, 2 = circle, 3 = flower
        val unityURL = "https://appassets.androidplatform.net/assets/testWebsite/index.html?user_id=$userId&session_id=$sessionId"

        viewBinding.wvWebView.loadUrl(unityURL)

    }

    fun turnComicDataToString(comicDetails: MutableList<ComicDetails>): String {
        val comicSplit = "±"
        val pageSplit = "§"
        val imageSplit = "¶"
        val dataSplit = "∞"

        var outputString = ""
        outputString += "ComicInput$comicSplit"

        for(i in 0 until comicDetails.size){  // for every page
            val pageData = comicDetails[i]
            if(i != 0) {
                outputString += pageSplit
            }
            outputString += pageData.background

            for(j in 0 until pageData.comicSpeechBubbles.size){  // for every speech bubble
                outputString += imageSplit

                val bubbleData = pageData.comicSpeechBubbles[j]
                outputString += bubbleData.displayText + dataSplit
                outputString += bubbleData.fontSize + dataSplit
                outputString += bubbleData.textPosition + dataSplit
                outputString += bubbleData.textBoxSize + dataSplit
                outputString += bubbleData.textAlignment + dataSplit
                outputString += bubbleData.bubbleImage + dataSplit
                outputString += bubbleData.bubblePosition + dataSplit
                outputString += bubbleData.bubbleSize + dataSplit
                outputString += bubbleData.pageNumber
            }
        }
        Log.d("FragmentComic", "Processed String: $outputString")

        return outputString
    }

    override fun onRightButtonClicked(text: String?) {
        viewModel.checkForNextQuestion(true)
    }

    override fun onWrongButtonClicked(text: String?) {
        viewModel.checkForNextQuestion(true)
    }

    override fun onClick(v: View?) {

    }
}