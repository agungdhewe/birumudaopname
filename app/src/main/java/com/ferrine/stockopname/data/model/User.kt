package com.ferrine.stockopname.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val username: String = "",
    val fullname: String = "",
    val password: String = "",
    val isAdmin: Boolean = false,
    val allowOpname: Boolean = false,
    val allowReceiving: Boolean = false,
    val allowTransfer: Boolean = false,
    val allowPrintlabel: Boolean = false
) : Parcelable
