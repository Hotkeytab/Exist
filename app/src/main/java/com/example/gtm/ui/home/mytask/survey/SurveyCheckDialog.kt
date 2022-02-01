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
import android.provider.Settings
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


@AndroidEntryPoint
class SurveyCheckDialog(
    lattitude: Double, longitude: Double
) :
    DialogFragment(), LocationListener {

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 3
    private var GpsStatus = false
    private lateinit var locationIn: Location
    var latIn = lattitude
    var longIn = longitude
    var veriftest = false

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
        locationManager.removeUpdates(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getLocation()

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
            if (distanceTest > 1 && distanceTest <100)
            {
                locationManager.removeUpdates(this)
                dismiss()
                SurveyListDialog().show(requireActivity().supportFragmentManager,"survey list dialog")
            }
            else if(distanceTest > 100 )
            {
                good.visibility = View.GONE
                progress_bar.visibility = View.GONE
                bad.visibility = View.VISIBLE

            }
            else
            {
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


}

