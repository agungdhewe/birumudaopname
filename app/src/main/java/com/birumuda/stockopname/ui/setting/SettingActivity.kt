package com.birumuda.stockopname.ui.setting

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.birumuda.stockopname.R
import com.birumuda.stockopname.data.model.BarcodeScannerOptions
import com.birumuda.stockopname.data.model.PrinterOptions
import com.birumuda.stockopname.data.model.WorkingTypes
import com.birumuda.stockopname.utils.SessionManager

class SettingActivity : AppCompatActivity() {

    private lateinit var etSiteCode: EditText
    private lateinit var etBrandCode: EditText
    private lateinit var spWorkingType: Spinner
    private lateinit var spBarcodeReader: Spinner
    private lateinit var spPrinter: Spinner

    private val sessionManager by lazy { SessionManager(this) }

    private val prefs by lazy {
        getSharedPreferences("app_setting", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
		// harus menggunakan Light Theme
		AppCompatDelegate.setDefaultNightMode(
			AppCompatDelegate.MODE_NIGHT_NO
		)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Setting"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        bindView()
        setupWorkingTypeSpinner()
        setupBarcodeReaderSpinner()
        setupPrinterSpinner()
        loadSetting()
        setupUIBasedOnLoginStatus()
    }

    private fun bindView() {
        etSiteCode = findViewById(R.id.etSiteCode)
        etBrandCode = findViewById(R.id.etBrandCode)
        spWorkingType = findViewById(R.id.spWorkingType)
        spBarcodeReader = findViewById(R.id.spBarcodeReader)
        spPrinter = findViewById(R.id.spPrinter)
    }

    private fun setupUIBasedOnLoginStatus() {
        val isLoggedIn = sessionManager.isLoggedIn()
        
        // Disable input jika sudah login, enable jika belum/sudah logout
        etSiteCode.isEnabled = !isLoggedIn
        etBrandCode.isEnabled = !isLoggedIn
        spWorkingType.isEnabled = !isLoggedIn
    }

    private fun setupWorkingTypeSpinner() {
        val options = WorkingTypes.entries.map { it.displayName }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            options
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spWorkingType.adapter = adapter
    }

    private fun setupBarcodeReaderSpinner() {
        val options = BarcodeScannerOptions.entries.map { it.displayName }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            options
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spBarcodeReader.adapter = adapter
    }

    private fun setupPrinterSpinner() {
        val options = PrinterOptions.entries.map { it.displayName }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            options
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spPrinter.adapter = adapter
    }

    /**
     * Load setting saat Activity dibuka
     */
    private fun loadSetting() {
        etSiteCode.setText(prefs.getString("site_code", ""))
        etBrandCode.setText(prefs.getString("brand_code", ""))

        val workingTypeName = prefs.getString("working_type", WorkingTypes.NONE.name)
        val workingTypeIndex = WorkingTypes.entries.indexOfFirst { it.name == workingTypeName }
            .coerceAtLeast(0)
        spWorkingType.setSelection(workingTypeIndex)

        val barcodeReaderName = prefs.getString("barcode_reader", BarcodeScannerOptions.SCANNER.name)
        val barcodeIndex = BarcodeScannerOptions.entries.indexOfFirst { it.name == barcodeReaderName }
            .coerceAtLeast(0)
        spBarcodeReader.setSelection(barcodeIndex)

        val printerPrefix = prefs.getString("printer_prefix", "")
        val printerIndex = PrinterOptions.entries.indexOfFirst { it.prefix == printerPrefix }
            .coerceAtLeast(0)
        spPrinter.setSelection(printerIndex)
    }

    /**
     * Simpan setting otomatis
     */
    private fun saveSetting() {
        // Jangan simpan jika field di-disable (asumsi: tidak ada perubahan data kritikal saat login)
        // Atau tetap simpan jika perlu, tapi biasanya yang di-disable tidak akan berubah.
        
        val selectedWorkingType = WorkingTypes.entries[spWorkingType.selectedItemPosition]
        val selectedBarcodeReader = BarcodeScannerOptions.entries[spBarcodeReader.selectedItemPosition]
        val selectedPrinter = PrinterOptions.entries[spPrinter.selectedItemPosition]

        prefs.edit().apply {
            putString("site_code", etSiteCode.text.toString())
            putString("brand_code", etBrandCode.text.toString())
            putString("working_type", selectedWorkingType.name)
            putString("barcode_reader", selectedBarcodeReader.name)
            putString("printer_prefix", selectedPrinter.prefix)
            apply()
        }
    }

    override fun onPause() {
        super.onPause()
        saveSetting() // AUTO SAVE saat Back / Home
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
