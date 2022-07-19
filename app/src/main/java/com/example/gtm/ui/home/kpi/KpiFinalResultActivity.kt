package com.example.gtm.ui.home.kpi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gtm.R
import com.example.gtm.data.entities.custom.KpiStats
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_kpi_final_result.*


@AndroidEntryPoint
class KpiFinalResultActivity : AppCompatActivity() {

    var kpiStatsObject: KpiStats? = null
    var etatFragment = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kpi_final_result)


        etatFragment = 0

        val extra = intent.extras
        if (extra != null) {
            val myValue = extra.getString("kpiObject")

            val gson = Gson()
             kpiStatsObject = gson.fromJson(myValue, KpiStats::class.java)

        }



        back_from_kpi.setOnClickListener {
            finish()
        }



    }


}