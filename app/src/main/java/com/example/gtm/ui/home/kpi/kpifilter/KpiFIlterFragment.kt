package com.example.gtm.ui.home.kpi.kpifilter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
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
import kotlinx.android.synthetic.main.dialog_add_visite.progress_indicator
import kotlinx.android.synthetic.main.fragment_kpi_f_ilter.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.widget.AdapterView

import android.widget.AdapterView.OnItemClickListener
import java.text.SimpleDateFormat


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
    private var arrayVilleNew = ArrayList<String>()
    private var arrayStoreNew = ArrayList<String>()
    private var arrayQuestNew = ArrayList<String>()
    private var type = ""

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

        binding.imageTableStats.setColorFilter(Color.argb(255, 0, 0, 255))
        binding.imageKpiStats.setColorFilter(Color.argb(255, 220, 220, 220))

        binding.imageTableStats.setOnClickListener {
            binding.imageTableStats.setColorFilter(Color.argb(255, 0, 0, 255))
            binding.imageKpiStats.setColorFilter(Color.argb(255, 220, 220, 220))
            type = "table"
        }

        binding.imageKpiStats.setOnClickListener {
            binding.imageKpiStats.setColorFilter(Color.argb(255, 0, 0, 255))
            binding.imageTableStats.setColorFilter(Color.argb(255, 220, 220, 220))
            type = "stats"
        }

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



        villeText.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            if (!checkForChips(arrayVilleNew, parent.getItemAtPosition(position).toString())) {
                addChipVille(parent.getItemAtPosition(position).toString())
                arrayVilleNew.add(parent.getItemAtPosition(position).toString())
            }
            villeText.setText("")
        }

        magasinText.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            if (!checkForChips(arrayStoreNew, parent.getItemAtPosition(position).toString())) {
                addChipMagasin(parent.getItemAtPosition(position).toString())
                arrayStoreNew.add(parent.getItemAtPosition(position).toString())
            }
            magasinText.setText("")
        }

        questionnaireText.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            if (!checkForChips(arrayQuestNew, parent.getItemAtPosition(position).toString())) {
                addChipQuestionnaire(parent.getItemAtPosition(position).toString())
                arrayQuestNew.add(parent.getItemAtPosition(position).toString())
            }
            questionnaireText.setText("")
        }

        valider_kpi.setOnClickListener {
            Log.i("DateDebut","${controleDate()}")
        }

    }


    private fun checkForChips(ourArray: ArrayList<String>, name: String): Boolean {

        for (i in ourArray) {
            if (i == name)
                return true
        }

        return false
    }

    private fun removeChip(ourArray: ArrayList<String>, name: String) {
        for (i in ourArray) {
            if (i == name) {
                ourArray.remove(i)
                return
            }
        }
    }

    private fun addChipVille(name: String) {
        binding.chipGroupVille.addView(createTagChip(requireContext(), name, "ville"))

    }

    private fun addChipMagasin(name: String) {
        binding.chipGroupMagasin.addView(createTagChip(requireContext(), name, "magasin"))
    }

    private fun addChipQuestionnaire(name: String) {
        binding.chipGroupQuestionnaire.addView(
            createTagChip(
                requireContext(),
                name,
                "questionnaire"
            )
        )
    }

    private fun controleDate() :Boolean
    {
        val day_debut_picker = "${date_debut_picker.year}-${(date_debut_picker.month + 1)}-${date_debut_picker.dayOfMonth}"
        val day_fin_picker = "${date_fin_picker.year}-${(date_fin_picker.month + 1)}-${date_fin_picker.dayOfMonth}"

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val debutParsed = sdf.parse(day_debut_picker)
        val finParsed = sdf.parse(day_fin_picker)

        val test = debutParsed.compareTo(finParsed)

        return  (test == 0 || test == -1)

    }


    private fun createTagChip(context: Context, chipName: String, tagNow: String): Chip {
        return Chip(context).apply {
            val myView = this
            text = chipName
            tag = tagNow
            setChipBackgroundColorResource(R.color.purpleLogin)
            setCloseIconTintResource(R.color.white)
            isCloseIconVisible = true
            setTextColor(ContextCompat.getColor(context, R.color.white))
            //setTextAppearance(R.style.ChipTextAppearance)
            setOnCloseIconClickListener {


                val anim = AlphaAnimation(1f, 0f)
                anim.duration = 250
                anim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        when (tagNow) {
                            "ville" -> {
                                removeChip(arrayVilleNew, chipName)
                                binding.chipGroupVille.removeView(myView)

                            }
                            "magasin" -> {
                                removeChip(arrayStoreNew, chipName)
                                binding.chipGroupMagasin.removeView(myView)
                            }
                            "questionnaire" -> {
                                removeChip(arrayQuestNew, chipName)
                                binding.chipGroupQuestionnaire.removeView(myView)
                            }
                        }

                    }

                    override fun onAnimationStart(animation: Animation?) {}
                })

                it.startAnimation(anim)
            }
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
            mapStore[i.name + "\n" + i.governorate + "\n" + i.address] = i.id
            arrayStore.add(i.name + "\n" + i.governorate + "\n" + i.address)
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