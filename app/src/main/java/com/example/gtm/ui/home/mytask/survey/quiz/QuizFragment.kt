package com.example.gtm.ui.home.mytask.survey.quiz

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.response.DataX
import com.example.gtm.data.entities.response.Quiz
import com.example.gtm.data.entities.response.QuizData
import com.example.gtm.databinding.FragmentQuizBinding
import com.example.gtm.utils.resources.Resource
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class QuizFragment : Fragment(),QuizAdapter.QuizItemListener {

    private lateinit var binding: FragmentQuizBinding
    private lateinit var adapterSurvey: QuizAdapter
    private lateinit var responseDataQuiz: Resource<Quiz>
    private var listaQuiz = ArrayList<QuizData>()
    private val viewModel: MyQuizViewModel by viewModels()


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
        getVisites()

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

    override fun onClickedQuiz(quiz: QuizData) {

        val responsJson : String  = Gson().toJson(quiz)

        val bundle = bundleOf("quizObject" to responsJson)
        findNavController().navigate(R.id.action_quizFragment_to_categoryFragment,bundle)

    }

    @DelicateCoroutinesApi
    private fun getVisites() {
        GlobalScope.launch(Dispatchers.Main) {

            responseDataQuiz = viewModel.getSurvey()

            if (responseDataQuiz.responseCode == 200) {
                listaQuiz = responseDataQuiz.data!!.data as ArrayList<QuizData>
                setupRecycleViewSurvey()
            }

        }
    }

}