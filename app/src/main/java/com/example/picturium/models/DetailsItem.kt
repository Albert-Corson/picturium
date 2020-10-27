package com.example.picturium.models

import com.example.picturium.R

class DetailsItem(type: Type) {
    enum class Type(val value: Int) {
        HEADER(R.layout.details_page_title),
        FOOTER(R.layout.details_page_description),
        ITEM(R.layout.details_page_media_item)
    }

    var image: Image? = null
        private set

    var text: String? = null
        private set

    var type: Type = type
        private set

    init {
        this.image = image
    }

    constructor(type: Type, text: String) : this(type) {
        this.text = text
    }

    constructor(type: Type, image: Image) : this(type) {
        this.image = image
    }
}
