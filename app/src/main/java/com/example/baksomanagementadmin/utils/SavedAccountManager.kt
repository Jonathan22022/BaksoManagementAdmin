package com.example.baksomanagementadmin.utils

import android.content.Context

object SavedAccountManager {

    private const val PREF_NAME = "saved_accounts"
    private const val KEY_EMAILS = "emails"

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveAccount(context: Context, email: String) {

        val prefs = getPrefs(context)

        val current =
            prefs.getStringSet(KEY_EMAILS, mutableSetOf())?.toMutableSet()
                ?: mutableSetOf()

        current.add(email)

        prefs.edit()
            .putStringSet(KEY_EMAILS, current)
            .apply()
    }

    fun getAccounts(context: Context): List<String> {

        return getPrefs(context)
            .getStringSet(KEY_EMAILS, mutableSetOf())
            ?.toList()
            ?: emptyList()
    }

    fun removeAccount(context: Context, email: String) {

        val prefs = getPrefs(context)

        val current =
            prefs.getStringSet(KEY_EMAILS, mutableSetOf())?.toMutableSet()
                ?: mutableSetOf()

        current.remove(email)

        prefs.edit()
            .putStringSet(KEY_EMAILS, current)
            .apply()
    }
}