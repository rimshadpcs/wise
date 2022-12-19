package com.intractable.simm.utils

import android.content.Context
import android.graphics.Paint
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import androidx.core.content.ContextCompat
import com.intractable.simm.R

object TextUtil {

    fun updateTextWithIcons(input: CharSequence, context: Context, fontMetrics: Paint.FontMetrics) : CharSequence {
        val matchResult = Regex("#[a-z]+ ").find(input)
        if (matchResult == null) {
            return input
        }
        else {
            var icon = when {
                matchResult.value.startsWith("#lock") -> R.drawable.lock
                matchResult.value.startsWith("#play") -> R.drawable.play_icon
                matchResult.value.startsWith("#checkmark") -> R.drawable.tick_icon
                matchResult.value.startsWith("#dash") -> R.drawable.dash_icon
                else -> R.drawable.lock
            }

            val descriptionText = SpannableString(input)
            val lockEmoji = ContextCompat.getDrawable(context, icon)
            val lockHeight = ((-1 * fontMetrics.ascent + fontMetrics.descent) * 0.8).toInt()
            val lockWidth = ((lockEmoji!!.intrinsicWidth.toFloat() / lockEmoji!!.intrinsicHeight.toFloat()) * lockHeight).toInt()
            lockEmoji!!.setBounds(0, 0, lockWidth, lockHeight)

            val span = ImageSpan(lockEmoji, ImageSpan.ALIGN_CENTER)
            descriptionText.setSpan(span, matchResult.range.start, matchResult.range.endInclusive, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

            return descriptionText
        }
    }

}