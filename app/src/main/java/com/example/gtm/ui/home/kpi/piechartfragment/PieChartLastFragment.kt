package com.example.gtm.ui.home.kpi.piechartfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.example.gtm.R
import com.example.gtm.databinding.FragmentAnalyseSuperviseurBinding
import com.example.gtm.databinding.FragmentPieChartLastBinding
import com.example.gtm.ui.home.kpi.KpiFinalResultActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PieChartLastFragment : Fragment() {

    private lateinit var binding: FragmentPieChartLastBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPieChartLastBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}