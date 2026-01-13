package com.birumuda.stockopname.utils

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.birumuda.stockopname.R
import com.birumuda.stockopname.data.model.Label
import com.birumuda.stockopname.BaseScannerActivity
import com.birumuda.stockopname.data.model.PrintLabelMode
import com.birumuda.stockopname.ui.ScannerBarcodeFragment
import com.birumuda.stockopname.ui.ScannerCameraFragment
import com.birumuda.stockopname.ui.ScannerResultFragment
import kotlinx.coroutines.launch

class BarcodeReader(private val activity: BaseScannerActivity) {

    private val bluetoothPrintManager by lazy { BluetoothPrintManager(activity) }
	private var currentToast: Toast? = null


    fun findBarcode(barcode: String, mode: PrintLabelMode) {

		currentToast?.cancel()
		currentToast = Toast.makeText(activity, "getting data barcode $barcode", Toast.LENGTH_SHORT)
		currentToast?.show()

        activity.lifecycleScope.launch {
            holdBarcodeReader(true)
            val resultFragment = activity.supportFragmentManager.findFragmentById(R.id.fragment_printlabel_result) as? ScannerResultFragment
            resultFragment?.setError(null)

            try {
                if (barcode.equals("TM34567890123")) {
                    soundBarcodeNotFound()
                    resultFragment?.setError("Barcode <b>\"$barcode\"</b> tidak ditemukan".toHtml())
                    return@launch
                }

                // Simulasi Data (Idealnya ini dari Repository/API)
                val label = Label(
                    barcode = barcode,
                    description = "Product Name Simulation",
                    category = "Category",
                    price = java.math.BigDecimal(100000),
                    discount = "50%",
                    oldPrice = java.math.BigDecimal(200000),
                    pricingCode = "P01"
                )

                soundBarcodeFound()

                when (mode) {
                    PrintLabelMode.PRINT_LABEL -> {
                        printBarcode(label)
                    }
                    PrintLabelMode.OPNAME -> {

                    }
                    PrintLabelMode.RECEIVING -> {
                    }
                    PrintLabelMode.TRANSFER -> {
                    }
                }

            } catch (e: Exception) {
                resultFragment?.setError(e.message ?: "Gagal memproses barcode")
            } finally {
                holdBarcodeReader(false)
            }
        }
    }

    private suspend fun printBarcode(label: Label) {
        val prefs = activity.getSharedPreferences("app_setting", Context.MODE_PRIVATE)
        val printerPrefix = prefs.getString("printer_prefix", "")

        if (printerPrefix.isNullOrEmpty()) {
            throw Exception("Printer belum diatur di Setting")
        }

        val tsplCommand = StringBuilder().apply {
            appendLine("SIZE 35 mm, 30 mm")
            appendLine("GAP 2 mm, 0")
            appendLine("DIRECTION 0")
            appendLine("CLS")
            appendLine("TEXT 10,10,\"ROMAN.TTF\",0,1,1,\"${label.description}\"")
            appendLine("BARCODE 10,50,\"128\",50,1,0,2,2,\"${label.barcode}\"")
            appendLine("PRINT 1")
        }.toString()

		bluetoothPrintManager.sendToPrinter(printerPrefix, tsplCommand)
    }

    fun holdBarcodeReader(hold: Boolean) {
        if (activity.isUseCamera) {
            val cameraFragment = activity.supportFragmentManager.findFragmentById(R.id.fragment_printlabel_camera) as? ScannerCameraFragment
            cameraFragment?.setHold(hold)
        } else {
            val barcodeFragment = activity.supportFragmentManager.findFragmentById(R.id.fragment_printlabel_barcode) as? ScannerBarcodeFragment
            barcodeFragment?.setHold(hold)
        }
    }

    fun soundBarcodeFound() {
		try {
			val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
			toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
		} catch (e: Exception) {
			Log.e("PrintlabelCamera", "Failed to play beep: ${e.message}")
		}
    }

    fun soundBarcodeNotFound() {
		try {
			// Menggunakan STREAM_ALARM agar suara lebih keras/menggelegar
			val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 100)
			// TONE_SUP_ERROR memberikan suara alert error yang tegas
			toneGen.startTone(ToneGenerator.TONE_SUP_ERROR, 500)
		} catch (e: Exception) {
			Log.e("PrintlabelCamera", "Failed to play alert: ${e.message}")
		}
    }
}
