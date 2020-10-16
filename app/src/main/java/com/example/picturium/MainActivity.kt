package com.example.picturium

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.top_bar.*

class MainActivity : AppCompatActivity() {
    private lateinit var _filterBtnManager: FilterButtonsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        User(this.applicationContext)
        _filterBtnManager = FilterButtonsManager(this)
        topBar_svSearchBar.setOnQueryTextListener(SearchBarQueryListener(topBar_svSearchBar))
    }

    public fun profileBtnOnClick(view: View) {
        val intent: Intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
    }

    public fun uploadBtnOnClick(view: View) {
    }

    public fun mainFilterBtnOnClick(button: View) {
        if (button !is RadioButton)
            return
        _filterBtnManager.mainFilterButtonOnClick(button)
    }

    public fun secondaryFilterBtnOnClick(button: View) {
        if (button !is RadioButton)
            return
        _filterBtnManager.secondaryFilterButtonOnClick(button)
    }
}