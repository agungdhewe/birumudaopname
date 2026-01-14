package com.ferrine.stockopname.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ferrine.stockopname.R
import com.ferrine.stockopname.data.db.AppDatabaseHelper
import com.ferrine.stockopname.data.model.User
import com.ferrine.stockopname.data.repository.UserRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UserActivity : AppCompatActivity() {

    private lateinit var rvUsers: RecyclerView
    private lateinit var fabAddUser: FloatingActionButton
    private lateinit var adapter: UserAdapter
    private val userRepository by lazy { UserRepository(AppDatabaseHelper(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Manage Users"
            setDisplayHomeAsUpEnabled(true)
        }

        rvUsers = findViewById(R.id.rvUsers)
        fabAddUser = findViewById(R.id.fabAddUser)

        setupRecyclerView()
        
        fabAddUser.setOnClickListener {
            showUserDialog(null)
        }
    }

    private fun setupRecyclerView() {
        val users = userRepository.getAllUsers()
        adapter = UserAdapter(
            users,
            onEditClick = { user -> showUserDialog(user) },
            onDeleteClick = { user -> confirmDelete(user) }
        )
        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = adapter
    }

    private fun refreshData() {
        adapter.updateData(userRepository.getAllUsers())
    }

    private fun showUserDialog(user: User?) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_user, null)
        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val etFullname = view.findViewById<EditText>(R.id.etFullname)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val cbIsAdmin = view.findViewById<CheckBox>(R.id.cbIsAdmin)
        val cbAllowOpname = view.findViewById<CheckBox>(R.id.cbAllowOpname)
        val cbAllowReceiving = view.findViewById<CheckBox>(R.id.cbAllowReceiving)
        val cbAllowTransfer = view.findViewById<CheckBox>(R.id.cbAllowTransfer)
        val cbAllowPrintLabel = view.findViewById<CheckBox>(R.id.cbAllowPrintLabel)

        if (user != null) {
            etUsername.setText(user.username)
            etUsername.isEnabled = false
            etFullname.setText(user.fullname)
            etPassword.hint = "Leave blank to keep current"
            
            if (user.username.equals("admin", ignoreCase = true)) {
                etFullname.isEnabled = false
                cbIsAdmin.isEnabled = false
                cbAllowOpname.isEnabled = false
                cbAllowReceiving.isEnabled = false
                cbAllowTransfer.isEnabled = false
                cbAllowPrintLabel.isEnabled = false
            }
            
            cbIsAdmin.isChecked = user.isAdmin
            cbAllowOpname.isChecked = user.allowOpname
            cbAllowReceiving.isChecked = user.allowReceiving
            cbAllowTransfer.isChecked = user.allowTransfer
            cbAllowPrintLabel.isChecked = user.allowPrintlabel
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(if (user == null) "Add User" else "Edit User")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                val username = etUsername.text.toString()
                val fullname = etFullname.text.toString()
                val password = etPassword.text.toString()
                
                if (username.isBlank() || (user == null && password.isBlank())) {
                    Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newUser = if (user?.username?.equals("admin", ignoreCase = true) == true) {
                     user.copy(password = if (password.isNotBlank()) password else user.password)
                } else {
                    User(
                        username = username,
                        fullname = fullname,
                        password = if (password.isNotBlank()) password else (user?.password ?: ""),
                        isAdmin = cbIsAdmin.isChecked,
                        allowOpname = cbAllowOpname.isChecked,
                        allowReceiving = cbAllowReceiving.isChecked,
                        allowTransfer = cbAllowTransfer.isChecked,
                        allowPrintlabel = cbAllowPrintLabel.isChecked
                    )
                }
                
                userRepository.saveUser(newUser, password.isNotBlank())
                refreshData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDelete(user: User) {
        if (user.username.equals("admin", ignoreCase = true)) {
            Toast.makeText(this, "Cannot delete admin user", Toast.LENGTH_SHORT).show()
            return
        }
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete ${user.username}?")
            .setPositiveButton("Delete") { _, _ ->
                userRepository.deleteUser(user.username)
                refreshData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
