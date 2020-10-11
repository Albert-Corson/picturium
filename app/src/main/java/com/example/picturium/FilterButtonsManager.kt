package com.example.picturium

import android.view.View
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.iterator

class FilterButtonsManager(private val activity: AppCompatActivity) {
    private val filters: HashMap<RadioButton, RadioGroup> = HashMap()

    init {
        val mainFilters: RadioGroup = activity.findViewById(R.id.main_filter_buttons)
        val secondaryFilters: ConstraintLayout = activity.findViewById(R.id.secondary_filter_buttons)
        for (view in mainFilters) {
            if (view !is RadioButton)
                continue
            val subFilters: RadioGroup = secondaryFilters.findViewWithTag(view.tag)
            filters[view] = subFilters
            view.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                    mainFilterButtonOn(view)
                else
                    mainFilterButtonOff(view)
            })
            for (subFilter in subFilters) {
                if (subFilter !is RadioButton)
                    continue
                subFilter.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        secondaryFilterButtonOn(subFilter, subFilters)
                    }
                })
            }
        }
    }

    private fun mainFilterButtonOn(button: RadioButton) {
        filters[button]?.visibility = View.VISIBLE
    }

    private fun mainFilterButtonOff(button: RadioButton) {
        filters[button]?.visibility = View.GONE
    }

    private fun secondaryFilterButtonOn(button: RadioButton, view: RadioGroup) {
    }
}