package com.example.gtm.ui.home.mytask.survey.quiz

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.response.Quiz
import com.example.gtm.data.entities.response.QuizData
import com.example.gtm.databinding.FragmentQuizBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.utils.resources.Resource
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class QuizFragment : Fragment(), QuizAdapter.QuizItemListener {

    private lateinit var binding: FragmentQuizBinding
    private lateinit var adapterSurvey: QuizAdapter
    private lateinit var responseDataQuiz: Resource<Quiz>
    private var listaQuiz = ArrayList<QuizData>()
    private val viewModel: MyQuizViewModel by viewModels()
    private var storeName: String? = ""
    lateinit var sharedPref: SharedPreferences



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)


        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        storeName = sharedPref.getString("storeName", "")

        requireActivity().bottom_nav.visibility = View.GONE
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.backFromQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_quizFragment_to_taskFragment)
        }

        binding.title.text = storeName

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
        binding.progressIndicator.visibility = View.GONE

    }

    override fun onClickedQuiz(quiz: QuizData,surveyId:Int) {

        val responsJson: String = Gson().toJson(quiz)

        putQuestionName(quiz.name)
        val bundle = bundleOf("quizObject" to responsJson)

        sharedPref =
            requireContext().getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE)!!
        with(sharedPref.edit()) {
            this?.putInt("surveyId", surveyId)
        }?.commit()

        findNavController().navigate(R.id.action_quizFragment_to_categoryFragment, bundle)

    }

    @DelicateCoroutinesApi
    private fun getVisites() {
        binding.progressIndicator.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.Main) {

            responseDataQuiz = viewModel.getSurvey()

            if (responseDataQuiz.responseCode == 200) {
                listaQuiz = responseDataQuiz.data!!.data as ArrayList<QuizData>
                setupRecycleViewSurvey()
            }

        }
    }


    private fun putQuestionName(questionName:String)
    {
        sharedPref =
          requireContext().getSharedPreferences(
                R.string.app_name.toString(),
                Context.MODE_PRIVATE
            )!!
        with(sharedPref.edit()) {
            this?.putString("questionName", questionName)
        }?.commit()
    }

}