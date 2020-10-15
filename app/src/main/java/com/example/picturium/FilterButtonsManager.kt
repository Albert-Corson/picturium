package com.example.picturium

import android.view.View
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.iterator

class FilterButtonsManager(activity: AppCompatActivity) {
    private val filters: HashMap<RadioButton, RadioGroup> = HashMap()

    init {
        val mainFilters: RadioGroup = activity.findViewById(R.id.rgMainFilterButtons)
        val secondaryFilters: ConstraintLayout = activity.findViewById(R.id.clSecondaryFilterButtons)
        for (view in mainFilters) {
            if (view !is RadioButton)
                continue
            filters[view] = secondaryFilters.findViewWithTag(view.tag)
            view.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
                if (!isChecked)
                    mainFilterButtonOnUnchecked(view)
            })
        }
    }

    private fun mainFilterButtonOnUnchecked(button: RadioButton) {
        filters[button]?.visibility = View.GONE
    }

    public fun mainFilterButtonOnClick(button: RadioButton) {
        if (filters[button]?.visibility == View.VISIBLE)
            filters[button]?.visibility = View.GONE
        else
            filters[button]?.visibility = View.VISIBLE
    }

    public fun secondaryFilterButtonOnClick(button: RadioButton) {
    }
}