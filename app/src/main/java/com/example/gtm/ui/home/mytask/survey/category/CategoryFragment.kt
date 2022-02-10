package com.example.gtm.ui.home.mytask.survey.category

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
import com.example.gtm.data.entities.response.Quiz
import com.example.gtm.data.entities.response.QuizData
import com.example.gtm.databinding.FragmentCategoryBinding
import com.example.gtm.utils.resources.Resource
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*


@AndroidEntryPoint
class CategoryFragment : Fragment(), CategoryAdapter.CategoryItemListener {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var adapterCategory: CategoryAdapter
    private var listaCategory = ArrayList<QuestionCategory>()
    lateinit var sharedPref: SharedPreferences
    private var questionName: String? = ""
    private var myVal :String? =""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        questionName = sharedPref.getString("questionName", "")

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = questionName

        requireActivity().bottom_nav.visibility = View.VISIBLE

        binding.backFromQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_quizFragment)
        }

         myVal = arguments?.getString("quizObject")

        val gson = Gson()
        val objectList = gson.fromJson(myVal, QuizData::class.java)


        if (objectList != null)
            listaCategory = objectList.questionCategories as ArrayList<QuestionCategory>


        setupRecycleViewCategory()

        // val objectList = gson.fromJson(json, Array<SomeObject>::class.java).asList()
    }


    private fun setupRecycleViewCategory() {

        adapterCategory = CategoryAdapter(this, requireActivity(),myVal!!)
        binding.categoryRecycleview.isMotionEventSplittingEnabled = false
        binding.categoryRecycleview.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryRecycleview.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.categoryRecycleview.adapter = adapterCategory
        adapterCategory.setItems(listaCategory)
    }

    override fun onClickedCategory(categoryId: Int) {
        //  findNavController().navigate(R.id.action_categoryFragment_to_sousCategoryFragment)
    }

}