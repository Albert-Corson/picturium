package com.example.picturium

import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView

class SearchBarQueryListener(private val view: View, private val toast: Toast) : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query.isNullOrBlank())
            return false
        toast.setText(query)
        toast.show()
        view.clearFocus()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }
}
