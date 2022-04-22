package com.example.gtm.ui.home.kpi.kpifilter

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.gtm.data.entities.custom.KpiStats
import com.example.gtm.data.entities.custom.chart.ChartResponse
import com.example.gtm.data.entities.custom.chart.DataChart
import com.example.gtm.data.entities.response.*
import com.example.gtm.ui.home.kpi.KpiFinalResultActivity
import com.example.gtm.ui.home.kpi.piechart.PieChartLastActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_kpi_f_ilter.swiperefreshlayout
import kotlinx.android.synthetic.main.fragment_kpi_f_ilter.topAppBar
import kotlinx.android.synthetic.main.fragment_task.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.roundToInt


@AndroidEntryPoint
class KpiFIlterFragment : Fragment() {

    private lateinit var binding: FragmentKpiFIlterBinding
    private lateinit var responseDataStores: Resource<GetStore>
    private val viewModel: AddVisiteDialogViewModel by viewModels()
    private val viewModelKpi: KpiFilterFragmentViewModel by viewModels()

    //List of StoreXX
    private var listaDataXX = ArrayList<DataXX>()
    private var listaKpi = ArrayList<DataXXX>()
    private lateinit var responseDataQuestionnaire: Resource<Quiz>
    private lateinit var responseKpi: Resource<AnalyseKpi>
    private lateinit var responseChart: Resource<ChartResponse>
    private var listaQuiz = ArrayList<QuizData>()
    private var listaChar = ArrayList<DataChart>()
    private val viewModelQuestionnaire: MyQuizViewModel by viewModels()
    private val mapStore: HashMap<String, Int> = HashMap<String, Int>()
    private var arrayStore = ArrayList<String>()
    private val mapQuest: HashMap<String, Int> = HashMap<String, Int>()
    private var arrayQuest = ArrayList<String>()
    private var arrayVilleNew = ArrayList<String>()
    private var arrayStoreNew = ArrayList<String>()
    private var arrayQuestNew = ArrayList<String>()
    private var userId = 0
    lateinit var sharedPref: SharedPreferences
    private var arraySupervisorsId = ArrayList<Int>()
    private var arrayStoresId = ArrayList<Int>()
    private var arrayQuestIds = ArrayList<Int>()
    var day_debut_picker = ""
    var day_fin_picker = ""
    private var fm: FragmentManager? = null
    private val progressUploadDialog = ProgressUploadDialog()
    var valueMagasinMap: HashMap<String, Double> = HashMap<String, Double>()
    var valueVilleMap: HashMap<String, Double> = HashMap<String, Double>()
    var magasinNameSet: HashSet<String> = HashSet<String>()
    var villeNameSet: HashSet<String> = HashSet<String>()
    private var etatFragment = 0


    //List of villes
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

        //Init childFragmentManager
        fm = childFragmentManager


        //Clear Editexts From Data
        clearData()

        // Get User ID From SharedPref
        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        userId = sharedPref.getInt("id", 0)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Init dates for date_début and date_fin
        setTodayDate()

        //Get Instance of Drawer Layout
        val mDrawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)

        //Top Bar with left animation
        topAppBar.setNavigationOnClickListener {
            mDrawerLayout.openDrawer(Gravity.LEFT)
        }

        //Set Fragment Title
        binding.topAppBar.title = "Analyse Kpi"


        //Prepare Adapter of Ville
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
        binding.villeText.setAdapter(arrayAdapter)

        //Get  Get Stores + Questionnaires
        getStoresAndQuestionnaires()


        //Ville Editext Listener
        villeText.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            //if ville doesnt exist in the groupList then add it
            if (!checkForChips(arrayVilleNew, parent.getItemAtPosition(position).toString())) {
                addChipVille(parent.getItemAtPosition(position).toString())
                arrayVilleNew.add(parent.getItemAtPosition(position).toString())
            }
        }

        //Magasin Editext Listener
        magasinText.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            //if magasin doesnt exist in the groupList then add it
            if (!checkForChips(arrayStoreNew, parent.getItemAtPosition(position).toString())) {
                addChipMagasin(parent.getItemAtPosition(position).toString())
                arrayStoreNew.add(parent.getItemAtPosition(position).toString())
            }
        }

        //Questionnaire Editext Listener
        questionnaireText.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            //if questionnaire doesn't exist in the groupList then add it
            if (!checkForChips(arrayQuestNew, parent.getItemAtPosition(position).toString())) {
                addChipQuestionnaire(parent.getItemAtPosition(position).toString())
                arrayQuestNew.add(parent.getItemAtPosition(position).toString())
            }
        }


        //Valider Button Listener
        valider_kpi.setOnClickListener {
            //if EndDate >= StartDate
            if (!controleDate()) {
                binding.questionnaireErrorText.visibility = View.GONE
                error_text.visibility = View.VISIBLE
                date_debut_text.requestFocus()
                //There must be at least one questionnaire if you want to get piechart
            } else  if (etatFragment == 1 && (arrayQuestNew.size != 1)) {
                error_text.visibility = View.GONE
                binding.questionnaireErrorText.visibility = View.VISIBLE
                binding.questionnaireErrorText.requestFocus()
                //Prepare for Analyse superviseur
            } else {
                error_text.visibility = View.GONE
                binding.questionnaireErrorText.visibility = View.GONE
                prepareRequestList()
            }
        }


        //Choose Analyses Superviseur Option ( etat fragment  = 0)
        binding.imageTableStatsCard.setOnClickListener {

            if (etatFragment == 1) {
                etatFragment = 0
                binding.imageTableStats.setColorFilter(Color.argb(255, 0, 0, 0))
                binding.imageKpiStats.setColorFilter(Color.argb(255, 220, 220, 220))
                binding.pieChartText.setHintTextColor(resources.getColor(R.color.clear_grey))
                binding.tableText.setTextColor(resources.getColor(R.color.purpleLogin))
            }
        }


        //Choose PieChart Option ( etat fragment = 1)
        binding.imageKpiStatsCard.setOnClickListener {

            if (etatFragment == 0) {
                etatFragment = 1
                binding.imageKpiStats.setColorFilter(Color.argb(255, 0, 0, 0))
                binding.imageTableStats.setColorFilter(Color.argb(255, 220, 220, 220))
                binding.pieChartText.setHintTextColor(resources.getColor(R.color.purpleLogin))
                binding.tableText.setTextColor(resources.getColor(R.color.clear_grey))
            }
        }


        //SwipeDown Refresh
        binding.swiperefreshlayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {

          //  getStoresAndQuestionnaires()
            swiperefreshlayout.isRefreshing = false
        })


        //Date debut setOnClickListener
        binding.dateDebutCardTextCalendar.setOnClickListener {

            //Show calendarView then setText in datedebuttext
            clickDataPicker(requireView(),0)
        }

        binding.dateDebutCardText.setOnClickListener {

            //Show calendarView then setText in datedebuttext
            clickDataPicker(requireView(),0)
        }


        //Date fin setOnClickListener
        binding.dateFinCardTextCalendar.setOnClickListener {

            //Show calendarView then setText in datefintext
            clickDataPicker(requireView(),1)
        }

        binding.dateFinCardText.setOnClickListener {

            //Show calendarView then setText in datefintext
            clickDataPicker(requireView(),1)
        }


    }


    //Check if the chip exists in the array
    private fun checkForChips(ourArray: ArrayList<String>, name: String): Boolean {

        for (i in ourArray) {
            if (i == name)
                return true
        }

        return false
    }

    //Remove chip from array
    private fun removeChip(ourArray: ArrayList<String>, name: String) {
        for (i in ourArray) {
            if (i == name) {
                ourArray.remove(i)
                return
            }
        }
    }

    //Add ville chip to grouplayout
    private fun addChipVille(name: String) {

        binding.chipGroupVille.addView(createTagChip(requireContext(), name, "ville"))

    }

    //Add magasin chip to grouplayout
    private fun addChipMagasin(name: String) {
        binding.chipGroupMagasin.addView(createTagChip(requireContext(), name, "magasin"))
    }

    //Add questionnaire chip to grouplayout
    private fun addChipQuestionnaire(name: String) {
        binding.chipGroupQuestionnaire.addView(
            createTagChip(
                requireContext(),
                name,
                "questionnaire"
            )
        )
    }

    //End Date must be >= to Start Date
    private fun controleDate(): Boolean {
       /* day_debut_picker =
            "${date_debut_picker.year}-${(date_debut_picker.month + 1)}-${date_debut_picker.dayOfMonth}"
        day_fin_picker =
            "${date_fin_picker.year}-${(date_fin_picker.month + 1)}-${date_fin_picker.dayOfMonth}" */

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val debutParsed = sdf.parse(day_debut_picker)
        val finParsed = sdf.parse(day_fin_picker)

        val test = debutParsed.compareTo(finParsed)

        return (test == 0 || test == -1)

    }


    //Create chip with animation
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


                //Animate chips
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


    //Get Questionnaires + Get Stores
    private fun getStoresAndQuestionnaires() {

        //If fragment manager is present
        if (!fm!!.isDestroyed)
            progressUploadDialog.show(fm!!, "ProgressUploadDialog")

        //Start couroutine to get stores and questionnaires
        lifecycleScope.launch(Dispatchers.Main) {

            //GetStores Response
            responseDataStores = viewModel.getStores()

            //If response is good
            if (responseDataStores.responseCode == 200) {
                //If progress indicator is present then make it gone
                if (progress_indicator != null)
                    progress_indicator.visibility = View.GONE

                //Get list of stores as listaDataXX
                listaDataXX = responseDataStores.data!!.data as ArrayList<DataXX>

                //Convert List of Store to Map storeName -> storeId
                getSubListStore()

                //Get List of Questionnaire Service
                getQuestionnaires()

                //if fragment manager is present then dismiss progressUploadDialog
                if (fm != null && !fm!!.isDestroyed)
                    progressUploadDialog.dismiss()

                // Repeat getStoresAndQuestionnaires() until we get good response
            } else {

                progressUploadDialog.dismiss()
                getStoresAndQuestionnaires()

            }

        }
    }

    //Get List of Questionnaire Service
    private fun getQuestionnaires() {
        lifecycleScope.launch(Dispatchers.Main) {

            //If Fragment is Added
            if (isAdded) {

                //Get Response Questionnaire
                responseDataQuestionnaire = viewModelQuestionnaire.getSurvey()

                //If Response Good
                if (responseDataQuestionnaire.responseCode == 200) {
                    //Extract List of Questionnaires from response
                    listaQuiz = responseDataQuestionnaire.data!!.data as ArrayList<QuizData>

                    //Convert List of Questionnaire to Map quizName -> quizId
                    getSubListQuestionnaire()
                    progressUploadDialog.dismiss()

                    //Repeat getQuestionnaires until we get a good response
                } else {
                    getQuestionnaires()
                }
            }

        }
    }

    //Convert List of Store to Map storeName -> storeId
    private fun getSubListStore() {
        for (i in listaDataXX) {
            mapStore[i.name + "\n" + i.governorate + "\n" + i.address] = i.id
            arrayStore.add(i.name + "\n" + i.governorate + "\n" + i.address)
        }
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, arrayStore)
        binding.magasinText.setAdapter(arrayAdapter)

    }

    //Convert List of Questionnaire to Map quizName -> quizId
    private fun getSubListQuestionnaire() {
        for (i in listaQuiz) {
            mapQuest[i.name] = i.id
            arrayQuest.add(i.name)
        }
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, arrayQuest)
        binding.questionnaireText.setAdapter(arrayAdapter)
    }


    private fun prepareRequestList() {


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

        //We chose between Analyse Superviseur and Pie chart Activity
        if (etatFragment == 0) {
            getAnalyseResponse()
        } else {

            getAnalyseChart()


        }


    }


    //Get Pie chart service
    private fun getAnalyseChart() {

        //show Progress Dialog
        progressUploadDialog.show(fm!!, "ProgressUploadDialog")

        //Launch Couroutine
        lifecycleScope.launch(Dispatchers.Main) {


            val newArrayVilleNew = ArrayList<String>()

            //Add special characters to arraylist of strings "%"
            for (i in arrayVilleNew) {
                newArrayVilleNew.add("\"$i\"")
            }

            //Get Response Chart
            responseChart = viewModelKpi.getStatChart(
                arrayStoresId,
                arrayQuestIds[0],
                day_debut_picker,
                day_fin_picker,
                arraySupervisorsId,
                newArrayVilleNew
            ) as Resource<ChartResponse>


            //If All is Good
            if (responseChart.responseCode == 200) {

                //Dismiss Dialog
                progressUploadDialog.dismiss()

                //Get List of CHar from response
                listaChar = responseChart.data?.data as ArrayList<DataChart>

                // Get Unique Name of every Store
                extractStore()

                // Get Unique Name of every Ville
                extractVille()


                //start Intent to PieCHarrtActivity
                val intent = Intent(requireActivity(), PieChartLastActivity::class.java)
                intent.putExtra("villeMap", valueVilleMap)
                intent.putExtra("magasinMap", valueMagasinMap)
                startActivity(intent)

                //Override Animations
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


    // Get Unique Name of every Store
    private fun extractStore() {
        for (i in listaChar) {
            magasinNameSet.add(i.store.name)
        }

        // Create HAShmap of magasin -> average
        extractAverageForStore()
    }


    // Create Hashmap of magasin -> average
    private fun extractAverageForStore() {
        var increment = 0
        var average = 0.0
        var totalAverage = 0.0

        for (i in magasinNameSet) {

            for (j in listaChar) {
                if (i == j.store.name) {
                    increment++
                    average += j.average
                }


            }
            totalAverage = average / increment

            valueMagasinMap[i] = totalAverage

        }

    }


    // Get Unique Name of every Ville
    private fun extractVille() {
        for (i in listaChar) {
            villeNameSet.add(i.store.governorate)
        }

        // Create HAShmap of ville -> average
        extractAverageForVille()

    }


    // Create HAShmap of ville -> average
    private fun extractAverageForVille() {

        var increment = 0
        var average = 0.0
        var totalAverage = 0.0

        for (i in villeNameSet) {

            for (j in listaChar) {
                if (i == j.store.governorate) {
                    increment++
                    average += j.average
                }


            }
            totalAverage = average / increment

            valueVilleMap[i] = totalAverage


        }


    }


    //Get Analyse Superviseur response froms ervice
    private fun getAnalyseResponse() {
        //show progress dialog
        progressUploadDialog.show(fm!!, "ProgressUploadDialog")

        //launch Couroutine
        lifecycleScope.launch(Dispatchers.Main) {

            val newArrayVilleNew = ArrayList<String>()
            //Add special characters to arraylist of strings "%"
            for (i in arrayVilleNew) {
                newArrayVilleNew.add("\"$i\"")
            }

            //Get response from service
            responseKpi = viewModelKpi.getStatTable(
                day_debut_picker,
                day_fin_picker,
                arrayStoresId,
                arrayQuestIds,
                arraySupervisorsId,
                newArrayVilleNew
            )

            //If >Everything is good
            if (responseKpi.responseCode == 200) {

                //Remove progress dialog and clear all errors
                progressUploadDialog.dismiss()
                binding.errorTextKpi.visibility = View.GONE


                //GetLista Kpi From response as DataXXX
                listaKpi = responseKpi.data!!.data as ArrayList<DataXXX>


                //Prepare KPI Object for Analyse superviseur
                val myObject: KpiStats = prepareKpiObject()

                //COnvert myObject to string Json
                val myObjectJson: String = Gson().toJson(myObject)


                //Intent to KpiResultActivity
                val intent = Intent(requireActivity(), KpiFinalResultActivity::class.java)
                intent.putExtra("kpiObject", myObjectJson)
                startActivity(intent)

                //Override Animation
                requireActivity().overridePendingTransition(
                    R.anim.right_to_left_activity,
                    R.anim.left_to_right_activity
                )

                //Clear errors and dismiss progress dialog
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


    //Prepare KPI Object for Analyse superviseur
    private fun prepareKpiObject(): KpiStats {
        val moyenneRetard = calculerMoyenneRetard()
        val moyenneDernierPointage = calculerMoyenneDernierPointage()
        val visitesPlanifie = calculerVisitesPlanifies()
        val visitesNonPlanifie = calculerVisitesNonPlanifies()
        val visiteRealise = calculerVisitesRealises()
        val questionnaireRealise = calculerQuestionnairesRealise()
        val moyenneQuestionnaire = calculerMoyenneQuestionnaire()
        val nombrePhotos = calculerNombrePhotos()
        val performance = (calculerPerformance(visiteRealise, visitesPlanifie) * 100).toInt()

        return KpiStats(
            moyenneRetard,
            moyenneDernierPointage,
            visitesPlanifie,
            visitesNonPlanifie,
            visiteRealise,
            questionnaireRealise,
            moyenneQuestionnaire,
            nombrePhotos,
            performance
        )

    }


    //Calculer performance supervisuer
    private fun calculerPerformance(done: Int, total: Int): Double {
        if (done == 0)
            return 0.0
        if (total == 0)
            return 1.0


        return ((done.toDouble() / total) * 100.0).roundToInt() / 100.0
    }


    //Calculer moyenne retard superviseur
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
                incrementCalcul++
            }
        }

        if (totalSeconds != 0 && incrementCalcul != 0)
            return ((totalSeconds / incrementCalcul) / 60).toLong()
        return 0
    }


    //Calculer moeyenne dernier poinatage superviseur
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


    //Calculer visites planifies pour superviseur
    private fun calculerVisitesPlanifies(): Int {
        var cvp = 0

        for (i in listaKpi) {
            if (i.planned)
                cvp++
        }

        return cvp
    }


    //Calculer visite non planifies pour superviseur
    private fun calculerVisitesNonPlanifies(): Int {
        var cvnp = 0

        for (i in listaKpi) {
            if (!i.planned)
                cvnp++
        }

        return cvnp
    }


    //Calculer visites realises superviseur
    private fun calculerVisitesRealises(): Int {
        var cvr = 0
        for (i in listaKpi) {
            if (i.surveyResponses.isNotEmpty()) {
                cvr++
            }
        }

        return cvr
    }

    //Calculer Questionnaire realise superviseur
    private fun calculerQuestionnairesRealise(): Int {
        val arrayIdQuest = ArrayList<Int>()
        for (i in listaKpi) {

            for (j in i.surveyResponses) {
                arrayIdQuest.add(j.surveyId)
            }
        }
        return arrayIdQuest.size
    }

    //Calculer moyenne questionnaires pour superviseur
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


    //Calculer nombre total de photos pour superviseur
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


    //Convert date to seconds
    private fun fromDateToSeconds(hours: Int, minutes: Int, seconds: Int): Int {
        return seconds + (minutes * 60) + (hours * 3600)
    }


    //Convert seconds to date
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


    //Clear all EditeTexts
    private fun clearData() {
        valueMagasinMap.clear()
        valueVilleMap.clear()
        villeNameSet.clear()
        magasinNameSet.clear()
    }


    //set today date for date_debut and date_fin
    private fun setTodayDate() {
        val cal = Calendar.getInstance()
        val my_year = cal.get(Calendar.YEAR)
        val my_month = cal.get(Calendar.MONTH)
        val my_day = cal.get(Calendar.DAY_OF_MONTH)

        binding.debutText.text = "$my_year-${my_month+1}-$my_day"
        binding.finText.text = "$my_year-${my_month+1}-$my_day"

        day_debut_picker = "$my_year-${my_month+1}-$my_day"
        day_fin_picker = "$my_year-${my_month+1}-$my_day"


    }


    //Prepare CalendarView
    fun clickDataPicker(view: View,typeDate: Int) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in Editext

                //date Début
                if(typeDate == 0)
                {
                    binding.debutText.text = "$year-${monthOfYear+1}-$dayOfMonth"
                    day_debut_picker = "$year-${monthOfYear+1}-$dayOfMonth"
                }
                //date Fin
                else
                {
                    binding.finText.text = "$year-${monthOfYear+1}-$dayOfMonth"
                    day_fin_picker = "$year-${monthOfYear+1}-$dayOfMonth"
                }
            },
            year,
            month,
            day
        )
        dpd.show()
    }

}