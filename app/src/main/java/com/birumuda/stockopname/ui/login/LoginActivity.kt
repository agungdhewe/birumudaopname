package com.birumuda.stockopname.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.birumuda.stockopname.R
import com.birumuda.stockopname.data.db.AppDatabaseHelper
import com.birumuda.stockopname.data.repository.UserRepository
import com.birumuda.stockopname.ui.setting.SettingActivity
import com.birumuda.stockopname.ui.main.MainActivity
import com.birumuda.stockopname.utils.SessionManager

class LoginActivity : AppCompatActivity() {

	companion object {
		private const val DUMMY_TOKEN = "232353453"
	}

	private lateinit var etUsername: EditText
	private lateinit var etPassword: EditText
	private lateinit var btnLogin: Button
	private lateinit var tvSetting: TextView

	private lateinit var sessionManager: SessionManager
	private lateinit var userRepository: UserRepository

	override fun onCreate(savedInstanceState: Bundle?) {
		// harus menggunakan Light Theme
		AppCompatDelegate.setDefaultNightMode(
			AppCompatDelegate.MODE_NIGHT_NO
		)

		super.onCreate(savedInstanceState)

		// init sessionManager
		sessionManager = SessionManager(this)
		
		// init userRepository
		val dbHelper = AppDatabaseHelper(this)
		userRepository = UserRepository(dbHelper)

		// Jika sudah login → langsung ke MainActivity
		if (sessionManager.isLoggedIn()) {
			navigateToMain()
			return
		}

		setContentView(R.layout.activity_login)

		initView()
		initAction()
	}

	private fun initView() {
		etUsername = findViewById(R.id.etUsername)
		etPassword = findViewById(R.id.etPassword)
		btnLogin = findViewById(R.id.btnLogin)
		tvSetting = findViewById(R.id.tvSetting)

		// Fokus awal ke Username
		etUsername.requestFocus()
	}

	private fun initAction() {

		// Enter di Username
		etUsername.setOnEditorActionListener { _, _, _ ->
			val username = etUsername.text.toString().trim()

			if (username.isEmpty()) {
				etUsername.error = "Username wajib diisi"
				etUsername.requestFocus()
			} else {
				etPassword.requestFocus()
			}
			true
		}

		// Enter di Password → Login
		etPassword.setOnEditorActionListener { _, _, _ ->
			val username = etUsername.text.toString().trim()
			val password = etPassword.text.toString().trim()

			if (validateInput(username, password)) {
				doLogin(username, password)
			}
			true
		}

		// Klik tombol Login
		btnLogin.setOnClickListener {
			val username = etUsername.text.toString().trim()
			val password = etPassword.text.toString().trim()

			if (validateInput(username, password)) {
				doLogin(username, password)
			}
		}


		// buka setting
		tvSetting.setOnClickListener {
			val intent = Intent(this, SettingActivity::class.java)
			startActivity(intent)
		}
	}

	private fun validateInput(username: String, password: String): Boolean {
		if (username.isEmpty()) {
			etUsername.error = "Username wajib diisi"
			etUsername.requestFocus()
			return false
		}

		if (password.isEmpty()) {
			etPassword.error = "Password wajib diisi"
			etPassword.requestFocus()
			return false
		}
		return true
	}

	private fun doLogin(username: String, pass: String) {
		val user = userRepository.login(username, pass)
		
		if (user != null) {
			// Simpan session menggunakan data dari database
			sessionManager.createLoginSession(
				userId = user.username,
				username = user.username,
				token = DUMMY_TOKEN // Token masih dummy karena belum ada API
			)

			Toast.makeText(this, "Login berhasil sebagai ${user.fullname}", Toast.LENGTH_SHORT).show()
			navigateToMain()
		} else {
			Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show()
		}
	}

	private fun navigateToMain() {
		startActivity(Intent(this, MainActivity::class.java))
		finish()
	}
}
