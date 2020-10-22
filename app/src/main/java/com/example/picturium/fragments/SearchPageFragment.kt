package com.example.picturium.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.map
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.picturium.R
import com.example.picturium.SearchBarQueryListener
import com.example.picturium.adapters.GalleryAdapter
import com.example.picturium.models.Submission
import com.example.picturium.viewmodels.GallerySearchViewModel
import kotlinx.android.synthetic.main.fragment_search_page.*


//add sort,
class SearchPageFragment : Fragment(R.layout.fragment_search_page), GalleryAdapter.OnItemClickListener, SearchBarQueryListener.OnTextSubmitListener {
    private val viewModel by viewModels<GallerySearchViewModel>()
    private val args : SearchPageFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = GalleryAdapter(this)

        viewModel.setQuery(args.query)
        viewModel.setWindow("all")

        topBar_returnBtn.setOnClickListener { returnBtnOnClick() }
        topBar_svSearchBarInSearch.setOnQueryTextListener(SearchBarQueryListener(topBar_svSearchBarInSearch, this))
        topBar_svSearchBarInSearch.setQuery(args.query, false)

        _setNoResultImage()
        search_rvGallery.adapter = adapter
        search_rvGallery.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        viewModel.submissions.observe(viewLifecycleOwner, {
            search_progressBar.visibility = View.GONE
            search_rvGallery.visibility = View.VISIBLE
            //search_ivNoResult.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
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
        val action = SearchPageFragmentDirections.actionSearchPageFragmentToDetailsPageFragment(submission)

        findNavController().navigate(action)
    }

    override fun onTextSubmit(query: String) {
        search_progressBar.visibility = View.VISIBLE
        search_rvGallery.visibility = View.GONE
        viewModel.setQuery(query)
    }

    private fun returnBtnOnClick() {
        findNavController().navigateUp()
    }
}