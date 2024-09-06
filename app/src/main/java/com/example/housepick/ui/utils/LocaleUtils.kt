package com.example.housepick.ui.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.preference.PreferenceManager
import com.example.housepick.MyApplication
import java.util.Locale

object LocaleUtils {

    const val ENGLISH = "en"
    const val PERSIAN = "fa"

    fun initialize(context: Context, defaultLanguage: String) {
        setLocale(context, defaultLanguage)
    }

    fun setLocale(context: Context, language: String): Boolean {
        return updateResources(context, language)
    }

    private fun updateResources(context: Context, language: String): Boolean {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        return true
    }

    fun getSelectedLanguageId(): String {
        val prefs =
            PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance().applicationContext)
        return prefs.getString("app_language_id", PERSIAN) ?: PERSIAN
    }


    fun setSelectedLanguageId(context: Context, languageId: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putString("app_language_id", languageId).apply()
    }
}
