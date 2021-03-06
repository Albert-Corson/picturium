package com.example.picturium.fragments

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.picturium.R
import com.example.picturium.SearchBarQueryListener
import com.example.picturium.adapters.GalleryAdapter
import com.example.picturium.models.Submission
import com.example.picturium.viewmodels.GalleryFilterViewModel
import com.example.picturium.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomePageFragment : Fragment(R.layout.fragment_home_page), GalleryAdapter.OnItemClickListener,
    SearchBarQueryListener.OnTextSubmitListener {
    private val viewModel by viewModels<GalleryFilterViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = GalleryAdapter(this, viewLifecycleOwner.lifecycleScope)

        home_svSearchBar.setOnQueryTextListener(SearchBarQueryListener(home_svSearchBar, this))
        home_ibProfile.setOnClickListener { _profileBtnOnClick() }
        home_ibUpload.setOnClickListener { _uploadBtnOnClick() }

        for (it in home_rgFilters) {
            if (it !is RadioButton)
                break
            it.setOnCheckedChangeListener { _, isChecked -> _filtersOnCheckedChange(it, isChecked) }
        }

        val rvManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        home_rvGallery.adapter = adapter
        home_rvGallery.layoutManager = rvManager

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                home_rvGallery.isVisible = loadStates.refresh !is LoadState.Loading
                home_srlSwipeRefresh.isRefreshing = loadStates.refresh is LoadState.Loading
                home_tvRefresh.isVisible = loadStates.refresh !is LoadState.Loading && adapter.itemCount == 0
            }
        }

        viewModel.submissions.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        UserViewModel.publicData.observe(viewLifecycleOwner) {
            _setProfileBtnImage(it?.profilePicture)
        }
        home_srlSwipeRefresh.setProgressBackgroundColorSchemeResource(R.color.translucent)
        home_srlSwipeRefresh.setColorSchemeResources(R.color.primaryAccent)
        home_srlSwipeRefresh.setOnRefreshListener {
            adapter.refresh()
            UserViewModel.refresh()
        }
    }

    override fun onItemClick(submission: Submission) {
        val action = HomePageFragmentDirections.actionHomeFragmentToDetailsPageFragment(submission.id ?: "")

        findNavController().navigate(action)
    }

    override fun onTextSubmit(query: String) {
        val action = HomePageFragmentDirections.actionHomeFragmentToSearchPageFragment(query)

        home_svSearchBar.setQuery("", false)
        findNavController().navigate(action)
    }

    private fun _setProfileBtnImage(profilePicture: String?) {
        Glide.with(requireContext())
            .load(profilePicture)
            .fallback(R.drawable.ic_dflt_profile)
            .circleCrop()
            .into(home_ibProfile)
    }

    private fun _profileBtnOnClick() {
        val action = HomePageFragmentDirections.actionHomeFragmentToProfilePageFragment()

        findNavController().navigate(action)
    }

    private fun _uploadBtnOnClick() {
        val action = HomePageFragmentDirections.actionHomeFragmentToUploadPageFragment()

        findNavController().navigate(action)
    }

    private fun _filtersOnCheckedChange(filterBtn: RadioButton, isChecked: Boolean) {
        if (filterBtn.isPressed) {
            viewModel.setSection(filterBtn.tag as String)
        }
    }
}