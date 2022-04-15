package com.example.gtm.ui.home.kpi.piechart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gtm.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_pie_chart_last.*
import android.content.Intent
import android.util.Log


@AndroidEntryPoint
class PieChartLastActivity : AppCompatActivity() {

    var villeHashMap: HashMap<String, Double> = HashMap<String, Double>()
    var magasinHashMap: HashMap<String, Double> = HashMap<String, Double>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pie_chart_last)



        back_from_pie_chart.setOnClickListener {
            finish()
        }

        villeHashMap.clear()
        magasinHashMap.clear()

        villeHashMap = intent.getSerializableExtra("villeMap") as HashMap<String, Double>
        magasinHashMap = intent.getSerializableExtra("magasinMap") as HashMap<String, Double>



        Log.i("nodatachart","$villeHashMap $magasinHashMap")

    }
}