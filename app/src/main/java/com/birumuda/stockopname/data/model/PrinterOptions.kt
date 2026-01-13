package com.birumuda.stockopname.data.model

enum class PrinterOptions(
    val displayName: String,
    val prefix: String
) {
    NONE(
        displayName = "Pilih Printer",
        prefix = ""
    ),
    EPSON(
        displayName = "Epson TM180",
        prefix = "TM180"
    ),
    KASSEN(
        displayName = "Kassen RPP02N",
        prefix = "RPP02N"
    );
}
