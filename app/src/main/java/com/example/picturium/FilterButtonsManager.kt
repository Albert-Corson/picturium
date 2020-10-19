package com.example.picturium

import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.FragmentActivity

class FilterButtonsManager(activity: FragmentActivity) {
    private val _filters: HashMap<RadioButton, RadioGroup> = HashMap()

    init {
    }

    private fun _mainFilterButtonOnUnchecked(button: RadioButton) {
        _filters[button]?.visibility = View.GONE
    }
}