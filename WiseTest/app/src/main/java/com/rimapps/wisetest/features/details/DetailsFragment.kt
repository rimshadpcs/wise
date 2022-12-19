package com.rimapps.wisetest.features.details

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.rimapps.wisetest.R
import com.rimapps.wisetest.databinding.FragmentDetailsBinding


class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val args by navArgs<DetailsFragmentArgs>()
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding  = FragmentDetailsBinding.bind(view)

        binding.apply {
            val news = args.news
            Glide.with(this@DetailsFragment)
                .load(news.thumbnailUrl)
                .error(R.drawable.error)
                .into(ivDetail)

            tvHeadline.text = news.title
            val uri = Uri.parse(news.url)
            val intent  = Intent(Intent.ACTION_VIEW,uri)

            tvUrl.apply {
                text = "read more at ${news.url}"
                setOnClickListener {
                    context.startActivity(intent)
                }
                paint.isUnderlineText = true
            }


        }
    }
}