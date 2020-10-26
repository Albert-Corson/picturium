package com.example.picturium.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.picturium.R
import com.example.picturium.viewmodels.DetailsPageViewModel
import kotlinx.android.synthetic.main.fragment_details_page.*

class DetailsPageFragment : Fragment(R.layout.fragment_details_page) {

    private lateinit var _submissionId: String
    private val _viewModel = DetailsPageViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _submissionId = navArgs<DetailsPageFragmentArgs>().value.submissionId

        details_ibReturn.setOnClickListener { _returnBtnOnClick() }
        details_btnRetry.setOnClickListener { _retryBtnOnClick() }

        _viewModel.isLoading.observe(viewLifecycleOwner) {
            details_pbLoading.isVisible = it
        }
        _viewModel.isLoadingError.observe(viewLifecycleOwner) {
            details_btnRetry.visibility = if (it) View.VISIBLE else View.GONE
        }
        _viewModel.submission.observe(viewLifecycleOwner) {
            details_clSubmission.visibility = View.VISIBLE
            details_tvTitle.text = it.title
            details_tvDescription.text = it.description
            details_cbDownvote.text = (it.downVotes ?: 0).toString()
            details_cbUpvote.text = (it.upVotes ?: 0).toString()
        }
        details_btnRetry.callOnClick()
    }

    private fun _returnBtnOnClick() {
        requireActivity().onBackPressed();
    }

    private fun _retryBtnOnClick() {
        _viewModel.loadSubmission(_submissionId)
    }
}