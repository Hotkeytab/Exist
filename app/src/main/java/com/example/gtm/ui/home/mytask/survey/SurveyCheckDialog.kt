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
import com.google.android.gms.location.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


@AndroidEntryPoint
class SurveyCheckDialog(
    lattitude: Double, longitude: Double, navController: NavController
) :
    DialogFragment(), LocationListener {

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 3
    private var GpsStatus = false
    private lateinit var locationIn: Location
    var latIn = lattitude
    var longIn = longitude
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

            progress_bar.visibility = View.VISIBLE
            title1.visibility = View.GONE
            title2.visibility = View.VISIBLE
            sign.visibility = View.GONE
            accept.visibility = View.GONE
            cancel_button.visibility = View.GONE
            textcontext1.visibility = View.GONE
            textcontext2.visibility = View.VISIBLE
            veriftest = true

        //    getLastKnownLocation()

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

            setUpLocationListener()
        }

        cancel_button.setOnClickListener {
            dismiss()
        }


    }

    override fun onLocationChanged(location: Location) {


        if (me != null && veriftest) {
            me.text = "LAT : ${location.latitude} / LONG : ${location.longitude}"
            him.text = "LAT : ${latIn} / LONG : ${longIn}"
            /* finalresult.text = distance(
                 location.latitude.toFloat(),
                 location.longitude.toFloat(),
                 latIn.toFloat(),
                 longIn.toFloat()
             ).toString() */
            val distanceTest = distance(
                location.latitude.toFloat(),
                location.longitude.toFloat(),
                latIn.toFloat(),
                longIn.toFloat()
            )
            distance.text = distanceTest.toString()
            if (distanceTest > 1 && distanceTest < 100) {
                locationManager.removeUpdates(this)
                dismiss()
                //    SurveyListDialog().show(requireActivity().supportFragmentManager,"survey list dialog")

                navControllerIn.navigate(R.id.action_taskFragment_to_quizFragment)
            } else {
                dismiss()
            }
        }


        Log.i("POSITIONGPSNOW", location.latitude.toString())
        /*    tvGpsLocation.text = "Latitude: " + location.latitude + " , Longitude: " + location.longitude
            Log.i("GPSLOCATION","${location.latitude}") */
    }

    private fun getLocation() {
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
        // in onCreate() initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        //GPS ONE
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.1f, this)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }


    fun distance(lat_a: Float, lng_a: Float, lat_b: Float, lng_b: Float): Float {
        val earthRadius = 3958.75
        val latDiff = Math.toRadians((lat_b - lat_a).toDouble())
        val lngDiff = Math.toRadians((lng_b - lng_a).toDouble())
        val a = sin(latDiff / 2) * sin(latDiff / 2) +
                cos(Math.toRadians(lat_a.toDouble())) * cos(Math.toRadians(lat_b.toDouble())) *
                sin(lngDiff / 2) * sin(lngDiff / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = earthRadius * c
        val meterConversion = 1609
        return (distance * meterConversion.toFloat()).toFloat()
    }

    fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }

        Log.i("HAHAH","called")
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                    val distanceTest = distance(
                        location.latitude.toFloat(),
                        location.longitude.toFloat(),
                        latIn.toFloat(),
                        longIn.toFloat()
                    )


                    Log.i("HAHAH","$distanceTest")
                    if (distanceTest > 1 && distanceTest < 150) {
                        locationManager.removeUpdates(this)
                        dismiss()

                        //    SurveyListDialog().show(requireActivity().supportFragmentManager,"survey list dialog")

                        navControllerIn.navigate(R.id.action_taskFragment_to_quizFragment)
                    } else {
                        dismiss()
                    }
                }
                else
                {
                    Log.i("HAHAH","NUL")
                }

            }


    }

    private fun setUpLocationListener() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        if(!testGps)

            fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {

                        if (location != null) {
                            val distanceTest = distance(
                                location.latitude.toFloat(),
                                location.longitude.toFloat(),
                                latIn.toFloat(),
                                longIn.toFloat()
                            )


                            Log.i("HAHAH", "$distanceTest")
                            if (distanceTest > 1 && distanceTest < 150) {
                                fusedLocationClient.removeLocationUpdates(this)
                                dismiss()
                                //    SurveyListDialog().show(requireActivity().supportFragmentManager,"survey list dialog")
                                navControllerIn.navigate(R.id.action_taskFragment_to_quizFragment)

                            } else {
                                dismiss()
                            }
                        }

                    }

                }
            },

            Looper.myLooper()!!
        )
    }


}

