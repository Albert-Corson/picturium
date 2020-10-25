package com.example.picturium.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.picturium.R
import com.example.picturium.models.Submission
import kotlinx.android.synthetic.main.fragment_details_page.*

class DetailsPageFragment : Fragment(R.layout.fragment_details_page) {

    private lateinit var _submission: Submission

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _submission = navArgs<DetailsPageFragmentArgs>().value.submission

        details_ibReturn.setOnClickListener { _returnBtnOnClick() }

        details_tvTitle.text = _submission.title
        details_tvDescription.text = _submission.description
        details_tbDownvote.textOff = (_submission.downVotes ?: 0).toString()
        details_tbDownvote.textOn = ((_submission.downVotes ?: 0) + 1).toString()
        details_tbUpvote.textOff = (_submission.upVotes ?: 0).toString()
        details_tbUpvote.textOn = ((_submission.upVotes ?: 0) + 1).toString()
    }

    private fun _returnBtnOnClick() {
        requireActivity().onBackPressed();
    }
}