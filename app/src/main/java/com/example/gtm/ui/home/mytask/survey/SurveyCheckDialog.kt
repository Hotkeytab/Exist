package com.example.gtm.ui.home.mytask.survey


import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.example.gtm.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_quiz_confirmation.*
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.gtm.data.entities.remote.VisitPost
import com.example.gtm.data.entities.response.SuccessResponse
import com.example.gtm.data.entities.response.TimeClass
import com.example.gtm.data.entities.response.Visite
import com.example.gtm.ui.home.mytask.LocationValueListener
import com.example.gtm.ui.home.mytask.MyTaskViewModel
import com.example.gtm.ui.home.mytask.TaskAdapter
import com.example.gtm.ui.home.mytask.TaskFragment
import com.example.gtm.ui.home.mytask.survey.quiz.MyQuizViewModel
import com.example.gtm.utils.resources.Resource
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_choix_visitee.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class SurveyCheckDialog(
    listener2: TaskFragment,
    navController: NavController,
    etat1: Int,
    viewAct1: View,
    adapterTask1: TaskAdapter,
    listatasks1: ArrayList<Visite>,
    visite1: Visite,

    ) :
    DialogFragment() {

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 3
    private var GpsStatus = false
    private lateinit var locationIn: Location
    var veriftest = false
    val navControllerIn = navController
    var testGps = false
    val etat = etat1
    val viewAct = viewAct1
    val adapterTask = adapterTask1
    val listaTasks = listatasks1
    var visite = visite1
    var listener = listener2
    private lateinit var responseTime: Resource<TimeClass>
    private val viewModel: MyTaskViewModel by viewModels()
    private val viewModelQuiz: MyQuizViewModel by viewModels()
    private lateinit var responseAdd: Resource<SuccessResponse>

    // declare a global variable of FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var userId = 0
    lateinit var sharedPref: SharedPreferences


    interface CloseCheckDialogListener {
        fun onClosedCheckDialog()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_quiz_confirmation, container, false)
    }

    override fun onStart() {
        super.onStart()

        val width = (resources.displayMetrics.widthPixels * 0.99).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.88).toInt()
        dialog!!.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog!!.window!!.setWindowAnimations(R.style.AnimationsForMapDialog)
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        userId = sharedPref.getInt("id", 0)

        // getLocation()

        if (etat == 1) {
            title1.text = "Pointage"
            textcontext1.text = "Souhaitez vous confirmer le pointage de Début ?"
        } else if (etat == 2) {
            title1.text = "Pointage"
            textcontext1.text = "Souhaitez vous confirmer le pointage de Fin ?"
        } else if (etat == 3) {
            title1.text = "Questionnaire"
            textcontext1.text = "Souhaitez vous répondre au questionnaire ? "
        }

        accept.setOnClickListener {

            progress_indicator.visibility = View.VISIBLE
            cancel_button.isEnabled =false
            accept.isEnabled = false
            dialog!!.setCancelable(false)

            if (etat == 1) {


                GlobalScope.launch(Dispatchers.Main) {
                    val visitePost = VisitPost(
                        visite.id,
                        getDateNow(),
                        0,
                        visite.storeId,
                        userId,
                        false,
                        "entrée"
                    )
                    val arayListVsitePost = ArrayList<VisitPost>()
                    arayListVsitePost.add(visitePost)
                    responseAdd = viewModelQuiz.addVisite(arayListVsitePost)

                    if (responseAdd.responseCode == 201) {
                        cancel_button.isEnabled =true
                        accept.isEnabled = true
                        dialog!!.setCancelable(true)

                        progress_indicator.visibility = View.GONE

                        val snack = Snackbar.make(
                            viewAct,
                            "Pointage de Début envoyé avec succès",
                            Snackbar.LENGTH_LONG
                        ).setBackgroundTint(resources.getColor(R.color.purpleLogin))
                        val view: View = snack.view
                        val params = view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.CENTER
                        dismiss()
                        view.layoutParams = params
                        snack.show()
                        var targetNew = listener as CloseCheckDialogListener
                        targetNew.onClosedCheckDialog()
                    } else {
                        cancel_button.isEnabled =true
                        accept.isEnabled = true
                        dialog!!.setCancelable(true)
                        progress_indicator.visibility = View.GONE
                        val snack = Snackbar.make(
                            viewAct,
                            "Erreur envoie pointage",
                            Snackbar.LENGTH_LONG
                        ).setBackgroundTint(resources.getColor(R.color.red))
                        val view: View = snack.view
                        val params = view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.CENTER
                        dismiss()
                        adapterTask.setItems(listaTasks)
                        view.layoutParams = params
                        snack.show()
                    }


                }

            } else if (etat == 2) {


                GlobalScope.launch(Dispatchers.Main) {
                    val visitePost = VisitPost(
                        visite.id,
                        getDateNow(),
                        0,
                        visite.storeId,
                        userId,
                        false,
                        "sortie"
                    )
                    val arayListVsitePost = ArrayList<VisitPost>()
                    arayListVsitePost.add(visitePost)
                    responseAdd = viewModelQuiz.addVisite(arayListVsitePost)

                    if (responseAdd.responseCode == 201) {
                        progress_indicator.visibility = View.GONE
                        cancel_button.isEnabled =true
                        accept.isEnabled = true
                        dialog!!.setCancelable(true)

                        val snack = Snackbar.make(
                            viewAct,
                            "Pointage de Fin envoyé avec succès",
                            Snackbar.LENGTH_LONG
                        ).setBackgroundTint(resources.getColor(R.color.purpleLogin))
                        val view: View = snack.view
                        val params = view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.CENTER
                        dismiss()
                        view.layoutParams = params
                        snack.show()
                        var targetNew = listener as CloseCheckDialogListener
                        targetNew.onClosedCheckDialog()
                    } else {
                        cancel_button.isEnabled =true
                        accept.isEnabled = true
                        dialog!!.setCancelable(true)
                        progress_indicator.visibility = View.GONE
                        val snack = Snackbar.make(
                            viewAct,
                            "Erreur envoie pointage",
                            Snackbar.LENGTH_LONG
                        ).setBackgroundTint(resources.getColor(R.color.red))
                        val view: View = snack.view
                        val params = view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.CENTER
                        dismiss()
                        adapterTask.setItems(listaTasks)
                        view.layoutParams = params
                        snack.show()
                    }


                }


            } else if (etat == 3) {
                cancel_button.isEnabled =true
                accept.isEnabled = true
                dialog!!.setCancelable(true)
                progress_indicator.visibility = View.GONE
                dismiss()
                LocationValueListener.locationOn = false
                navControllerIn.navigate(R.id.action_taskFragment_to_quizFragment)

            }


        }

        cancel_button.setOnClickListener {
            progress_indicator.visibility = View.GONE
            LocationValueListener.locationOn = true
            dismiss()
        }


    }


    private fun compareDatesDay2(simpleDate: String): String {

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val date: Date = format.parse(simpleDate)
        format.applyPattern("HH:mm")
        return format.format(date)
    }

    private fun getDateNow(): String {

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val d = Date()
        return sdf.format(d)

    }


}

