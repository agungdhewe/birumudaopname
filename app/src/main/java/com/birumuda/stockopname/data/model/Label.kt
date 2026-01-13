package com.birumuda.stockopname.data.model

import java.math.BigDecimal

data class Label(
	val barcode: String,
	val description: String,
	val category: String,
	val price: BigDecimal,
	val discount: String,
	val oldPrice: BigDecimal,
	val pricingCode: String
)
