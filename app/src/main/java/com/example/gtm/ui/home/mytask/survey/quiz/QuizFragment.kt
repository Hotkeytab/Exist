package com.example.gtm.ui.home.mytask.survey.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.ui.Survey
import com.example.gtm.databinding.FragmentPlanningBinding
import com.example.gtm.databinding.FragmentQuizBinding
import com.example.gtm.databinding.FragmentQuizBindingImpl
import com.example.gtm.ui.home.mytask.TaskAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*


@AndroidEntryPoint
class QuizFragment : Fragment(),QuizAdapter.QuizItemListener {

    private lateinit var binding: FragmentQuizBinding
    private lateinit var adapterSurvey: QuizAdapter
    private val listaQuiz = ArrayList<Survey>()


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

        binding.backFromQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_quizFragment_to_taskFragment)
        }
        statisData()

    }

    private fun statisData()
    {


        val newOne = Survey(1)

        listaQuiz.add(newOne)
        listaQuiz.add(newOne)
        listaQuiz.add(newOne)

        setupRecycleViewSurvey()
    }

    private fun setupRecycleViewSurvey() {

        adapterSurvey = QuizAdapter(this, requireActivity())
        binding.quizRecycleview.isMotionEventSplittingEnabled = false
        binding.quizRecycleview.layoutManager = LinearLayoutManager(requireContext())
        binding.quizRecycleview.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.quizRecycleview.adapter = adapterSurvey
        adapterSurvey.setItems(listaQuiz)
    }

    override fun onClickedQuiz(quizId: Int) {
        findNavController().navigate(R.id.action_quizFragment_to_categoryFragment)
    }

}