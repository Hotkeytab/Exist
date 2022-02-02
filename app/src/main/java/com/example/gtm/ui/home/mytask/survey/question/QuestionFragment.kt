package com.example.gtm.ui.home.mytask.survey.question

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.ui.Survey
import com.example.gtm.databinding.FragmentQuestionBinding
import com.example.gtm.databinding.FragmentSousCategoryBinding
import com.example.gtm.ui.home.mytask.survey.souscategory.SousCategoryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionFragment : Fragment() {

    private lateinit var binding: FragmentQuestionBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionBinding.inflate(inflater, container, false)



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backFromQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_questionFragment_to_sousCategoryFragment)
        }


    }


}

