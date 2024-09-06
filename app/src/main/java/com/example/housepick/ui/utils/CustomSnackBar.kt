package com.example.housepick.ui.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.example.housepick.R
import com.google.android.material.snackbar.Snackbar


fun showSnackBar(viewRoot: View, text: Int, icon: Int) {
    val locale = LocaleUtils.getSelectedLanguageId()
    val snackBar = Snackbar.make(viewRoot, text, Snackbar.LENGTH_SHORT)
    val snackBarView = snackBar.view
    snackBarView.translationY = -(convertDpToPixel(16F, viewRoot.context))
    snackBar.setBackgroundTint(ContextCompat.getColor(viewRoot.context, R.color.white))
    ViewCompat.setLayoutDirection(snackBarView, ViewCompat.LAYOUT_DIRECTION_LOCALE)
    val snackBarTextView =
        snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    val font = ResourcesCompat.getFont(viewRoot.context, R.font.iran_yekan_500)
    snackBarTextView.apply {
        typeface = font
        setTextColor(ContextCompat.getColor(viewRoot.context, R.color.blue_dark))
        if (locale == "fa") {
            // Persian (icon on the right)
            setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0)
        } else {
            // English (icon on the left)
            setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        }
        compoundDrawablePadding =
            viewRoot.resources.getDimensionPixelOffset(R.dimen.snackBar_padding_horizontal)
    }
    snackBar.show()
}

fun convertDpToPixel(dp: Float, context: Context): Float {
    return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}