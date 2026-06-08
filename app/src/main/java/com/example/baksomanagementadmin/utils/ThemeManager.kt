package com.example.baksomanagementadmin.utils

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.baksomanagementadmin.R

object ThemeManager {

    private const val PREF_NAME = "theme_pref"
    private const val KEY_THEME = "selected_theme"

    const val LIGHT = "light"
    const val DARK = "dark"

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

    fun saveTheme(
        context: Context,
        theme: String
    ) {

        getPrefs(context)
            .edit()
            .putString(KEY_THEME, theme)
            .apply()
    }

    fun getTheme(context: Context): String {

        return getPrefs(context)
            .getString(KEY_THEME, LIGHT)
            ?: LIGHT
    }

    fun applyTheme(theme: String) {

        when (theme) {

            LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
            }

            DARK -> {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
            }
        }
    }
}