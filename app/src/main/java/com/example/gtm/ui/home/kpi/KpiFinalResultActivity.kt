package com.example.gtm.ui.home.kpi

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.gtm.R
import com.example.gtm.data.entities.custom.KpiStats
import com.example.gtm.data.entities.response.QuestionSubCategory
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_kpi_final_result.*
import kotlinx.android.synthetic.main.fragment_position_map.*


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