package com.intractable.simm.view.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import com.intractable.simm.databinding.ActivityOnboardingBinding
import com.intractable.simm.databinding.OnboardingComicBinding
import com.intractable.simm.model.ComicDetails
import com.intractable.simm.viewmodel.OnBoardingViewModel

class OnBoardingComicActivity : AppCompatActivity() {

    private var sharedPreferences: SharedPreferences?=null
    private lateinit var viewBinding: OnboardingComicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = OnboardingComicBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val viewModel = ViewModelProvider(this)[OnBoardingViewModel::class.java]

        val lifecycleOwner: LifecycleOwner = this
        viewModel.comicData.observe(this) {
            loadWebView(3, it.sessionOption, it.comicDetails, this, lifecycleOwner)
        }



        viewBinding.btContinueOnBoarding.setOnClickListener {
//            firebaseAnalytics.logEvent("simm_onboarding_completed", null)

            (viewBinding.wvWebView.parent as ViewGroup).removeView(viewBinding.wvWebView)
            viewBinding.wvWebView.removeAllViews()
            viewBinding.wvWebView.destroy()

            savePrefData()
            val i = Intent(applicationContext, HomeActivity::class.java)
            startActivity(i)
            finish()
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView(
        sceneOption: Int,
        shapeType: Int,
        comicDetails: MutableList<ComicDetails>,
        context: Context,
        lifecycleOwner: LifecycleOwner
    ) {
        val viewModel = ViewModelProvider(this)[OnBoardingViewModel::class.java]

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        viewBinding.wvWebView.webViewClient = object : WebViewClientCompat() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }

            // for API < 21
            @Deprecated(
                "Deprecated in Java",
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


        viewBinding.wvWebView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                if ("sendToAndroid" in consoleMessage.message()) {
                    val msg = consoleMessage.message().split("&")
                    Log.i("WebViewConsoleMessages", msg[1])
                    if (msg[1] == "game won" || msg[1].endsWith("ActivityComplete") || msg[1].contains("FinalPage")
                    ) {
                        viewBinding.btContinueOnBoarding.visibility = View.VISIBLE
                    }
                    else if(msg[1].contains("PreviousPages")){
                        viewBinding.btContinueOnBoarding.visibility = View.INVISIBLE
                    }
                    else if (msg[1] == "Startup Complete") {
                        viewModel.userInputName.observe(lifecycleOwner){
                            var comicString = turnComicDataToString(comicDetails)
                            var userName = it ?:""
                            if(userName != ""){
                                userName = " $userName"
                            }
                            comicString = comicString.replace("%user_name", userName)
                            // need to use escape characters
                            for(i in comicString.length - 1 downTo 0){
                                if (comicString[i] == '\''){
                                    comicString = comicString.replaceRange(i, i, "\\")
                                }
                            }

                            viewBinding.wvWebView.loadUrl("javascript:init('$comicString')")
                        }
                    } else if (msg[1].contains("ComicLoaded")) {
                        viewBinding.vWebViewCover.visibility = View.INVISIBLE
                    } else if (msg[1].contains("ActivityComplete") || msg[1].contains("FinalPage")) {
                        // haptics
                        val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val vibratorManager =
                                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                            vibratorManager.defaultVibrator
                        } else {
                            @Suppress("DEPRECATION")
                            context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
                        }

                        // this type of vibration requires API 29
                        val vibrationEffect: VibrationEffect
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val singleClickPattern = longArrayOf(0, 10)
                            val singleClickAmplitude = intArrayOf(0, 180)

                            vibrationEffect = VibrationEffect.createWaveform(
                                singleClickPattern,
                                singleClickAmplitude,
                                -1
                            )

                            // it is safe to cancel other vibrations currently taking place
                            vib.cancel()
                            vib.vibrate(vibrationEffect)
                        }
                    }

                    Log.i("Onboarding", "state ${msg[1]}")
                }
                Log.i("Onboarding", "console message: ${consoleMessage.message()}")
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
        val unityURL =
            "https://appassets.androidplatform.net/assets/testWebsite/index.html?user_id=$userId&session_id=$sessionId"

        viewBinding.wvWebView.loadUrl(unityURL)

    }

    fun turnComicDataToString(comicDetails: MutableList<ComicDetails>): String {
        val comicSplit = "±"
        val pageSplit = "§"
        val imageSplit = "¶"
        val dataSplit = "∞"

        var outputString = ""
        outputString += "ComicInput$comicSplit"

        for (i in 0 until comicDetails.size) {  // for every page
            val pageData = comicDetails[i]
            if (i != 0) {
                outputString += pageSplit
            }
            outputString += pageData.background

            for (j in 0 until pageData.comicSpeechBubbles.size) {  // for every speech bubble
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

    private fun savePrefData(){

        sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = sharedPreferences!!.edit()
        editor!!.putBoolean("isFirstTime", true)
        editor.apply()
    }
}