package com.example.gtm.ui.home.kpi

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


       /* val lineSeries: LineGraphSeries<DataPoint> = LineGraphSeries(
            arrayOf(
                DataPoint(0.0, 1.0),
                DataPoint(1.0, 5.0),
                DataPoint(2.0, 3.0),
                DataPoint(3.0, 2.0),
                DataPoint(4.0, 12.0)
            )
        )
        line_graph.addSeries(lineSeries)


        val barGraph_Data: BarGraphSeries<DataPoint> = BarGraphSeries(
            arrayOf(
                DataPoint(0.0, 1.0),
                DataPoint(1.0, 5.0),
                DataPoint(2.0, 3.0),
                DataPoint(3.0, 2.0),
                DataPoint(4.0, 12.0)
            )
        )
        bar_graph.addSeries(barGraph_Data)


    /*    barGraph_Data.setValueDependentColor(new ValueDependentColor<Data>() {
            @Override
            public int get(Data info) {
                return Color.rgb((int) info.getX()*255/4, (int) Math.abs(info.getY()*255/6), 100);
            }
        }) */


        barGraph_Data.setValueDependentColor {
            return@setValueDependentColor Color.rgb( (it.x *255/4).toInt(),  (abs(it.y *255/6)).toInt(), 100)
        }

        barGraph_Data.spacing = 20

        barGraph_Data.isDrawValuesOnTop = true
        barGraph_Data.valuesOnTopColor = Color.RED
        */

        setupPieChart()
        loadPieChartData()
    }


    private fun setupPieChart() {
        binding.activityPiechart.isDrawHoleEnabled = true
        binding.activityPiechart.setUsePercentValues(true)
        binding.activityPiechart.setEntryLabelTextSize(12f)
        binding.activityPiechart.setEntryLabelColor(Color.BLACK)
        binding.activityPiechart.centerText = "Spending by Category"
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
        entries.add(PieEntry(0.2f, "Food & Dining"))
        entries.add(PieEntry(0.15f, "Medical"))
        entries.add(PieEntry(0.10f, "Entertainment"))
        val colors: ArrayList<Int> = ArrayList()
        for (color in ColorTemplate.MATERIAL_COLORS) {
            colors.add(color)
        }
        for (color in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color)
        }
        val dataSet = PieDataSet(entries, "Expense Category")
        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(binding.activityPiechart))
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)
        binding.activityPiechart.setData(data)
        binding.activityPiechart.invalidate()
        binding.activityPiechart.animateY(1400, Easing.EaseInOutQuad)
    }


}