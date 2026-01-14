package com.ferrine.stockopname.data.repository

import android.annotation.SuppressLint
import android.content.ContentValues
import com.ferrine.stockopname.BaseDataRepository
import com.ferrine.stockopname.data.db.AppDatabaseHelper
import com.ferrine.stockopname.data.db.DbContract
import com.ferrine.stockopname.data.model.User
import java.security.MessageDigest
import android.database.sqlite.SQLiteDatabase


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
            user = mapCursorToUser(cursor)
        }
        cursor.close()
        return user
    }

    @SuppressLint("Range")
    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(DbContract.UserTable.TABLE_NAME, null, null, null, null, null, null)
        
        if (cursor.moveToFirst()) {
            do {
                users.add(mapCursorToUser(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return users
    }

    fun saveUser(user: User, passChanged: Boolean = false) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DbContract.UserTable.COLUMN_USERNAME, user.username)
            put(DbContract.UserTable.COLUMN_FULLNAME, user.fullname)
            if (passChanged && user.password.isNotBlank()) {
                put(DbContract.UserTable.COLUMN_PASSWORD, hashPassword(user.password))
            }
            put(DbContract.UserTable.COLUMN_IS_ADMIN, if (user.isAdmin) 1 else 0)
            put(DbContract.UserTable.COLUMN_ALLOW_OPNAME, if (user.allowOpname) 1 else 0)
            put(DbContract.UserTable.COLUMN_ALLOW_RECEIVING, if (user.allowReceiving) 1 else 0)
            put(DbContract.UserTable.COLUMN_ALLOW_TRANSFER, if (user.allowTransfer) 1 else 0)
            put(DbContract.UserTable.COLUMN_ALLOW_PRINTLABEL, if (user.allowPrintlabel) 1 else 0)
        }
        
        // Cek jika user sudah ada
        val cursor = db.query(
            DbContract.UserTable.TABLE_NAME,
            arrayOf(DbContract.UserTable.COLUMN_USERNAME),
            "${DbContract.UserTable.COLUMN_USERNAME} = ?",
            arrayOf(user.username),
            null, null, null
        )
        
        val exists = cursor.moveToFirst()
        cursor.close()
        
        if (exists) {
            db.update(
                DbContract.UserTable.TABLE_NAME,
                values,
                "${DbContract.UserTable.COLUMN_USERNAME} = ?",
                arrayOf(user.username)
            )
        } else {
            // Untuk user baru, pastikan password ada
            if (!values.containsKey(DbContract.UserTable.COLUMN_PASSWORD)) {
                values.put(DbContract.UserTable.COLUMN_PASSWORD, hashPassword(user.password))
            }
            db.insert(DbContract.UserTable.TABLE_NAME, null, values)
        }
    }

    fun deleteUser(username: String) {
        val db = dbHelper.writableDatabase
        db.delete(
            DbContract.UserTable.TABLE_NAME,
            "${DbContract.UserTable.COLUMN_USERNAME} = ?",
            arrayOf(username)
        )
    }

    @SuppressLint("Range")
    private fun mapCursorToUser(cursor: android.database.Cursor): User {
        return User(
            username = cursor.getString(cursor.getColumnIndex(DbContract.UserTable.COLUMN_USERNAME)) ?: "",
            fullname = cursor.getString(cursor.getColumnIndex(DbContract.UserTable.COLUMN_FULLNAME)) ?: "",
            password = cursor.getString(cursor.getColumnIndex(DbContract.UserTable.COLUMN_PASSWORD)) ?: "",
            isAdmin = cursor.getInt(cursor.getColumnIndex(DbContract.UserTable.COLUMN_IS_ADMIN)) == 1,
            allowOpname = cursor.getInt(cursor.getColumnIndex(DbContract.UserTable.COLUMN_ALLOW_OPNAME)) == 1,
            allowReceiving = cursor.getInt(cursor.getColumnIndex(DbContract.UserTable.COLUMN_ALLOW_RECEIVING)) == 1,
            allowTransfer = cursor.getInt(cursor.getColumnIndex(DbContract.UserTable.COLUMN_ALLOW_TRANSFER)) == 1,
            allowPrintlabel = cursor.getInt(cursor.getColumnIndex(DbContract.UserTable.COLUMN_ALLOW_PRINTLABEL)) == 1
        )
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
