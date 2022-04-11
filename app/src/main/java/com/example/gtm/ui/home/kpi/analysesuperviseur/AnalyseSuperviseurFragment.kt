package com.example.gtm.ui.home.kpi.analysesuperviseur

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import com.example.gtm.R
import com.example.gtm.databinding.FragmentAnalyseSuperviseurBinding
import com.example.gtm.databinding.FragmentKpiBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnalyseSuperviseurFragment : Fragment() {

    private lateinit var binding: FragmentAnalyseSuperviseurBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyseSuperviseurBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animationRightToLeft =
            AnimationUtils.loadAnimation(requireContext(), R.anim.right_to_left_kpi)



        animateResults(animationRightToLeft)


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

    }

}