package com.ferrine.stockopname.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val username: String = "",
    val fullname: String = "",
    val password: String = ""
) : Parcelable
