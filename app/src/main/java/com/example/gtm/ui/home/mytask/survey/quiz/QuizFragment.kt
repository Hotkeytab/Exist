package com.example.gtm.ui.home.mytask.survey.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gtm.R
import com.example.gtm.databinding.FragmentPlanningBinding
import com.example.gtm.databinding.FragmentQuizBinding
import com.example.gtm.databinding.FragmentQuizBindingImpl
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QuizFragment : Fragment() {

    private lateinit var binding: FragmentQuizBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}