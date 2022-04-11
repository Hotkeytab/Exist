package com.example.gtm.ui.home.kpi

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gtm.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_kpi_final_result.*


@AndroidEntryPoint
class KpiFinalResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kpi_final_result)

        back_from_kpi.setOnClickListener {
            finish()
        }




        image_table_stats_card.setOnClickListener {
            image_table_stats.setColorFilter(Color.argb(255, 0, 0, 0))
            image_kpi_stats.setColorFilter(Color.argb(255, 220, 220, 220))
            pie_chart_text.setHintTextColor(resources.getColor(R.color.clear_grey))
            table_text.setTextColor(resources.getColor(R.color.purpleLogin))
            title_chart.text = "Analyses Supérviseur"
        }



        image_kpi_stats_card.setOnClickListener {
            image_kpi_stats.setColorFilter(Color.argb(255, 0, 0, 0))
            image_table_stats.setColorFilter(Color.argb(255, 220, 220, 220))
            pie_chart_text.setHintTextColor(resources.getColor(R.color.purpleLogin))
            table_text.setTextColor(resources.getColor(R.color.clear_grey))
            title_chart.text = "PieChart"
        }

    }


}