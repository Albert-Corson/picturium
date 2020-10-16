package com.example.picturium

import android.view.View
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.iterator

class FilterButtonsManager(activity: AppCompatActivity) {
    private val _filters: HashMap<RadioButton, RadioGroup> = HashMap()

    init {
        val mainFilters: RadioGroup = activity.findViewById(R.id.filterBar_rgMainFilterButtons)
        val secondaryFilters: ConstraintLayout = activity.findViewById(R.id.filterBar_clSecondaryFilterButtons)
        for (view in mainFilters) {
            if (view !is RadioButton)
                continue
            _filters[view] = secondaryFilters.findViewWithTag(view.tag)
            view.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
                if (!isChecked)
                    mainFilterButtonOnUnchecked(view)
            })
        }
    }

    private fun mainFilterButtonOnUnchecked(button: RadioButton) {
        _filters[button]?.visibility = View.GONE
    }

    public fun mainFilterButtonOnClick(button: RadioButton) {
        if (_filters[button]?.visibility == View.VISIBLE)
            _filters[button]?.visibility = View.GONE
        else
            _filters[button]?.visibility = View.VISIBLE
    }

    public fun secondaryFilterButtonOnClick(button: RadioButton) {
    }
}