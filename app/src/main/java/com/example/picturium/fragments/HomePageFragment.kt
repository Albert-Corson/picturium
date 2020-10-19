package com.example.picturium.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.picturium.FilterButtonsManager
import com.example.picturium.R
import com.example.picturium.SearchBarQueryListener
import com.example.picturium.User
import com.example.picturium.adapters.GalleryAdapter
import com.example.picturium.models.Submission
import com.example.picturium.viewmodels.GalleryFilterViewModel
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomePageFragment : Fragment(R.layout.fragment_home_page), GalleryAdapter.OnItemClickListener, SearchBarQueryListener.OnTextSubmitListener {
    private lateinit var _filterBtnManager: FilterButtonsManager
    private val viewModel by viewModels<GalleryFilterViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _filterBtnManager = FilterButtonsManager(this.requireActivity())
        topBar_svSearchBar.setOnQueryTextListener(SearchBarQueryListener(topBar_svSearchBar, this))
        topBar_ibProfile.setOnClickListener { profileBtnOnClick() }
        topBar_ibUpload.setOnClickListener { uploadBtnOnClick() }

        gallery_recyclerView.adapter = GalleryAdapter(emptyList(), this)
        gallery_recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        viewModel.submissions.observe(viewLifecycleOwner, {
            val adapter = gallery_recyclerView.adapter as GalleryAdapter

            adapter.setData(it)
        })

        GlobalScope.launch(Dispatchers.IO) {
            User.init(this@HomePageFragment.requireActivity().applicationContext)
            withContext(Dispatchers.Main) {
                _setProfileBtnImage()
            }
        }
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

        topBar_svSearchBar.setQuery("", false)
        findNavController().navigate(action)
    }

    private fun _setProfileBtnImage() {
        Glide.with(this)
            .load(User.publicData?.profilePicture)
            .fallback(R.drawable.ic_dflt_profile)
            .circleCrop()
            .into(topBar_ibProfile)
    }

    private fun profileBtnOnClick() {
        val action = HomePageFragmentDirections.actionHomeFragmentToProfilePageFragment()

        findNavController().navigate(action)
    }

    private fun uploadBtnOnClick() {
        Toast.makeText(this.context, "Upload", Toast.LENGTH_LONG).show()
    }
}