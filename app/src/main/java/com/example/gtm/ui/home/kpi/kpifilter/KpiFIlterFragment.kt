package com.example.gtm.ui.home.kpi.kpifilter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gtm.R
import com.example.gtm.databinding.FragmentKpiFIlterBinding
import com.example.gtm.ui.home.mytask.addvisite.AddVisiteDialogViewModel
import com.example.gtm.ui.home.mytask.survey.quiz.MyQuizViewModel
import com.example.gtm.utils.resources.Resource
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_add_visite.progress_indicator
import kotlinx.android.synthetic.main.fragment_kpi_f_ilter.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import android.widget.AdapterView.OnItemClickListener
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.example.gtm.data.entities.custom.KpiStats
import com.example.gtm.data.entities.response.*
import com.example.gtm.ui.home.kpi.KpiFinalResultActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_kpi_f_ilter.topAppBar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt


@AndroidEntryPoint
class KpiFIlterFragment : Fragment() {

    private lateinit var binding: FragmentKpiFIlterBinding
    private lateinit var responseDataStores: Resource<GetStore>
    private val viewModel: AddVisiteDialogViewModel by viewModels()
    private val viewModelKpi: KpiFilterFragmentViewModel by viewModels()
    private var listaDataXX = ArrayList<DataXX>()
    private var listaKpi = ArrayList<DataXXX>()
    private lateinit var responseDataQuiz: Resource<Quiz>
    private lateinit var responseKpi: Resource<AnalyseKpi>
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
    private var userId = 0
    lateinit var sharedPref: SharedPreferences
    private var arraySupervisorsId = ArrayList<Int>()
    private var arrayStoresId = ArrayList<Int>()
    private var arrayQuestIds = ArrayList<Int>()
    var day_debut_picker = ""
    var day_fin_picker = ""
    private var fm: FragmentManager? = null
    private val progressUploadDialog = ProgressUploadDialog()
    // heure_debut = "8:00"


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
        "Tunis",
        "Zaghouan"
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKpiFIlterBinding.inflate(inflater, container, false)

        fm = childFragmentManager

        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        userId = sharedPref.getInt("id", 0)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        progressUploadDialog.show(fm!!, "ProgressUploadDialog")


        val mDrawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        //Top Bar
        topAppBar.setNavigationOnClickListener {
            mDrawerLayout.openDrawer(Gravity.LEFT)
        }

        binding.topAppBar.title = "Analyse Kpi"


        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
        binding.villeText.setAdapter(arrayAdapter)


        getStores()



        villeText.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            if (!checkForChips(arrayVilleNew, parent.getItemAtPosition(position).toString())) {
                addChipVille(parent.getItemAtPosition(position).toString())
                arrayVilleNew.add(parent.getItemAtPosition(position).toString())
            }
        }

        magasinText.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            if (!checkForChips(arrayStoreNew, parent.getItemAtPosition(position).toString())) {
                addChipMagasin(parent.getItemAtPosition(position).toString())
                arrayStoreNew.add(parent.getItemAtPosition(position).toString())
            }
        }

        questionnaireText.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            if (!checkForChips(arrayQuestNew, parent.getItemAtPosition(position).toString())) {
                addChipQuestionnaire(parent.getItemAtPosition(position).toString())
                arrayQuestNew.add(parent.getItemAtPosition(position).toString())
            }
        }

        valider_kpi.setOnClickListener {
            if (!controleDate()) {
                error_text.visibility = View.VISIBLE
                date_debut_text.requestFocus()
            } else {
                prepareRequestList()
            }
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

    private fun controleDate(): Boolean {
        day_debut_picker =
            "${date_debut_picker.year}-${(date_debut_picker.month + 1)}-${date_debut_picker.dayOfMonth}"
        day_fin_picker =
            "${date_fin_picker.year}-${(date_fin_picker.month + 1)}-${date_fin_picker.dayOfMonth}"

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val debutParsed = sdf.parse(day_debut_picker)
        val finParsed = sdf.parse(day_fin_picker)

        val test = debutParsed.compareTo(finParsed)

        return (test == 0 || test == -1)

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
                progressUploadDialog.dismiss()


            } else {


                getStores()

            }

        }
    }

    private fun getQuestionnaires() {
        lifecycleScope.launch(Dispatchers.Main) {

            responseDataQuiz = viewModelQuestionnaire.getSurvey()

            if (responseDataQuiz.responseCode == 200) {
                listaQuiz = responseDataQuiz.data!!.data as ArrayList<QuizData>
                getSubListQuestionnaire()
                progressUploadDialog.dismiss()
            } else {
                getQuestionnaires()
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


    private fun prepareRequestList() {

        progressUploadDialog.show(fm!!, "ProgressUploadDialog")

        //Prepare Arraylist Supervisor ID
        if (arraySupervisorsId.size > 1) {
            arraySupervisorsId.clear()
            arraySupervisorsId.add(userId)
        } else
            arraySupervisorsId.add(userId)

        //Prepare Arraylist Villes
        if (arrayVilleNew.size == 0) {

            for (i in items)
                arrayVilleNew.add(i)
        }


        //Prepare ArrayList Magasins
        if (arrayStoresId.size > 1)
            arrayStoresId.clear()


        if (arrayStoreNew.size == 0) {
            for (i in listaDataXX.indices) {
                arrayStoresId.add(listaDataXX[i].id)
            }
        } else {
            for (i in arrayStoreNew) {
                for ((key, value) in mapStore.entries) {
                    if (i == key) {
                        arrayStoresId.add(value)
                    }
                }
            }
        }


        //Prepare ArrayList Questionnaire
        if (arrayQuestIds.size > 1)
            arrayQuestIds.clear()

        if (arrayQuestNew.size == 0) {
            for (i in listaQuiz) {
                arrayQuestIds.add(i.id)
            }
        } else {
            for (i in arrayQuestNew) {
                for ((key, value) in mapQuest.entries) {
                    if (i == key) {
                        arrayQuestIds.add(value)
                    }
                }
            }
        }


        getAnalyseResponse()

    }


    private fun getAnalyseResponse() {
        lifecycleScope.launch(Dispatchers.Main) {

            val newArrayVilleNew = ArrayList<String>()
            //Add special characters to arraylist of strings "%"
            for (i in arrayVilleNew) {
                newArrayVilleNew.add("\"$i\"")
            }

            responseKpi = viewModelKpi.getStatTable(
                day_debut_picker,
                day_fin_picker,
                arrayStoresId,
                arrayQuestIds,
                arraySupervisorsId,
                newArrayVilleNew
            )

            if (responseKpi.responseCode == 200) {

                progressUploadDialog.dismiss()
                binding.errorTextKpi.visibility = View.GONE

                listaKpi = responseKpi.data!!.data as ArrayList<DataXXX>

                val myObject : KpiStats = prepareKpiObject()

                val myObjectJson: String = Gson().toJson(myObject)


                val intent = Intent(requireActivity(), KpiFinalResultActivity::class.java)
                intent.putExtra("kpiObject", myObjectJson)
                startActivity(intent)
                requireActivity().overridePendingTransition(
                    R.anim.right_to_left_activity,
                    R.anim.left_to_right_activity
                )

            } else {

                arraySupervisorsId.clear()
                arrayStoresId.clear()
                arrayQuestIds.clear()
                arrayVilleNew.clear()
                progressUploadDialog.dismiss()
                binding.errorTextKpi.visibility = View.VISIBLE
            }


        }
    }



    private fun prepareKpiObject(): KpiStats
    {
        val moyenneRetard = calculerMoyenneRetard()
        val moyenneDernierPointage = calculerMoyenneDernierPointage()
        val visitesPlanifie = calculerVisitesPlanifies()
        val visitesNonPlanifie = calculerVisitesNonPlanifies()
        val visiteRealise = calculerVisitesRealises()
        val questionnaireRealise = calculerQuestionnairesRealise()
        val moyenneQuestionnaire = calculerMoyenneQuestionnaire()
        val nombrePhotos = calculerNombrePhotos()
        val performance = (calculerPerformance(visiteRealise,visitesPlanifie) * 100).toInt()

        return KpiStats(moyenneRetard,moyenneDernierPointage,visitesPlanifie,visitesNonPlanifie,visiteRealise,questionnaireRealise, moyenneQuestionnaire ,nombrePhotos,performance)

    }


    private fun calculerPerformance(done: Int, total: Int): Double {
        if (done == 0)
            return 0.0
        if (total == 0)
            return 1.0


        return ((done.toDouble() / total) * 100.0).roundToInt() / 100.0
    }


    private fun calculerMoyenneRetard(): Long {
        var totalSeconds = 0
        var incrementCalcul = 0
        for (i in listaKpi) {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val date: Date = format.parse(i.start)

            val hours = date.hours + 1
            val minutes = date.minutes
            val seconds = date.seconds

            if (hours >= 8) {
                totalSeconds += ((hours - 8) * 3600) + (minutes * 60) + seconds
                Log.i("totalSeconds", "$totalSeconds")
                incrementCalcul++
            }
        }

        if (totalSeconds != 0 && incrementCalcul != 0)
            return ((totalSeconds / incrementCalcul) / 60).toLong()
        return 0
    }


    private fun calculerMoyenneDernierPointage(): String {
        var totalSeconds = 0
        var numberTime = 0


        for (i in listaKpi) {
            if (i.end != null) {
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val date: Date = format.parse(i.end)
                totalSeconds += fromDateToSeconds(date.hours + 1, date.minutes, date.seconds)
                numberTime++
            }
        }

        if (totalSeconds != 0 && numberTime != 0) {
            val finalTime: Long = (totalSeconds / numberTime).toLong()
            return fromSecondsToDate(finalTime)
        }
        return "0"
    }


    private fun calculerVisitesPlanifies(): Int {
        var cvp = 0

        for (i in listaKpi) {
            if (i.planned)
                cvp++
        }

        return cvp
    }


    private fun calculerVisitesNonPlanifies(): Int {
        var cvnp = 0

        for (i in listaKpi) {
            if (!i.planned)
                cvnp++
        }

        return cvnp
    }


    private fun calculerVisitesRealises(): Int {
        var cvr = 0
        for (i in listaKpi) {
            if (i.surveyResponses.isNotEmpty()) {
                cvr++
            }
        }

        return cvr
    }

    private fun calculerQuestionnairesRealise(): Int {
        val arrayIdQuest = ArrayList<Int>()
        for (i in listaKpi) {

            for (j in i.surveyResponses) {
                arrayIdQuest.add(j.surveyId)
            }
        }
        return arrayIdQuest.size
    }

    private fun calculerMoyenneQuestionnaire(): Double {
        var avr = 0.0
        var iterateAvr = 0
        for (i in listaKpi) {
            for (j in i.surveyResponses) {
                avr += j.average
                iterateAvr++
            }
        }

        val finalTest = (avr / iterateAvr)

        return if (avr != 0.0 && iterateAvr != 0)
            (finalTest * 100.0).roundToInt() / 100.0
        else
            0.0
    }


    private fun calculerNombrePhotos(): Int {
        var cnp = 0

        for (i in listaKpi) {
            for (j in i.surveyResponses) {
                for (k in j.responses) {
                    cnp += k.responsePictures.size
                }
            }
        }

        return cnp
    }


    private fun fromDateToSeconds(hours: Int, minutes: Int, seconds: Int): Int {
        return seconds + (minutes * 60) + (hours * 3600)
    }

    private fun fromSecondsToDate(scds: Long): String {
        val hours = scds / 3600
        var rest = scds % 3600
        val minutes = rest / 60
        rest %= 60

        var finalHours = ""
        var finalMinutes = ""

        if (hours < 10)
            finalHours = "$hours"
        else
            finalHours = "$hours"

        if (minutes < 10)
            finalMinutes = "0$minutes"
        else
            finalMinutes = "$minutes"

        return "$finalHours:$finalMinutes"

    }
}