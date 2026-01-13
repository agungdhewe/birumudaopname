package com.birumuda.stockopname.data.repository

import android.annotation.SuppressLint
import com.birumuda.stockopname.BaseDataRepository
import com.birumuda.stockopname.data.db.AppDatabaseHelper
import com.birumuda.stockopname.data.db.DbContract
import com.birumuda.stockopname.data.model.User
import java.security.MessageDigest

class UserRepository(private val dbHelper: AppDatabaseHelper) : BaseDataRepository() {

    @SuppressLint("Range")
    fun login(username: String, pass: String): User? {
        val db = dbHelper.readableDatabase
        val hashedPassword = hashPassword(pass)
        
        val cursor = db.query(
            DbContract.UserTable.TABLE_NAME,
            null,
            "${DbContract.UserTable.COLUMN_USERNAME} = ? AND ${DbContract.UserTable.COLUMN_PASSWORD} = ?",
            arrayOf(username, hashedPassword),
            null,
            null,
            null
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                username = cursor.getString(cursor.getColumnIndex(DbContract.UserTable.COLUMN_USERNAME)),
                fullname = cursor.getString(cursor.getColumnIndex(DbContract.UserTable.COLUMN_FULLNAME)),
                password = cursor.getString(cursor.getColumnIndex(DbContract.UserTable.COLUMN_PASSWORD))
            )
        }
        cursor.close()
        return user
    }

    companion object {
        fun hashPassword(password: String): String {
            val bytes = password.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }
    }
}
