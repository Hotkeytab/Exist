package com.example.gtm.ui.home.kpi.kpifilter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gtm.R
import com.example.gtm.data.entities.response.DataXX
import com.example.gtm.data.entities.response.GetStore
import com.example.gtm.data.entities.response.Quiz
import com.example.gtm.data.entities.response.QuizData
import com.example.gtm.databinding.FragmentKpiFIlterBinding
import com.example.gtm.databinding.FragmentKpiGraphBinding
import com.example.gtm.ui.home.mytask.addvisite.AddVisiteDialogViewModel
import com.example.gtm.ui.home.mytask.survey.quiz.MyQuizViewModel
import com.example.gtm.utils.resources.Resource
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_add_visite.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class KpiFIlterFragment : Fragment() {

    private lateinit var binding: FragmentKpiFIlterBinding
    private lateinit var responseDataStores: Resource<GetStore>
    private val viewModel: AddVisiteDialogViewModel by viewModels()
    private var listaDataXX = ArrayList<DataXX>()
    private lateinit var responseDataQuiz: Resource<Quiz>
    private var listaQuiz = ArrayList<QuizData>()
    private val viewModelQuestionnaire: MyQuizViewModel by viewModels()
    private val mapStore: HashMap<String, Int> = HashMap<String, Int>()
    private var arrayStore = ArrayList<String>()
    private val mapQuest: HashMap<String, Int> = HashMap<String, Int>()
    private var arrayQuest = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKpiFIlterBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.title = "Analyse Kpi"


        val items = listOf(
            "Ariana",
            "Béja",
            "Ben Arous",
            "Bizerte",
            "Gabès",
            "Nabeul",
            "Jendouba",
            "Kairouan",
            "Kasserine",
            "Kebili",
            "Kef",
            "Mahdia",
            "Manouba",
            "Medenine",
            "Monastir",
            "Gafsa",
            "Sfax",
            "Sidi Bouzid",
            "Siliana",
            "Sousse",
            "Tataouine",
            "Tozeur",
            "Zaghouan"
        )

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
        binding.villeText.setAdapter(arrayAdapter)


        getStores()


    }


    private fun addChipVille(name: String) {
        binding.chipGroupVille.addView(createTagChip(requireContext(), name))
    }

    private fun addChipMagasin(name: String) {
        binding.chipGroupMagasin.addView(createTagChip(requireContext(), name))
    }

    private fun addChipQuestionnaire(name: String) {
        binding.chipGroupQuestionnaire.addView(createTagChip(requireContext(), name))
    }


    private fun createTagChip(context: Context, chipName: String): Chip {
        return Chip(context).apply {
            text = chipName
            setChipBackgroundColorResource(R.color.purpleLogin)
            isCloseIconVisible = true
            setTextColor(ContextCompat.getColor(context, R.color.white))
            //setTextAppearance(R.style.ChipTextAppearance)
        }
    }


    private fun getStores() {

        lifecycleScope.launch(Dispatchers.Main) {


            responseDataStores = viewModel.getStores()

            if (responseDataStores.responseCode == 200) {
                progress_indicator.visibility = View.GONE
                listaDataXX = responseDataStores.data!!.data as ArrayList<DataXX>
                getSubListStore()
                getQuestionnaires()

            } else {

            }

        }
    }

    private fun getQuestionnaires() {
        lifecycleScope.launch(Dispatchers.Main) {

            responseDataQuiz = viewModelQuestionnaire.getSurvey()

            if (responseDataQuiz.responseCode == 200) {
                listaQuiz = responseDataQuiz.data!!.data as ArrayList<QuizData>
                getSubListQuestionnaire()
            } else {

            }

        }
    }

    private fun getSubListStore() {
        for (i in listaDataXX) {
            mapStore[i.name + " " + i.governorate + " " + i.address] = i.id
            arrayStore.add(i.name + " " + i.governorate + " " + i.address)
        }
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, arrayStore)
        binding.magasinText.setAdapter(arrayAdapter)

    }

    private fun getSubListQuestionnaire() {
        for (i in listaQuiz) {
            mapQuest[i.name] = i.id
            arrayQuest.add(i.name)
        }
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, arrayQuest)
        binding.questionnaireText.setAdapter(arrayAdapter)
    }


}