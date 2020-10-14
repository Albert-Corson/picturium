package com.example.picturium

import android.view.View
import androidx.appcompat.widget.SearchView

class SearchBarQueryListener(private val view: View) : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query.isNullOrBlank())
            return false
        view.clearFocus()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }
}
