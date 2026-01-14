package com.ferrine.stockopname.data.model

enum class WorkingTypes (
	val displayName: String
) {
	NONE(
		displayName = "Pilih Working Type",
	),
	OPNAME(
		displayName = "Opname",
	),
	RECEIVING(
		displayName = "Receiving",
	),
	TRANSFER(
		displayName = "Transfer",
	),
	PRINTLABEL(
		displayName = "Print Label"
	);
}
