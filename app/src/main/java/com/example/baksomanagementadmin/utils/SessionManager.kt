package com.example.baksomanagementadmin.utils

import android.content.Context

object SessionManager {

    private const val PREF_NAME = "bakso_session"
    private const val KEY_LAST_LOGIN = "last_login_time"
    private const val SESSION_DURATION_MS = 5 * 60 * 1000L

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveLoginSession(context: Context) {
        getPrefs(context).edit()
            .putLong(KEY_LAST_LOGIN, System.currentTimeMillis())
            .apply()
    }

    fun isSessionActive(context: Context): Boolean {
        val lastLogin = getPrefs(context).getLong(KEY_LAST_LOGIN, 0L)
        if (lastLogin == 0L) return false
        val elapsed = System.currentTimeMillis() - lastLogin
        return elapsed < SESSION_DURATION_MS
    }

    fun clearSession(context: Context) {
        getPrefs(context).edit()
            .remove(KEY_LAST_LOGIN)
            .apply()
    }
}