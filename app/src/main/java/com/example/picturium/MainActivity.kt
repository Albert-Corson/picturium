package com.example.picturium

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity() {
    lateinit var filterBtnManager: FilterButtonsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        filterBtnManager = FilterButtonsManager(this)

        val searchBar: SearchView = findViewById(R.id.svSearchBar)
        searchBar.setOnQueryTextListener(SearchBarQueryListener(searchBar))
    }

    public fun profileBtnOnClick(view: View) {
    }

    public fun uploadBtnOnClick(view: View) {
    }

    public fun mainFilterBtnOnClick(button: View) {
        if (button !is RadioButton)
            return
        filterBtnManager.mainFilterButtonOnClick(button)
    }

    public fun secondaryFilterBtnOnClick(button: View) {
        if (button !is RadioButton)
            return
        filterBtnManager.secondaryFilterButtonOnClick(button)
    }
}