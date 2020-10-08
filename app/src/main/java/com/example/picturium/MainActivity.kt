package com.example.picturium

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity() {
    private lateinit var searchBar: SearchView
    private lateinit var uploadBtn: ImageButton
    private lateinit var profileBtn: ImageButton
    private lateinit var toast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchBar = findViewById(R.id.searchbar)
        uploadBtn = findViewById(R.id.upload_btn)
        profileBtn = findViewById(R.id.profile_btn)
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        searchBar.setOnQueryTextListener(SearchBarQueryListener(searchBar, toast))
        profileBtn.setOnClickListener { profileBtnClickListener() }
        uploadBtn.setOnClickListener { uploadBtnClickListener() }
    }

    private fun profileBtnClickListener() {
        toast.setText("PROFILE BTN")
        toast.show()
    }

    private fun uploadBtnClickListener() {
        toast.setText("UPLOAD BTN")
        toast.show()
    }
}