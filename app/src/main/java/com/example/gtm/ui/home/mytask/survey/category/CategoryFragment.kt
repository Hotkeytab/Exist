package com.example.gtm.ui.home.mytask.survey.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.ui.Survey
import com.example.gtm.databinding.FragmentCategoryBinding
import com.example.gtm.databinding.FragmentQuizBinding
import com.example.gtm.ui.home.mytask.survey.quiz.QuizAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CategoryFragment : Fragment(), CategoryAdapter.CategoryItemListener {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var adapterCategory: CategoryAdapter
    private val listaCategory = ArrayList<Survey>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backFromQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_quizFragment)
        }
        statisData()

    }

    private fun statisData()
    {


        val newOne = Survey(1)

        listaCategory.add(newOne)
        listaCategory.add(newOne)
        listaCategory.add(newOne)

        setupRecycleViewCategory()
    }

    private fun setupRecycleViewCategory() {

        adapterCategory = CategoryAdapter(this, requireActivity())
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