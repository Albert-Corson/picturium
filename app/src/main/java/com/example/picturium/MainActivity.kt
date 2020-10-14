package com.example.picturium

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity() {
    private lateinit var filersManager: FilterButtonsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBar: SearchView = findViewById(R.id.searchbar)
        filersManager = FilterButtonsManager(this)

        searchBar.setOnQueryTextListener(SearchBarQueryListener(searchBar))
    }

    public fun profileBtnOnClick(view: View) {
    }

    public fun uploadBtnClick(view: View) {
    }
}