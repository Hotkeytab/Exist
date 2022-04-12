package com.example.gtm.ui.home.kpi.analysesuperviseur

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.gtm.R
import com.example.gtm.databinding.FragmentAnalyseSuperviseurBinding
import com.example.gtm.databinding.FragmentKpiBinding
import com.example.gtm.ui.home.kpi.KpiFinalResultActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_kpi_final_result.*
import kotlinx.android.synthetic.main.fragment_position_map.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnalyseSuperviseurFragment : Fragment() {

    private lateinit var binding: FragmentAnalyseSuperviseurBinding
    private lateinit var kpiActivity: KpiFinalResultActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyseSuperviseurBinding.inflate(inflater, container, false)

        kpiActivity = (activity as KpiFinalResultActivity)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animationRightToLeft =
            AnimationUtils.loadAnimation(requireContext(), R.anim.right_to_left_kpi)



        animateResults(animationRightToLeft)
        prepareStats()


    }


    private fun animateResults(rightToLeft: Animation) {

        binding.performance.animation = rightToLeft
        binding.moyenneRetard.animation = rightToLeft
        binding.moyenneDerniersPointage.animation = rightToLeft
        binding.visitesPlanifie.animation = rightToLeft
        binding.visitesNonPlanifie.animation = rightToLeft
        binding.visitesRealise.animation = rightToLeft
        binding.questionnaireRealise.animation = rightToLeft
        binding.moyenneQuestionnaire.animation = rightToLeft
        binding.nombrePhoto.animation = rightToLeft


        /*kpiActivity.image_kpi_stats_card.setOnClickListener {
            findNavController().navigate(R.id.action_analyseSuperviseurFragment_to_pieChartLastFragment)
        } */


        kpiActivity.image_table_stats_card.setOnClickListener {

            if (kpiActivity.etatFragment == 1) {
                kpiActivity.etatFragment = 0
                kpiActivity.image_table_stats.setColorFilter(Color.argb(255, 0, 0, 0))
                kpiActivity.image_kpi_stats.setColorFilter(Color.argb(255, 220, 220, 220))
                kpiActivity.pie_chart_text.setHintTextColor(resources.getColor(R.color.clear_grey))
                kpiActivity.table_text.setTextColor(resources.getColor(R.color.purpleLogin))
                kpiActivity.title_chart.text = "Analyses Supérviseur"
                findNavController().navigate(R.id.action_pieChartLastFragment_to_analyseSuperviseurFragment)
            }
        }



        kpiActivity.image_kpi_stats_card.setOnClickListener {

            if(kpiActivity.etatFragment == 0) {
                kpiActivity.etatFragment = 1
                kpiActivity.image_kpi_stats.setColorFilter(Color.argb(255, 0, 0, 0))
                kpiActivity.image_table_stats.setColorFilter(Color.argb(255, 220, 220, 220))
                kpiActivity.pie_chart_text.setHintTextColor(resources.getColor(R.color.purpleLogin))
                kpiActivity.table_text.setTextColor(resources.getColor(R.color.clear_grey))
                kpiActivity.title_chart.text = "PieChart"
                findNavController().navigate(R.id.action_analyseSuperviseurFragment_to_pieChartLastFragment)
            }
        }

    }

    private fun prepareStats() {
        binding.performanceText.text = "${kpiActivity.kpiStatsObject?.performance}%"
        binding.moyenneRetardText.text = "${kpiActivity.kpiStatsObject?.moyenneRetard}\nMinutes"
        binding.moyenneDerniersPointageText.text =
            "${kpiActivity.kpiStatsObject?.moyenneDernierPointage}"
        binding.visitesPlanifieText.text = "${kpiActivity.kpiStatsObject?.visitesPlanifie}"
        binding.visitesNonPlanifieText.text = "${kpiActivity.kpiStatsObject?.visitesNonPlanifie}"
        binding.visitesRealiseText.text = "${kpiActivity.kpiStatsObject?.visitesRealise}"
        binding.questionnaireRealiseText.text =
            "${kpiActivity.kpiStatsObject?.questionnaireRealise}"
        binding.nombrePhotoText.text = "${kpiActivity.kpiStatsObject?.nombrePhotos}"
        binding.moyenneQuestionnaireText.text =
            "${kpiActivity.kpiStatsObject?.moyenneQuestionnaire}"
    }

}