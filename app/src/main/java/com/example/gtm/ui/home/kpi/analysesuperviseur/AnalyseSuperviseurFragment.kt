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

        //Get Instance of KpiFinalResultActivity
        kpiActivity = (activity as KpiFinalResultActivity)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Init Animation From Right to Left
        val animationRightToLeft =
            AnimationUtils.loadAnimation(requireContext(), R.anim.right_to_left_kpi)


        //Start Animation for all Cards
        animateResults(animationRightToLeft)

        //Fill Cards with Stats Results
        prepareStats()

    }


    //Start Animation for all Cards
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
    }

    //Fill Cards with Stats Results
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