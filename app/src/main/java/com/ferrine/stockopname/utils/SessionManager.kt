package com.ferrine.stockopname.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

	companion object {
		private const val PREF_NAME = "stockopname_session"
		private const val KEY_IS_LOGGED_IN = "is_logged_in"
		private const val KEY_USER_ID = "user_id"
		private const val KEY_USERNAME = "username"
		private const val KEY_TOKEN = "token"
	}

	private val prefs: SharedPreferences =
		context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

	private val editor: SharedPreferences.Editor = prefs.edit()

	/* =========================
	   LOGIN SESSION
	   ========================= */

	fun createLoginSession(
		userId: String,
		username: String,
		token: String
	) {
		editor.putBoolean(KEY_IS_LOGGED_IN, true)
		editor.putString(KEY_USER_ID, userId)
		editor.putString(KEY_USERNAME, username)
		editor.putString(KEY_TOKEN, token)
		editor.apply()
	}

	fun isLoggedIn(): Boolean {
		return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
	}

	/* =========================
	   GETTER USER DATA
	   ========================= */

	val userId: String?
		get() = prefs.getString(KEY_USER_ID, null)

	val username: String?
		get() = prefs.getString(KEY_USERNAME, null)

	val token: String?
		get() = prefs.getString(KEY_TOKEN, null)

	/* =========================
	   LOGOUT
	   ========================= */

	fun logout() {
		editor.clear()
		editor.apply()
	}
}
