package com.example.housepick.ui.utils

import android.util.Base64
import android.widget.ImageView
import com.bumptech.glide.Glide

fun showImage(base64Image: String, view: ImageView) {
    val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
    Glide.with(view.context)
        .asBitmap()
        .load(decodedString)
        .into(view)
}