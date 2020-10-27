package com.example.picturium.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.picturium.Picturium
import com.example.picturium.R
import com.example.picturium.api.ImgurAPI
import com.example.picturium.models.Submission
import com.example.picturium.viewmodels.DetailsPageViewModel
import kotlinx.android.synthetic.main.fragment_details_page.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsPageFragment : Fragment(R.layout.fragment_details_page) {

    private val _viewModel = DetailsPageViewModel()
    private lateinit var _submissionId: String
    private lateinit var _submission: Submission
    private lateinit var _latestVote: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _submissionId = navArgs<DetailsPageFragmentArgs>().value.submissionId

        details_ibReturn.setOnClickListener { _returnBtnOnClick() }
        details_btnRetry.setOnClickListener { _retryBtnOnClick() }
        details_ibShare.setOnClickListener { _shareBtnOnClick() }

        _viewModel.isLoading.observe(viewLifecycleOwner) {
            details_pbLoading.isVisible = it
        }
        _viewModel.isLoadingError.observe(viewLifecycleOwner) {
            details_btnRetry.visibility = if (it) View.VISIBLE else View.GONE
        }
        _viewModel.submission.observe(viewLifecycleOwner) {
            _submission = it
            _latestVote = it.vote ?: "veto"
            _setDetailsPage()
        }
        details_btnRetry.callOnClick()
    }

    private fun _setDetailsPage() {
        details_clSubmission.visibility = View.VISIBLE
        details_tvTitle.text = _submission.title
        details_tvDescription.text = _submission.description
        details_cbDownvote.text = (_submission.downVotes ?: 0).toString()
        details_cbUpvote.text = (_submission.upVotes ?: 0).toString()
        details_cbUpvote.isChecked = _submission.vote == "up"
        details_cbDownvote.isChecked = _submission.vote == "down"
        details_cbFavorite.isChecked = _submission.isFavorite ?: false

        details_cbUpvote.setOnCheckedChangeListener { _, isChecked -> _voteBtnOnCheckedChange(details_cbUpvote, isChecked, details_cbDownvote, "up") }
        details_cbDownvote.setOnCheckedChangeListener { _, isChecked -> _voteBtnOnCheckedChange(details_cbDownvote, isChecked, details_cbUpvote, "down") }
        details_cbFavorite.setOnCheckedChangeListener { btn, isChecked -> _favoriteBtnOnCheckedChange(btn, isChecked) }
    }

    private fun _returnBtnOnClick() {
        requireActivity().onBackPressed();
    }

    private fun _retryBtnOnClick() {
        _viewModel.loadSubmission(_submissionId)
    }

    private fun _favoriteBtnOnCheckedChange(btn: CompoundButton, isChecked: Boolean) {
        if (!btn.isPressed)
            return
        lifecycleScope.launch(Dispatchers.IO) {
            val res: ImgurAPI.CallResult<String?> = if (_submission.isAlbum) {
                ImgurAPI.safeCall {
                    ImgurAPI.instance.toggleFavoriteAlbum(_submissionId)
                }
            } else {
                ImgurAPI.safeCall {
                    ImgurAPI.instance.toggleFavoriteImage(_submissionId)
                }
            }
            withContext(Dispatchers.Main) {
                if (res !is ImgurAPI.CallResult.SuccessResponse) {
                    btn.isChecked = false
                    if (res is ImgurAPI.CallResult.NetworkError)
                        Picturium.toastConnectionError()
                }
            }
        }
    }

    private fun _voteBtnOnCheckedChange(btn: CompoundButton, isChecked: Boolean, otherBtn: CompoundButton, voteType: String) {
        if (!btn.isPressed)
            return
        if (otherBtn.isChecked)
            _toggleVoteBtn(otherBtn)

        val vote: String
        if (isChecked) {
            vote = voteType
            btn.text = (btn.text.toString().toInt() + 1).toString()
        } else {
            vote = "veto"
            btn.text = (btn.text.toString().toInt() - 1).toString()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val res = ImgurAPI.safeCall {
                ImgurAPI.instance.voteOnSubmission(_submissionId, vote)
            }
            if (res is ImgurAPI.CallResult.SuccessResponse) {
                _latestVote = vote
            } else {
                withContext(Dispatchers.Main) {
                    _toggleVoteBtn(btn)
                    if (_latestVote != "veto")
                        _toggleVoteBtn(otherBtn)
                    if (res is ImgurAPI.CallResult.NetworkError)
                        Picturium.toastConnectionError()
                }
            }
        }
    }

    private fun _shareBtnOnClick() {
        val content = _viewModel.submission.value?.title + System.lineSeparator() + _viewModel.submission.value?.link
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun _toggleVoteBtn(btn: CompoundButton) {
        val voteCount = btn.text.toString().toInt()
        if (btn.isChecked) {
            btn.isChecked = false
            btn.text = (voteCount - 1).toString()
        } else {
            btn.isChecked = true
            btn.text = (voteCount + 1).toString()
        }
    }
}