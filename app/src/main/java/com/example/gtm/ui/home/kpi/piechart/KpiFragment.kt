package com.example.gtm.ui.home.kpi.piechart

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.gtm.R
import com.example.gtm.databinding.FragmentKpiBinding
import com.example.gtm.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jjoe64.graphview.series.DataPoint
import dagger.hilt.android.AndroidEntryPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_kpi.*
import com.jjoe64.graphview.series.BarGraphSeries
import kotlin.math.abs
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.animation.Easing

import com.github.mikephil.charting.formatter.PercentFormatter

import com.github.mikephil.charting.data.PieData

import com.github.mikephil.charting.data.PieDataSet

import com.github.mikephil.charting.utils.ColorTemplate

import com.github.mikephil.charting.data.PieEntry




@AndroidEntryPoint
class KpiFragment : Fragment() {

    private lateinit var binding: FragmentKpiBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKpiBinding.inflate(inflater,container,false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPieChart()
        loadPieChartData()
    }


    private fun setupPieChart() {
        binding.activityPiechart.isDrawHoleEnabled = true
        binding.activityPiechart.setUsePercentValues(true)
        binding.activityPiechart.setEntryLabelTextSize(12f)
        binding.activityPiechart.setEntryLabelColor(Color.BLACK)
        binding.activityPiechart.centerText = "Questionnaires par ville"
        binding.activityPiechart.setCenterTextSize(24f)
        binding.activityPiechart.description.isEnabled = false
        val l: Legend = binding.activityPiechart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.isEnabled = true
    }


    private fun loadPieChartData() {
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(0.3f, "Gabès"))
        entries.add(PieEntry(0.15f, "Arianna"))
        entries.add(PieEntry(0.10f, "Sfax"))
        val colors: ArrayList<Int> = ArrayList()
        for (color in ColorTemplate.MATERIAL_COLORS) {
            colors.add(color)
        }
        for (color in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color)
        }
        val dataSet = PieDataSet(entries, "Questionnaires par ville")
        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(binding.activityPiechart))
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)
        binding.activityPiechart.data = data
        binding.activityPiechart.invalidate()
        binding.activityPiechart.animateY(1400, Easing.EaseInOutQuad)
    }


}