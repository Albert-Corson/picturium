package com.example.picturium.fragments

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.picturium.R
import com.example.picturium.SearchBarQueryListener
import com.example.picturium.adapters.GalleryAdapter
import com.example.picturium.models.Submission
import com.example.picturium.viewmodels.GallerySearchViewModel
import kotlinx.android.synthetic.main.fragment_search_page.*

class SearchPageFragment : Fragment(R.layout.fragment_search_page), GalleryAdapter.OnItemClickListener,
    SearchBarQueryListener.OnTextSubmitListener {
    private val viewModel by viewModels<GallerySearchViewModel>()
    private val args: SearchPageFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setQuery(args.query)
        viewModel.setWindow("all")

        search_btnReturn.setOnClickListener { _returnBtnOnClick() }
        search_svSearchBar.setOnQueryTextListener(SearchBarQueryListener(search_svSearchBar, this))
        search_svSearchBar.setQuery(args.query, false)

        for (it in search_rgSortFilters) {
            if (it !is RadioButton)
                continue
            it.setOnCheckedChangeListener { _, isChecked -> _filtersOnCheckedChangeListener(it, isChecked) }
        }

        _setNoResultImage()
        search_rvGallery.adapter = GalleryAdapter(emptyList(), this, lifecycleScope)
        search_rvGallery.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        viewModel.submissions.observe(viewLifecycleOwner, {
            val adapter = search_rvGallery.adapter as GalleryAdapter

            search_progressBar.visibility = View.GONE
            search_rvGallery.visibility = View.VISIBLE
            search_ivNoResult.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            adapter.setData(it)
        })
    }

    private fun _setNoResultImage() {
        Glide.with(this)
            .load(R.drawable.no_result)
            .placeholder(R.drawable.no_result)
            .into(search_ivNoResult)
    }

    override fun onItemClick(submission: Submission) {
        val action = SearchPageFragmentDirections.actionSearchPageFragmentToDetailsPageFragment(submission)

        findNavController().navigate(action)
    }

    override fun onTextSubmit(query: String) {
        search_progressBar.visibility = View.VISIBLE
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