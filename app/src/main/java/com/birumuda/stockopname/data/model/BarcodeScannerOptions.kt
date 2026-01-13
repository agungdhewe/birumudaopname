package com.birumuda.stockopname.data.model

enum class BarcodeScannerOptions(
	val displayName: String,
	val isUseCamera: Boolean

) {
	SCANNER(
		displayName = "Barcode Scanner",
		isUseCamera = false,
	),
	CAMERA(
		displayName = "Device Camera",
		isUseCamera = true,
	);

}