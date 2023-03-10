package com.intractable.simm.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.transition.TransitionInflater
import android.util.Log
import android.view.*
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler
import androidx.webkit.WebViewClientCompat
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R
import com.intractable.simm.databinding.FragmentKeypadBinding
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel


class FragmentKeypad : Fragment(),RightBottomSheetDialog.RightBottomSheetListener,
    WrongBottomSheetDialog.WrongBottomSheetListener ,View.OnClickListener {

    private val TAG = "FragmentKeypad"
    private lateinit var viewBinding: FragmentKeypadBinding
    private val viewModel: SessionViewModel by activityViewModels()
    private var currentQuestion = 0
    lateinit var mp : MediaPlayer

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
        viewBinding = FragmentKeypadBinding.inflate(inflater, container, false)
        val view = viewBinding.root

        viewModel.htmlData.observe(context as SessionActivity) {
            viewBinding.tvQuestion.text = it.text
            loadWebView(it.sceneOption, it.sessionOption)
            Firebase.analytics.logEvent("simm_html_started", null)
        }

        viewModel.hintFlow.observe(context as SessionActivity) {
            if (it != null) {
                viewBinding.tvQuestion.text = it
            }
        }

        viewBinding.btComplete.setOnClickListener{
            viewBinding.vWebViewCover.visibility = View.VISIBLE


            (viewBinding.wvWebView.parent as ViewGroup).removeView(viewBinding.wvWebView)
            viewBinding.wvWebView.removeAllViews()
            viewBinding.wvWebView.destroy()

            viewModel.checkForNextQuestion(true)

            Firebase.analytics.logEvent("simm_html_completed", null)
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


        viewBinding.etTextInput.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    val inputText = viewBinding.etTextInput.text.toString().filter { !it.isWhitespace() }

                    viewBinding.wvWebView.loadUrl("javascript:init('KeypadInput??$inputText')")

                    return true
                }
                return false

            }
        })




        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FragmentKeypad().apply {
                arguments = Bundle().apply {
                }
            }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView(sceneOption: Int, shapeType: Int){

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
                Log.i(TAG, "console message: ${consoleMessage.message()}")
                if("sendToAndroid" in consoleMessage.message()) {
                    val msg = consoleMessage.message().split("&")
                    if(msg[1] == "game won" || msg[1].endsWith("ActivityComplete")){
                        viewBinding.btComplete.visibility = View.VISIBLE
                        viewModel.changeProgress()
                    }
                    else if(msg[1] == "Startup Complete"){
                        viewBinding.vWebViewCover.visibility = View.INVISIBLE
                    }
                    else if(msg[1].contains("AllCheckpoints") || msg[1].contains("ActivityComplete")){
                        vibrateCorrect()
                    }
                    else if(msg[1].contains("KeypadOutput")){
                        val numbersString = msg[1].split(":")[1].trim()
                        val numbers = mutableListOf<Int>()

                        for(element in numbersString){
                            Log.d("keypad output", element.toString())
                            numbers.add(element.code)
                        }
                    }
                    else if(msg[1] == "Keypad and word do not match"){
                        playHTMLWrongSound()
                        vibrateWrong()
                    }
                    else if(msg[1] == "Keypad and word match"){
                        playHTMLCorrectSound()
                        vibrateCorrect()
                    }

                    viewModel.updateState(msg[1])
                }
                return true
            }
        }

        // Assets are hosted under http(s)://appassets.androidplatform.net/assets/... .
        // If the application's assets are in the "main/assets" folder this will read the file
        // from "main/assets/www/index.html" and load it as if it were hosted on:
        // https://appassets.androidplatform.net/assets/www/index.html
        // 0 = trace shape, 1 = pinch & zoom, 2 = Gesture Navigation, 3 = comic, 4 = Gesture Home, 5 = Gesture Previous, 6 = Gesture Recent
        // 4 = Gesture home screen, 5 = Gesture previous screen, 6 = Gesture Recent, 7 = Ink Scene, 8 = Keypad
        val userId = sceneOption
        val sessionId = shapeType  // For Keypad error feedback: 0 = immediate feedback, 1 = feedback on submit, 2 = no feedback
        val unityURL = "https://appassets.androidplatform.net/assets/testWebsite/index.html?user_id=$userId&session_id=$sessionId"

        viewBinding.wvWebView.loadUrl(unityURL)

    }

    private fun playHTMLCorrectSound(){
        // play sound effect
        if(mp.isPlaying) {
            mp.pause() // Pause the current track
        }
        mp.release()
        mp = MediaPlayer.create(context, R.raw.minigame_success)
        mp.start()  // play sound
    }
    private fun playHTMLWrongSound(){
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

    private fun vibrateCorrect(){
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
            vib.cancel();
            vib.vibrate(vibrationEffect);
        }
    }
    private fun vibrateWrong(){
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
            val doubleClickPattern = longArrayOf(0, 10, 160, 10)
            val doubleClickAmplitude = intArrayOf(0, 255, 0, 255)

            vibrationEffect = VibrationEffect.createWaveform(doubleClickPattern, doubleClickAmplitude, -1)

            // it is safe to cancel other vibrations currently taking place
            vib.cancel();
            vib.vibrate(vibrationEffect);
        }
    }

//    private fun callRightBottomSheet(){
//        viewModel.current_question = currentQuestion
//        viewModel.eventlivedata.value = Constants.EVENT_SHOW_RIGHT_BOTTOMSHEET
//        viewModel.eventlivedata.value = Constants.EVENT_NONE
//    }
//    private fun callWrongBottomSheet(){
//        viewModel.current_question = currentQuestion
//        viewModel.eventlivedata.value = Constants.EVENT_SHOW_WRONG_BOTTOMSHEET
//        viewModel.eventlivedata.value = Constants.EVENT_NONE
//    }

    override fun onRightButtonClicked(text: String?) {
        viewModel.checkForNextQuestion(true)
    }

    override fun onWrongButtonClicked(text: String?) {
        viewModel.checkForNextQuestion(true)
    }

    override fun onClick(v: View?) {

    }
}