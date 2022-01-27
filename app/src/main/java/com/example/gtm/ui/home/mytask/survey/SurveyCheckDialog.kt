package com.example.gtm.ui.home.mytask.survey


import android.Manifest
import android.app.Activity
import android.content.Context
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


@AndroidEntryPoint
class SurveyCheckDialog(activity:Activity,context:Context
) :
    DialogFragment(), LocationListener {

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 3

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

        getLocation()

    }

    override fun onLocationChanged(location: Location) {

        position.text = location.latitude.toString()
      /*    tvGpsLocation.text = "Latitude: " + location.latitude + " , Longitude: " + location.longitude
          Log.i("GPSLOCATION","${location.latitude}") */
    }

     private fun getLocation() {
         locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
         if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
             ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
         }
         locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
     }

     override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
         Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
         if (requestCode == locationPermissionCode) {
             if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
                 Log.i("PERMISSIONBITCH","GRANTED")
             }
             else {
                 Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                 Log.i("PERMISSIONBITCH","DENIED")
             }
         }
     }

}

