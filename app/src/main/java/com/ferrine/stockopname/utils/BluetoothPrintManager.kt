package com.ferrine.stockopname.utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID

class BluetoothPrintManager(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    // Mutex untuk memastikan hanya satu proses printing yang berjalan dalam satu waktu
    private val mutex = Mutex()

    suspend fun sendToPrinter(prefix: String, command: String) = mutex.withLock {
        withContext(Dispatchers.IO) {
            val adapter = bluetoothAdapter ?: throw Exception("Bluetooth tidak tersedia di perangkat ini")
            
            if (!adapter.isEnabled) {
                throw Exception("Bluetooth tidak aktif")
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
            ) {
                throw Exception("Izin Bluetooth tidak diberikan")
            }

            val targetDevice = adapter.bondedDevices?.find {
                it.name?.startsWith(prefix, ignoreCase = true) == true
            } ?: throw Exception("Printer '$prefix' tidak ditemukan di daftar paired devices")

            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

            runCatching {
                targetDevice.createRfcommSocketToServiceRecord(uuid).use { socket ->
                    socket.connect()
                    socket.outputStream.use { output ->
                        output.write(command.toByteArray(Charsets.US_ASCII))
                        output.flush()
                        // Memberi sedikit jeda agar buffer terkirim sepenuhnya sebelum socket ditutup
                        Thread.sleep(200)
                    }
                }
            }.onFailure { e ->
                throw Exception("Gagal terhubung ke printer: ${e.message}")
            }
        }
        // Jeda tambahan setelah koneksi ditutup sebelum mengizinkan print berikutnya (cool-down printer)
        delay(500)
    }
}
