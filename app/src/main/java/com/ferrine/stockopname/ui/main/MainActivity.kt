package com.ferrine.stockopname.ui.main

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.MaterialToolbar
import com.ferrine.stockopname.BaseDrawerActivity
import com.ferrine.stockopname.R

class MainActivity : BaseDrawerActivity() {


	private lateinit var toolbar: MaterialToolbar

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		setupDrawer(toolbar)

	}

	override fun drawerIconColor(): Int {
		return android.R.color.black
	}


}
