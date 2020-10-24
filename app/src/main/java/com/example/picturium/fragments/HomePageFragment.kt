package com.example.picturium.fragments

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.picturium.R
import com.example.picturium.SearchBarQueryListener
import com.example.picturium.User
import com.example.picturium.adapters.GalleryAdapter
import com.example.picturium.models.Submission
import com.example.picturium.viewmodels.GalleryFilterViewModel
import kotlinx.android.synthetic.main.fragment_home_page.*

class HomePageFragment : Fragment(R.layout.fragment_home_page), GalleryAdapter.OnItemClickListener,
    SearchBarQueryListener.OnTextSubmitListener {
    private val viewModel by viewModels<GalleryFilterViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = GalleryAdapter(this)

        home_svSearchBar.setOnQueryTextListener(SearchBarQueryListener(home_svSearchBar, this))
        home_ibProfile.setOnClickListener { _profileBtnOnClick() }
        home_ibUpload.setOnClickListener { _uploadBtnOnClick() }

        for (it in home_rgFilters) {
            if (it !is RadioButton)
                break
            it.setOnCheckedChangeListener { _, isChecked -> _filtersOnCheckedChange(it, isChecked) }
        }

        gallery_recyclerView.adapter = adapter
        gallery_recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        viewModel.submissions.observe(viewLifecycleOwner, {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    override fun onResume() {
        super.onResume()
        _setProfileBtnImage()
    }

    override fun onItemClick(submission: Submission) {
        val action = HomePageFragmentDirections.actionHomeFragmentToDetailsPageFragment(submission)

        findNavController().navigate(action)
    }

    override fun onTextSubmit(query: String) {
        val action = HomePageFragmentDirections.actionHomeFragmentToSearchPageFragment(query)

        home_svSearchBar.setQuery("", false)
        findNavController().navigate(action)
    }

    private fun _setProfileBtnImage() {
        Glide.with(requireContext())
            .load(User.publicData?.profilePicture)
            .fallback(R.drawable.ic_dflt_profile)
            .circleCrop()
            .into(home_ibProfile)
    }

    private fun _profileBtnOnClick() {
        val action = HomePageFragmentDirections.actionHomeFragmentToProfilePageFragment()

        findNavController().navigate(action)
    }

    private fun _uploadBtnOnClick() {
        Toast.makeText(context, "Upload", Toast.LENGTH_LONG).show()
    }

    private fun _filtersOnCheckedChange(filterBtn: RadioButton, isChecked: Boolean) {
        if (filterBtn.isPressed) {
            viewModel.setSection(filterBtn.tag as String)
        }
    }
}