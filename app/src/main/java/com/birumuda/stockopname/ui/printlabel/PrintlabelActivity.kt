package com.birumuda.stockopname.ui.printlabel

import android.os.Bundle
import com.birumuda.stockopname.BaseScannerActivity
import com.birumuda.stockopname.R
import com.birumuda.stockopname.data.model.PrintLabelMode

class PrintlabelActivity : BaseScannerActivity() {

    override var currentMode: PrintLabelMode = PrintLabelMode.PRINT_LABEL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        setupDrawer(findViewById(R.id.toolbar))
        setupScanner()
    }
}
