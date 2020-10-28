package com.example.picturium.fragments

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.core.os.bundleOf
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
import com.example.picturium.viewmodels.GallerySearchViewModel
import kotlinx.android.synthetic.main.fragment_search_page.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchPageFragment : Fragment(R.layout.fragment_search_page), GalleryAdapter.OnItemClickListener,
    SearchBarQueryListener.OnTextSubmitListener {
    private val viewModel by viewModels<GallerySearchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = GalleryAdapter(this, viewLifecycleOwner.lifecycleScope)

        viewModel.setWindow("all")

        search_btnReturn.setOnClickListener { _returnBtnOnClick() }
        search_svSearchBar.setOnQueryTextListener(SearchBarQueryListener(search_svSearchBar, this))
        search_svSearchBar.setQuery(arguments?.getString("query"), true)

        for (it in search_rgSortFilters) {
            if (it !is RadioButton)
                continue
            it.setOnCheckedChangeListener { _, isChecked -> _filtersOnCheckedChangeListener(it, isChecked) }
        }

        _setNoResultImage()
        search_rvGallery.adapter = adapter
        search_rvGallery.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                search_rgSortFilters.isVisible = adapter.itemCount != 0
                search_pgLoading.isVisible = loadStates.refresh is LoadState.Loading
                search_rvGallery.isVisible = loadStates.refresh !is LoadState.Loading
                search_ivNoResult.isVisible = loadStates.refresh !is LoadState.Loading && adapter.itemCount == 0
            }
        }
        viewModel.submissions.observe(viewLifecycleOwner, {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    private fun _setNoResultImage() {
        Glide.with(this)
            .load(R.drawable.no_result)
            .placeholder(R.drawable.no_result)
            .into(search_ivNoResult)
    }

    override fun onItemClick(submission: Submission) {
        val action = SearchPageFragmentDirections.actionSearchPageFragmentToDetailsPageFragment(submission.id ?: "")

        findNavController().navigate(action)
    }

    override fun onPause() {
        super.onPause()
        arguments = bundleOf(
            "query" to search_svSearchBar.query.toString()
        )
    }

    override fun onTextSubmit(query: String) {
        search_pgLoading.visibility = View.VISIBLE
        search_rvGallery.visibility = View.GONE
        viewModel.setQuery(query)
    }

    private fun _returnBtnOnClick() {
        findNavController().navigateUp()
    }

    private fun _filtersOnCheckedChangeListener(filterBtn: RadioButton, isChecked: Boolean) {
        if (filterBtn.isPressed)
            viewModel.setSort(filterBtn.tag as String)
    }
}