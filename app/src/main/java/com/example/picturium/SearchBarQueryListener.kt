package com.example.picturium

import android.view.View
import androidx.appcompat.widget.SearchView

class SearchBarQueryListener(private val view: View, private val listener: OnTextSubmitListener) : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query.isNullOrBlank())
            return false
        view.clearFocus()
        listener.onTextSubmit(query)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    interface OnTextSubmitListener {
        fun onTextSubmit(query: String)
    }
}
