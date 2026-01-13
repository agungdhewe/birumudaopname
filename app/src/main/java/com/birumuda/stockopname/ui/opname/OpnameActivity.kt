package com.birumuda.stockopname.ui.opname

import android.os.Bundle
import com.birumuda.stockopname.BaseScannerActivity
import com.birumuda.stockopname.R
import com.birumuda.stockopname.data.model.PrintLabelMode

class OpnameActivity : BaseScannerActivity() {

    override var currentMode: PrintLabelMode = PrintLabelMode.OPNAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        setupDrawer(findViewById(R.id.toolbar))
        supportActionBar?.title = "Opname"
        setupScanner()
    }
}
