package com.ferrine.stockopname.ui.main

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.ferrine.stockopname.BaseDrawerActivity
import com.ferrine.stockopname.R
import com.ferrine.stockopname.data.model.Barcode
import com.ferrine.stockopname.data.model.Item
import com.ferrine.stockopname.data.model.WorkingTypes
import com.ferrine.stockopname.data.repository.ItemRepository
import com.ferrine.stockopname.data.repository.OpnameRowRepository
import com.ferrine.stockopname.ui.setting.SettingActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : BaseDrawerActivity() {

    companion object {
        const val COL_BARCODE = 0
        const val COL_ITEM_ID = 1
        const val COL_ARTICLE = 2
        const val COL_MATERIAL = 3
        const val COL_COLOR = 4
        const val COL_SIZE = 5
        const val COL_NAME = 6
        const val COL_DESCRIPTION = 7
        const val COL_CATEGORY = 8
        const val COL_PRICE = 9
        const val COL_SELL_PRICE = 10
        const val COL_DISCOUNT = 11
        const val COL_IS_SPECIAL_PRICE = 12
        const val COL_STOCK_QTY = 13
        const val COL_PRINT_QTY = 14
        const val COL_PRICING_ID = 15
    }

    private lateinit var tvSiteCode: TextView
    private lateinit var tvBrandCode: TextView
    private lateinit var tvItemCount: TextView
    private lateinit var tvOpnameCount: TextView
    private lateinit var btnUploadCsv: Button
    private lateinit var btnDownloadDb: Button
    private lateinit var btnClearCollectedData: Button
    private lateinit var btnClearItem: Button

    private val itemRepository by lazy { ItemRepository(this) }
    private val opnameRowRepository by lazy { OpnameRowRepository(this) }

    private val prefs by lazy {
        getSharedPreferences(SettingActivity.PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val selectCsvLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            importCsv(it)
        }
    }

    private val createDbLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/x-sqlite3")) { uri: Uri? ->
        uri?.let {
            exportDatabase(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupDrawer(toolbar)

        bindView()
        setupListeners()
    }

    private fun bindView() {
        tvSiteCode = findViewById(R.id.tvSiteCode)
        tvBrandCode = findViewById(R.id.tvBrandCode)
        tvItemCount = findViewById(R.id.tvItemCount)
        tvOpnameCount = findViewById(R.id.tvOpnameCount)
        btnUploadCsv = findViewById(R.id.btnUploadCsv)
        btnDownloadDb = findViewById(R.id.btnDownloadDb)
        btnClearCollectedData = findViewById(R.id.btnClearCollectedData)
        btnClearItem = findViewById(R.id.btnClearItem)
    }

    private fun setupListeners() {
        btnUploadCsv.setOnClickListener {
            selectCsvLauncher.launch("text/comma-separated-values")
        }
        btnDownloadDb.setOnClickListener {
            createDbLauncher.launch("stockopname.db")
        }
        btnClearCollectedData.setOnClickListener {
            showClearCollectedDataDialog()
        }
        btnClearItem.setOnClickListener {
            showClearItemDialog()
        }
    }

    private fun showClearCollectedDataDialog() {
        val input = EditText(this)
        input.hint = "type 'clear data' here"
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        lp.setMargins(48, 20, 48, 0)
        input.layoutParams = lp
        container.addView(input)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Clear Collected Data")
            .setMessage("Data opname yang telah dikumpulkan akan dihapus permanen. Ketik \"clear data\" untuk melanjutkan.")
            .setView(container)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Lanjut Delete") { _, _ ->
                val typedText = input.text.toString()
                if (typedText == "clear data") {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) { opnameRowRepository.deleteAll() }
                        updateCounts()
                        Toast.makeText(this@MainActivity, "Data opname berhasil dihapus", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Konfirmasi salah, data tidak dihapus", Toast.LENGTH_SHORT).show()
                }
            }
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(android.R.color.holo_red_dark))
    }

    private fun showClearItemDialog() {
        AlertDialog.Builder(this)
            .setTitle("Clear Item")
            .setMessage("Tabel item dan barcode akan dikosongkan. Lanjutkan?")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) { itemRepository.deleteAll() }
                    updateCounts()
                    Toast.makeText(this@MainActivity, "Data item dan barcode berhasil dihapus", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        displaySettings()
        updateCounts()
    }

    private fun displaySettings() {
        val siteCode = prefs.getString(SettingActivity.KEY_SITE_CODE, "-") ?: "-"
        val brandCode = prefs.getString(SettingActivity.KEY_BRAND_CODE, "-") ?: "-"
        val workingTypeName = prefs.getString(SettingActivity.KEY_WORKING_TYPE, WorkingTypes.NONE.name)
        val useCentralServer = prefs.getBoolean(SettingActivity.KEY_USE_CENTRAL_SERVER, false)
        
        val workingType = try {
            WorkingTypes.valueOf(workingTypeName ?: WorkingTypes.NONE.name)
        } catch (e: Exception) {
            WorkingTypes.NONE
        }

        if (workingType == WorkingTypes.NONE || sessionManager.isAdmin) {
            supportActionBar?.title = "Main"
        } else {
            supportActionBar?.title = workingType.displayName
        }

        tvSiteCode.text = siteCode
        tvBrandCode.text = brandCode

        // Fitur upload CSV muncul jika Use Central Server aktif
        btnUploadCsv.visibility = if (!useCentralServer) View.VISIBLE else View.GONE
    }

    private fun updateCounts() {
        lifecycleScope.launch {
            val itemCount = withContext(Dispatchers.IO) { itemRepository.getCount() }
            val opnameCount = withContext(Dispatchers.IO) { opnameRowRepository.getCount() }
            
            tvItemCount.text = itemCount.toString()
            tvOpnameCount.text = opnameCount.toString()
        }
    }

    private fun importCsv(uri: Uri) {
        lifecycleScope.launch {
            try {
                val (itemsWithBarcodes, failedCount) = withContext(Dispatchers.IO) {
                    parseCsv(uri)
                }
                
                if (itemsWithBarcodes.isEmpty()) {
                    val msg = if (failedCount > 0) "Gagal: $failedCount baris tidak valid" else "File CSV kosong atau format salah"
                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    itemRepository.insertOrUpdateBatch(itemsWithBarcodes)
                }

                val message = if (failedCount > 0) {
                    "Berhasil impor ${itemsWithBarcodes.size} item. ($failedCount baris mismatch diabaikan)"
                } else {
                    "Berhasil mengimpor ${itemsWithBarcodes.size} item"
                }
                
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                updateCounts()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Gagal mengimpor CSV: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun parseCsv(uri: Uri): Pair<List<Pair<Item, Barcode>>, Int> {
        val result = mutableListOf<Pair<Item, Barcode>>()
        var failedCount = 0
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Skip header
                val header = reader.readLine()
                var line: String? = reader.readLine()
                while (line != null) {
                    if (line.isBlank()) {
                        line = reader.readLine()
                        continue
                    }
                    
                    // Coba deteksi delimiter (default pipe, fallback ke koma atau semicolon)
                    var tokens = line.split("|")
                    if (tokens.size < 2) tokens = line.split(",")
                    if (tokens.size < 2) tokens = line.split(";")

                    if (tokens.size >= 2) { // Minimal ada barcode dan itemId
                        try {
                            val barcodeStr = tokens[COL_BARCODE].trim()
                            val itemIdStr = tokens[COL_ITEM_ID].trim()
                            
                            if (barcodeStr.isEmpty() || itemIdStr.isEmpty()) {
                                failedCount++
                            } else {
                                val item = Item(
                                    itemId = itemIdStr,
                                    article = tokens.getOrElse(COL_ARTICLE) { "" }.trim(),
                                    material = tokens.getOrElse(COL_MATERIAL) { "" }.trim(),
                                    color = tokens.getOrElse(COL_COLOR) { "" }.trim(),
                                    size = tokens.getOrElse(COL_SIZE) { "" }.trim(),
                                    name = tokens.getOrElse(COL_NAME) { "" }.trim(),
                                    description = tokens.getOrElse(COL_DESCRIPTION) { "" }.trim(),
                                    category = tokens.getOrElse(COL_CATEGORY) { "" }.trim(),
                                    price = tokens.getOrElse(COL_PRICE) { "0" }.trim().replace(",", ".").toDoubleOrNull() ?: 0.0,
                                    sellPrice = tokens.getOrElse(COL_SELL_PRICE) { "0" }.trim().replace(",", ".").toDoubleOrNull() ?: 0.0,
                                    discount = tokens.getOrElse(COL_DISCOUNT) { "0" }.trim().replace(",", ".").toDoubleOrNull() ?: 0.0,
                                    isSpecialPrice = tokens.getOrElse(COL_IS_SPECIAL_PRICE) { "" }.trim().lowercase().let { it == "true" || it == "1" || it == "yes" },
                                    stockQty = tokens.getOrElse(COL_STOCK_QTY) { "0" }.trim().toIntOrNull() ?: 0,
                                    printQty = tokens.getOrElse(COL_PRINT_QTY) { "0" }.trim().toIntOrNull() ?: 0,
                                    pricingId = tokens.getOrElse(COL_PRICING_ID) { "" }.trim()
                                )
                                
                                val barcode = Barcode(
                                    barcode = barcodeStr,
                                    itemId = itemIdStr
                                )
                                
                                result.add(Pair(item, barcode))
                            }
                        } catch (e: Exception) {
                            failedCount++
                        }
                    } else {
                        failedCount++
                    }
                    line = reader.readLine()
                }
            }
        }
        return Pair(result, failedCount)
    }

    private fun exportDatabase(uri: Uri) {
        lifecycleScope.launch {
            try {
                val dbFile = getDatabasePath("stockopname.db")
                if (!dbFile.exists()) {
                    Toast.makeText(this@MainActivity, "Database tidak ditemukan", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    contentResolver.openOutputStream(uri)?.use { outputStream ->
                        dbFile.inputStream().use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
                Toast.makeText(this@MainActivity, "Database berhasil diekspor", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Gagal mengekspor database: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun drawerIconColor(): Int {
        return android.R.color.black
    }
}
