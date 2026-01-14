package com.ferrine.stockopname.ui.main

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.ferrine.stockopname.BaseDrawerActivity
import com.ferrine.stockopname.R
import com.ferrine.stockopname.data.model.WorkingTypes
import com.ferrine.stockopname.ui.setting.SettingActivity

class MainActivity : BaseDrawerActivity() {

    private lateinit var tvWorkingType: TextView
    private lateinit var tvSiteCode: TextView
    private lateinit var tvBrandCode: TextView

    private val prefs by lazy {
        getSharedPreferences(SettingActivity.PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupDrawer(toolbar)

        bindView()
    }

    private fun bindView() {
        tvWorkingType = findViewById(R.id.tvWorkingType)
        tvSiteCode = findViewById(R.id.tvSiteCode)
        tvBrandCode = findViewById(R.id.tvBrandCode)
    }

    override fun onResume() {
        super.onResume()
        displaySettings()
    }

    private fun displaySettings() {
        val siteCode = prefs.getString(SettingActivity.KEY_SITE_CODE, "-") ?: "-"
        val brandCode = prefs.getString(SettingActivity.KEY_BRAND_CODE, "-") ?: "-"
        val workingTypeName = prefs.getString(SettingActivity.KEY_WORKING_TYPE, WorkingTypes.NONE.name)
        
        val workingType = try {
            WorkingTypes.valueOf(workingTypeName ?: WorkingTypes.NONE.name)
        } catch (e: Exception) {
            WorkingTypes.NONE
        }

        tvWorkingType.text = workingType.displayName
        
        // Jika belum pilih working type, warnanya merah
        if (workingType == WorkingTypes.NONE) {
            tvWorkingType.setTextColor(Color.RED)
        } else {
            tvWorkingType.setTextColor(Color.BLACK)
        }

        tvSiteCode.text = siteCode
        tvBrandCode.text = brandCode
    }

    override fun drawerIconColor(): Int {
        return android.R.color.black
    }
}
