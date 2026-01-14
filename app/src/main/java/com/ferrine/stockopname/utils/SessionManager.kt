package com.ferrine.stockopname.utils

import android.content.Context
import android.content.SharedPreferences
import com.ferrine.stockopname.data.model.User

class SessionManager(context: Context) {

	companion object {
		private const val PREF_NAME = "stockopname_session"
		private const val KEY_IS_LOGGED_IN = "is_logged_in"
		private const val KEY_USERNAME = "username"
		private const val KEY_FULLNAME = "fullname"
		private const val KEY_IS_ADMIN = "is_admin"
		private const val KEY_ALLOW_OPNAME = "allow_opname"
		private const val KEY_ALLOW_RECEIVING = "allow_receiving"
		private const val KEY_ALLOW_TRANSFER = "allow_transfer"
		private const val KEY_ALLOW_PRINTLABEL = "allow_printlabel"
	}

	private val prefs: SharedPreferences =
		context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

	private val editor: SharedPreferences.Editor = prefs.edit()

	fun createLoginSession(user: User) {
		editor.putBoolean(KEY_IS_LOGGED_IN, true)
		editor.putString(KEY_USERNAME, user.username)
		editor.putString(KEY_FULLNAME, user.fullname)
		editor.putBoolean(KEY_IS_ADMIN, user.isAdmin)
		editor.putBoolean(KEY_ALLOW_OPNAME, user.allowOpname)
		editor.putBoolean(KEY_ALLOW_RECEIVING, user.allowReceiving)
		editor.putBoolean(KEY_ALLOW_TRANSFER, user.allowTransfer)
		editor.putBoolean(KEY_ALLOW_PRINTLABEL, user.allowPrintlabel)
		editor.apply()
	}

	fun isLoggedIn(): Boolean {
		return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
	}

	val username: String?
		get() = prefs.getString(KEY_USERNAME, null)

	val isAdmin: Boolean
		get() = prefs.getBoolean(KEY_IS_ADMIN, false)

	val allowOpname: Boolean
		get() = prefs.getBoolean(KEY_ALLOW_OPNAME, false)

	val allowReceiving: Boolean
		get() = prefs.getBoolean(KEY_ALLOW_RECEIVING, false)

	val allowTransfer: Boolean
		get() = prefs.getBoolean(KEY_ALLOW_TRANSFER, false)

	val allowPrintlabel: Boolean
		get() = prefs.getBoolean(KEY_ALLOW_PRINTLABEL, false)

	fun logout() {
		editor.clear()
		editor.apply()
	}
}
