package com.ferrine.stockopname.ui.printlabel

import android.os.Bundle
import com.ferrine.stockopname.BaseScannerActivity
import com.ferrine.stockopname.R
import com.ferrine.stockopname.data.model.PrintLabelMode

class PrintlabelActivity : BaseScannerActivity() {

    override var currentMode: PrintLabelMode = PrintLabelMode.PRINT_LABEL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        setupDrawer(findViewById(R.id.toolbar))
        setupScanner()
    }
}
