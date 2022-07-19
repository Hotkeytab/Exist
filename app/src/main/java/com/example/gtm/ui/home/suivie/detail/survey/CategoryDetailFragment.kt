package com.example.gtm.ui.home.suivie.detail.survey

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.quiz.QuestionCategory
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.quiz.QuizData
import com.example.gtm.databinding.FragmentCategoryDetailBinding
import com.example.gtm.ui.home.suivie.detail.SuiviDetailActivity
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CategoryDetailFragment : Fragment(), CategoryDetailNewAdapter.CategoryDetailNewItemListener {

    private lateinit var binding: FragmentCategoryDetailBinding
    private lateinit var adapterDetailCategory: CategoryDetailNewAdapter
    private var listaCategory = ArrayList<QuestionCategory>()
    private var myVal: String? = ""
    lateinit var sharedPref: SharedPreferences
    private var questionName: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryDetailBinding.inflate(inflater, container, false)



        //Get Quiz Object as String from passed arguments
        myVal = arguments?.getString("quizObject")


        //Get QUestion Name from shared pref
        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        questionName = sharedPref.getString("questionName", "")

        //Init myVal if null
        if (myVal == null)
            myVal = ""



        //Convert myVal Json to QUizData Object
        val gson = Gson()
        val objectList = gson.fromJson(myVal, QuizData::class.java)


        if (objectList != null)
            listaCategory = objectList.questionCategories as ArrayList<QuestionCategory>

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set Title text
        binding.title.text = questionName


        //Back to previous fragment
        binding.backFromQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_categoryDetailFragment_to_suivieDetailFragment)
        }

        setupRecycleViewCategory()
    }

    override fun onClickedCategory(categoryId: Int) {
    }


    private fun setupRecycleViewCategory() {

        adapterDetailCategory =
            CategoryDetailNewAdapter(this, requireActivity(), myVal!!,(activity as SuiviDetailActivity))
        Log.d("valmeher",myVal.toString())

        binding.categoryRecycleview.isMotionEventSplittingEnabled = false
        binding.categoryRecycleview.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryRecycleview.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.categoryRecycleview.adapter = adapterDetailCategory
        adapterDetailCategory.setItems(listaCategory)
    }



}