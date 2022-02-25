package com.example.gtm.ui.home.suivie.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gtm.R
import com.example.gtm.databinding.FragmentPlanningBinding
import com.example.gtm.databinding.FragmentSuivieDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuivieDetailFragment : Fragment() {

    private lateinit var binding: FragmentSuivieDetailBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSuivieDetailBinding.inflate(inflater, container, false)



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}