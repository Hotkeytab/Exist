package com.example.gtm.ui.home.mytask.survey


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
import com.example.gtm.data.entities.response.TimeClass
import com.example.gtm.data.entities.response.Visite
import com.example.gtm.ui.home.mytask.LocationValueListener
import com.example.gtm.ui.home.mytask.MyTaskViewModel
import com.example.gtm.ui.home.mytask.TaskAdapter
import com.example.gtm.utils.resources.Resource
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class SurveyCheckDialog(
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
    private lateinit var responseTime: Resource<TimeClass>
    private val viewModel: MyTaskViewModel by viewModels()

    // declare a global variable of FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient


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

        // getLocation()

        if (etat == 1) {
            title1.text = "Pointage"
            textcontext1.text = "Souhaitez vous confirmer le pointage d'arrivée ?"
        } else if (etat == 2) {
            title1.text = "Pointage"
            textcontext1.text = "Souhaitez vous confirmer le pointage de départ ?"
        } else if (etat == 3) {
            title1.text = "Questionnaire"
            textcontext1.text = "Souhaitez vous répondre au questionnaire ? "
        }

        accept.setOnClickListener {


            if (etat == 1) {

                lifecycleScope.launch(Dispatchers.Main) {

                    responseTime = viewModel.getTime() as Resource<TimeClass>
                    Log.i("timetime","$responseTime")

                    if(responseTime.responseCode == 200)
                    {
                        visite.pe = 1
                        visite.pe_time =  compareDatesDay2(responseTime.data!!.datetime)
                        val snack = Snackbar.make(
                            viewAct,
                            "Pointage d'Arrivée envoyé avec succès",
                            Snackbar.LENGTH_LONG
                        ).setBackgroundTint(resources.getColor(R.color.purpleLogin))
                        val view: View = snack.view
                        val params = view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.CENTER
                        view.layoutParams = params
                        snack.show()
                    }

                    else
                    {
                        val snack = Snackbar.make(
                            viewAct,
                            "Erreur envoie pointage",
                            Snackbar.LENGTH_LONG
                        ).setBackgroundTint(resources.getColor(R.color.red))
                        val view: View = snack.view
                        val params = view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.CENTER
                        view.layoutParams = params
                        snack.show()
                    }


                }



            } else if (etat == 2) {



                lifecycleScope.launch(Dispatchers.Main) {

                    responseTime = viewModel.getTime() as Resource<TimeClass>
                    Log.i("timetime", "$responseTime")

                    if(responseTime.responseCode == 200)
                    {
                        visite.ps = 1
                        visite.ps_time =  compareDatesDay2(responseTime.data!!.datetime)
                        val snack = Snackbar.make(
                            viewAct,
                            "Pointage de Départ envoyé avec succès",
                            Snackbar.LENGTH_LONG
                        ).setBackgroundTint(resources.getColor(R.color.purpleLogin))
                        val view: View = snack.view
                        val params = view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.CENTER
                        view.layoutParams = params
                        snack.show()
                    }

                    else
                    {
                        val snack = Snackbar.make(
                            viewAct,
                            "Erreur envoie pointage",
                            Snackbar.LENGTH_LONG
                        ).setBackgroundTint(resources.getColor(R.color.red))
                        val view: View = snack.view
                        val params = view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.CENTER
                        view.layoutParams = params
                        snack.show()
                    }

                }



            } else if (etat == 3) {
                LocationValueListener.locationOn = false
                navControllerIn.navigate(R.id.action_taskFragment_to_quizFragment)

            }

            dismiss()
            adapterTask.setItems(listaTasks)
        }

        cancel_button.setOnClickListener {
            LocationValueListener.locationOn = true
            dismiss()
        }


    }


    private fun compareDatesDay2(simpleDate: String): String {

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val date: Date = format.parse(simpleDate)
        format.applyPattern("HH:MM")
        return format.format(date)
    }


}

