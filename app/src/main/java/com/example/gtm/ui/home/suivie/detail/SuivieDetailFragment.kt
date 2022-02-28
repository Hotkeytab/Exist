package com.example.gtm.ui.home.suivie.detail

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.response.DataX
import com.example.gtm.data.entities.response.Survey
import com.example.gtm.databinding.FragmentSuivieDetailBinding
import com.example.gtm.ui.home.mytask.survey.quiz.QuizAdapter
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuivieDetailFragment : Fragment(), SuivieDetailFragmentAdapter.SuivieDetailItemListener {

    private lateinit var binding: FragmentSuivieDetailBinding
    private var storeName: String? = ""
    private lateinit var adapterSurvey: SuivieDetailFragmentAdapter
    lateinit var sharedPref: SharedPreferences
    var afterSuiviArray = ArrayList<Survey>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSuivieDetailBinding.inflate(inflater, container, false)

        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        storeName = sharedPref.getString("storeName", "")

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = storeName

        binding.backFromDetail.setOnClickListener {
            requireActivity().finish()
        }

        getListOfSurveys()
    }

    override fun onClickedQuiz(quiz: Survey, surveyId: Int) {
        val responsJson: String = Gson().toJson(quiz)

        putQuestionName(quiz.name)
        val bundle = bundleOf("quizObject" to responsJson)

        sharedPref =
            requireContext().getSharedPreferences(
                R.string.app_name.toString(),
                Context.MODE_PRIVATE
            )!!
        with(sharedPref.edit()) {
            this?.putInt("surveyId", surveyId)
        }?.commit()

        findNavController().navigate(R.id.action_suivieDetailFragment_to_categoryDetailFragment, bundle)
    }

    private fun setupRecycleViewSurvey() {

        adapterSurvey = SuivieDetailFragmentAdapter(this, requireActivity())
        binding.quizRecycleview.isMotionEventSplittingEnabled = false
        binding.quizRecycleview.layoutManager = LinearLayoutManager(requireContext())
        binding.quizRecycleview.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.quizRecycleview.adapter = adapterSurvey
        adapterSurvey.setItems(afterSuiviArray)
        binding.progressIndicator.visibility = View.GONE

    }


    private fun getListOfSurveys() {
        for (i in (activity as SuiviDetailActivity).afterSuiviArray) {
            afterSuiviArray.add(i.survey)
        }

        setupRecycleViewSurvey()
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