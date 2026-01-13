package com.birumuda.stockopname.ui.main

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.MaterialToolbar
import com.birumuda.stockopname.BaseDrawerActivity
import com.birumuda.stockopname.R

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
