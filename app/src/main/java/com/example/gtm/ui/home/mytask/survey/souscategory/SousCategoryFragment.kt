package com.example.gtm.ui.home.mytask.survey.souscategory

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
import com.example.gtm.databinding.FragmentSousCategoryBinding
import com.example.gtm.ui.home.mytask.survey.category.CategoryAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SousCategoryFragment : Fragment(), SousCategoryAdapter.SousCategoryItemListener {

    private lateinit var binding: FragmentSousCategoryBinding
    private lateinit var adapterSousCategory: SousCategoryAdapter
    private val listaSousCategory = ArrayList<Survey>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSousCategoryBinding.inflate(inflater, container, false)



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backFromQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_sousCategoryFragment_to_categoryFragment)
        }
        statisData()

    }

    private fun statisData()
    {


        val newOne = Survey(1)

        listaSousCategory.add(newOne)
        listaSousCategory.add(newOne)
        listaSousCategory.add(newOne)

        setupRecycleViewCategory()
    }

    private fun setupRecycleViewCategory() {

        adapterSousCategory = SousCategoryAdapter(this, requireActivity())
        binding.sousCategoryRecycleview.isMotionEventSplittingEnabled = false
        binding.sousCategoryRecycleview.layoutManager = LinearLayoutManager(requireContext())
        binding.sousCategoryRecycleview.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.sousCategoryRecycleview.adapter = adapterSousCategory
        adapterSousCategory.setItems(listaSousCategory)
    }

    override fun onClickedSousCategory(sousCategoryId: Int) {
        findNavController().navigate(R.id.action_sousCategoryFragment_to_questionFragment)
    }

}