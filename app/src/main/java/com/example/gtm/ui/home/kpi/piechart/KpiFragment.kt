package com.example.gtm.ui.home.kpi.piechart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.gtm.R
import com.example.gtm.data.entities.custom.QuestionNewPost
import com.example.gtm.data.entities.custom.UserInf
import com.example.gtm.databinding.FragmentKpiBinding
import dagger.hilt.android.AndroidEntryPoint
import com.github.mikephil.charting.data.BarData

import com.github.mikephil.charting.data.BarDataSet

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieData

import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.animation.Easing

import com.github.mikephil.charting.formatter.PercentFormatter

import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_pie_chart_last.*


@AndroidEntryPoint
class KpiFragment : Fragment() {

    private lateinit var binding: FragmentKpiBinding
    private lateinit var pieChartLastActivity: PieChartLastActivity
    private var entries: ArrayList<PieEntry> = ArrayList()
    private var etatGroup = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKpiBinding.inflate(inflater, container, false)

        //Get instance of PieChartLastActivity
        pieChartLastActivity = activity as PieChartLastActivity

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //SetupPieChart Design
        setupPieChart()

        //Prepare Entry Arraylist for piechart
        preparePieChartData()

        //Load PieCHartData with Data
        loadPieChartData()


        //Change filter to "Par Magasin"
        pieChartLastActivity.par_magasin_cardview.setOnClickListener {
            if (etatGroup == 1) {
                etatGroup = 0
                pieChartLastActivity.image_table_stats.setColorFilter(Color.argb(255, 0, 0, 0))
                pieChartLastActivity.image_kpi_stats.setColorFilter(Color.argb(255, 220, 220, 220))
                pieChartLastActivity.pie_chart_text.setHintTextColor(resources.getColor(R.color.clear_grey))
                pieChartLastActivity.table_text.setTextColor(resources.getColor(R.color.purpleLogin))
                binding.piechart.centerText = "Par\nMagasin"

                //Prepare Entry Arraylist for piechart
                preparePieChartData()
            }


        }

        //Change filter to "Par Ville"
        pieChartLastActivity.par_ville_card.setOnClickListener {

            if (etatGroup == 0) {
                etatGroup = 1
                pieChartLastActivity.image_kpi_stats.setColorFilter(Color.argb(255, 0, 0, 0))
                pieChartLastActivity.image_table_stats.setColorFilter(
                    Color.argb(
                        255,
                        220,
                        220,
                        220
                    )
                )
                pieChartLastActivity.pie_chart_text.setHintTextColor(resources.getColor(R.color.purpleLogin))
                pieChartLastActivity.table_text.setTextColor(resources.getColor(R.color.clear_grey))
                binding.piechart.centerText = "Par\nVille"

                //Prepare Entry Arraylist for piechart
                preparePieChartData()
            }

        }

    }


    //SetupPieChart Design
    private fun setupPieChart() {
        binding.piechart.isDrawHoleEnabled = true
        binding.piechart.setUsePercentValues(false)
        binding.piechart.setEntryLabelTextSize(12f)
        binding.piechart.setEntryLabelColor(Color.BLACK)
        binding.piechart.centerText = "Par\nMagasin"
        binding.piechart.setCenterTextSize(24f)
        binding.piechart.description.isEnabled = false
        val l: Legend = binding.piechart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.isEnabled = true
    }


    //Prepare Entry Arraylist
    private fun preparePieChartData() {
        //clear PieEntry Arraylist
        entries.clear()

        //Prepare entry arraylist with store ( par magasin )
        if (etatGroup == 0) {

            if (pieChartLastActivity.magasinHashMap.isEmpty()) {
                binding.nodatachart.visibility = View.VISIBLE
                binding.piechart.visibility = View.GONE
            } else {

                binding.nodatachart.visibility = View.GONE
                binding.piechart.visibility = View.VISIBLE
                for (i in pieChartLastActivity.magasinHashMap) {
                    entries.add(PieEntry(i.value.toFloat(), i.key))
                }


                //Load PieCHartData with Data
                loadPieChartData()
            }
        }

        //Prepare entry arraylist with ville ( par ville )
        else {
            if (pieChartLastActivity.villeHashMap.isEmpty()) {
                binding.nodatachart.visibility = View.VISIBLE
                binding.piechart.visibility = View.GONE
            } else {
                binding.nodatachart.visibility = View.GONE
                binding.piechart.visibility = View.VISIBLE
                for (i in pieChartLastActivity.villeHashMap) {
                    entries.add(PieEntry(i.value.toFloat(), i.key))
                }


                //Load PieCHartData with Data
                loadPieChartData()
            }
        }


    }


    //Load PieCHartData with Data
    private fun loadPieChartData() {

        val colors: ArrayList<Int> = ArrayList()
        for (color in ColorTemplate.MATERIAL_COLORS) {
            colors.add(color)
        }
        for (color in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color)
        }

        var dataSet = PieDataSet(entries, "Par Magasin")
        if (etatGroup == 1)
            dataSet = PieDataSet(entries, "Par Ville")
        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)
        binding.piechart.data = data
        binding.piechart.invalidate() // refresh pie chart
        binding.piechart.animateY(1400, Easing.EaseInOutQuad)
    }


}