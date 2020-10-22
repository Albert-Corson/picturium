package com.example.picturium.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.picturium.R
import kotlinx.android.synthetic.main.fragment_details_page.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsPageFragment : Fragment(R.layout.fragment_details_page) {

    private val args: DetailsPageFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topBar_returnBtn.setOnClickListener { returnBtnOnClick() }

        lifecycleScope.launch {
            val url: String? = args.submission.getCoverImage()?.link
            withContext(Dispatchers.Main) {
                Glide.with(this@DetailsPageFragment)
                    .load(url)
                    .error(R.drawable.error)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            detail_progressBar.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            detail_progressBar.isVisible = false
                            text_view_title.isVisible = true
                            text_view_description.isVisible = true
                            linear_button_action.isVisible = true
                            return false
                        }

                    })
                    .into(image_view)
            }
        }
        text_view_title.text = args.submission.title
    }

    private fun returnBtnOnClick() {
        findNavController().navigateUp()
    }
}