package com.ferrine.stockopname.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Barcode(
    val barcode: String = "",
    val itemId: String = ""
) : Parcelable
