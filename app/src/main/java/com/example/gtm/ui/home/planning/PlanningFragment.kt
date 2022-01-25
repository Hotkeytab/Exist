package com.example.gtm.ui.home.planning

import android.icu.number.IntegerWidth
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gtm.databinding.FragmentPlanningBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PlanningFragment : Fragment() {

    private lateinit var binding: FragmentPlanningBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlanningBinding.inflate(inflater, container, false)



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}