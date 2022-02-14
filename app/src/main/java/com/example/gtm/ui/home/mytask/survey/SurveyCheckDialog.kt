package com.example.gtm.ui.home.mytask.survey


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.gtm.R
import com.example.gtm.ui.home.mytask.StaticMapClicked
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.gms.maps.CameraUpdate
import kotlinx.android.synthetic.main.dialog_quiz_confirmation.*
import kotlinx.android.synthetic.main.fragment_position_map.*
import kotlinx.android.synthetic.main.item_task.*
import android.location.GpsStatus
import android.os.Looper
import android.provider.Settings
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.gtm.ui.home.mytask.LocationValueListener
import com.google.android.gms.location.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


@AndroidEntryPoint
class SurveyCheckDialog(
     navController: NavController
) :
    DialogFragment() {

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 3
    private var GpsStatus = false
    private lateinit var locationIn: Location
    var veriftest = false
    val navControllerIn = navController
    var testGps = false

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

        accept.setOnClickListener {

            LocationValueListener.locationOn = false
            navControllerIn.navigate(R.id.action_taskFragment_to_quizFragment)
            dismiss()
        }

        cancel_button.setOnClickListener {
            LocationValueListener.locationOn = true
            dismiss()
        }


    }




}

