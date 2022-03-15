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
import com.example.gtm.data.entities.response.QuestionCategory
import com.example.gtm.data.entities.response.QuizData
import com.example.gtm.data.entities.response.Survey
import com.example.gtm.databinding.FragmentCategoryDetailBinding
import com.example.gtm.databinding.FragmentSuivieDetailBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.mytask.survey.category.CategoryAdapter
import com.example.gtm.ui.home.suivie.detail.SuiviDetailActivity
import com.example.gtm.ui.home.suivie.detail.SuivieDetailFragmentAdapter
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CategoryDetailFragment : Fragment(), CategoryDetailAdapter.CategoryDetailItemListener {

    private lateinit var binding: FragmentCategoryDetailBinding
    private lateinit var adapterDetailCategory: CategoryDetailAdapter
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



        myVal = arguments?.getString("quizObject")

        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        questionName = sharedPref.getString("questionName", "")

        if (myVal == null)
            myVal = ""



        val gson = Gson()
        val objectList = gson.fromJson(myVal, QuizData::class.java)


        if (objectList != null)
            listaCategory = objectList.questionCategories as ArrayList<QuestionCategory>

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = questionName

        binding.backFromQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_categoryDetailFragment_to_suivieDetailFragment)
        }

        setupRecycleViewCategory()
    }

    override fun onClickedCategory(categoryId: Int) {
    }


    private fun setupRecycleViewCategory() {

        adapterDetailCategory =
            CategoryDetailAdapter(this, requireActivity(), myVal!!)
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