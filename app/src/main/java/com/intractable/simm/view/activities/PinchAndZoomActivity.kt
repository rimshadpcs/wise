package com.intractable.simm.view.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.intractable.simm.R
import com.intractable.simm.databinding.ActivityPinchAndZoomBinding
import kotlinx.coroutines.*
import kotlin.math.max
import kotlin.math.min

@SuppressLint("ClickableViewAccessibility")
class PinchAndZoomActivity : AppCompatActivity() {

    private lateinit var pinchAndZoomBinding: ActivityPinchAndZoomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pinchAndZoomBinding = ActivityPinchAndZoomBinding.inflate(layoutInflater)
        setContentView(pinchAndZoomBinding.root)
        val insideImage = pinchAndZoomBinding.ivStarInside
        var scaleFactor = 1f

        val scaleGestureDetector = ScaleGestureDetector(
            this,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener(){
                override fun onScale(detector: ScaleGestureDetector?): Boolean {
                    scaleFactor *= detector!!.scaleFactor
                    scaleFactor = scaleFactor.coerceIn(0.3f,5.0f)

                    insideImage.scaleX = scaleFactor
                    insideImage.scaleY = scaleFactor

                    scaleFactor = max(1.0f, min(scaleFactor,1.0f))

                    if (insideImage.scaleX == 1.0f && insideImage.scaleY==1.0f){
                        showPopUpDialog(R.layout.right_dialog)
                        return false
                    }
                    return true

                }
            }
        )
        insideImage.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
        }

    }
    private fun showPopUpDialog(resId: Int) {
        val view = View.inflate(this, resId, null)
        val builder = AlertDialog.Builder(this@PinchAndZoomActivity)
        builder.setView(view)

        val dialog = builder.create()
        CoroutineScope(Dispatchers.Main).launch {
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            delay(500)
            dialog.show()
            delay(1000)
            dialog.dismiss()
        }
    }
}
