package com.birumuda.stockopname.ui.receiving

import android.os.Bundle
import com.birumuda.stockopname.BaseScannerActivity
import com.birumuda.stockopname.R
import com.birumuda.stockopname.data.model.PrintLabelMode

class ReceivingActivity : BaseScannerActivity() {

    override var currentMode: PrintLabelMode = PrintLabelMode.RECEIVING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        setupDrawer(findViewById(R.id.toolbar))
        supportActionBar?.title = "Receiving"
        setupScanner()
    }
}
