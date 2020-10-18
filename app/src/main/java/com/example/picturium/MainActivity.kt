package com.example.picturium

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.top_bar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var _filterBtnManager: FilterButtonsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch(Dispatchers.IO) {
            User.init(this@MainActivity.applicationContext)
            withContext(Dispatchers.Main) {
                _setProfileBtnImage()
            }
        }

        topBar_svSearchBar.setOnQueryTextListener(SearchBarQueryListener(topBar_svSearchBar))
        _filterBtnManager = FilterButtonsManager(this)
    }

    private fun _setProfileBtnImage() {
        Glide.with(this)
            .load(User.publicData?.profilePicture)
            .circleCrop()
            .placeholder(R.drawable.ic_dflt_profile)
            .error(R.drawable.ic_dflt_profile)
            .into(topBar_ibProfile)
    }

    override fun onResume() {
        super.onResume()
        _setProfileBtnImage()
    }

    fun profileBtnOnClick(view: View) {
        val intent: Intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
    }

    fun uploadBtnOnClick(view: View) {
    }

    fun mainFilterBtnOnClick(button: View) {
        if (button !is RadioButton)
            return
        _filterBtnManager.mainFilterButtonOnClick(button)
    }

    fun secondaryFilterBtnOnClick(button: View) {
        if (button !is RadioButton)
            return
        _filterBtnManager.secondaryFilterButtonOnClick(button)
    }
}