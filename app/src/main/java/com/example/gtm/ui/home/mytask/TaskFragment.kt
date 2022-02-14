package com.example.gtm.ui.home.mytask

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.response.Visite
import com.example.gtm.data.entities.response.VisiteResponse
import com.example.gtm.databinding.FragmentTaskBinding
import com.example.gtm.ui.home.mytask.survey.SurveyCheckDialog
import com.example.gtm.utils.resources.Resource
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


@AndroidEntryPoint
class TaskFragment : Fragment(), TaskAdapter.TaskItemListener {


    private lateinit var binding: FragmentTaskBinding
    private lateinit var adapterTask: TaskAdapter
    private var listaTasks = ArrayList<Visite>()
    lateinit var sharedPref: SharedPreferences
    private lateinit var responseData: Resource<VisiteResponse>
    private val viewModel: MyTaskViewModel by viewModels()
    private var userId = 0
    private lateinit var dateTime: String
    private lateinit var fm: FragmentManager
    private lateinit var locationManager: LocationManager
    private val REQUEST_CODE = 2
    private var GpsStatus = false
    private lateinit var navController: NavController
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onStart() {
        super.onStart()

        if (isAdded) {
            LocationValueListener.locationOn = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            askForPermissions()
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskBinding.inflate(inflater, container, false)



        fm = requireActivity().supportFragmentManager

        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        userId = sharedPref.getInt("id", 0)

        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        dateTime = simpleDateFormat.format(calendar.time).toString()



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val mDrawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        //Top Bar
        topAppBar.setNavigationOnClickListener {
            mDrawerLayout.openDrawer(Gravity.LEFT)
        }

        if (isAdded)
            navController = NavHostFragment.findNavController(this)
    }


    private fun setupRecycleViewPredictionDetail() {


        adapterTask = TaskAdapter(this, requireActivity())
        binding.taskRecycleview.isMotionEventSplittingEnabled = false
        binding.taskRecycleview.layoutManager = LinearLayoutManager(requireContext())
        binding.taskRecycleview.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.taskRecycleview.adapter = adapterTask
        adapterTask.setItems(listaTasks)


    }

    override fun onClickedTask(taskId: Int, distance: String) {

        askForPermissionsDialog()
    }

    @DelicateCoroutinesApi
    private fun getVisites() {
        GlobalScope.launch(Dispatchers.Main) {

            responseData = viewModel.getVisites(userId.toString(), dateTime, dateTime)

            if (responseData.responseCode == 200) {

                listaTasks = responseData.data!!.data as ArrayList<Visite>
               /* listaTasks[0].store.lat = 22.3
                listaTasks[0].store.lng = 22.3*/
                listaTasks.sortBy { list ->
                    list.store.calculateDistance(
                        LocationValueListener.myLocation.latitude.toFloat(),
                        LocationValueListener.myLocation.longitude.toFloat()
                    )
                }
                setupRecycleViewPredictionDetail()
                binding.progressIndicator.visibility = View.GONE

            }

        }
    }


    fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionDeniedDialog()
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            }
            return false
        } else {
            Log.i("PERMISSIONBITCH", "4")
            if (CheckGpsStatus())
            // SurveyCheckDialog(latitude, Longitude,navController).show(fm, "SurveyDialog")
            {
                setUpLocationListener()

            } else {
                showPermissionDeniedGPS()
            }
        }
        return true
    }

    fun askForPermissionsDialog(): Boolean {
        if (!isPermissionsAllowed()) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionDeniedDialog()
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            }
            return false
        } else {
            Log.i("PERMISSIONBITCH", "4")
            if (CheckGpsStatus())
            // SurveyCheckDialog(latitude, Longitude,navController).show(fm, "SurveyDialog")
            {

                SurveyCheckDialog(navController).show(fm, "SurveyDialog")

            } else {
                showPermissionDeniedGPS()
            }
        }
        return true
    }


    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Autorisation refusée")
            .setMessage("L’autorisation est refusée, veuillez autoriser les autorisations à partir des paramètres de l’application.")
            .setPositiveButton("Paramètres de l’application",
                DialogInterface.OnClickListener { _, _ ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                })
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun showPermissionDeniedGPS() {
        AlertDialog.Builder(requireContext())
            .setTitle("Autorisation GPS")
            .setMessage("Veuillez autoriser le GPS à partir des paramètres de l’application.")
            .setPositiveButton("Paramètres de l’application",
                DialogInterface.OnClickListener { _, _ ->
                    // send to app settings if permission is denied permanently

                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)

                })
            .setCancelable(false)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted, you can perform your operation here
                    /* if (CheckGpsStatus())
                         SurveyCheckDialog(requireActivity(), requireContext()).show(
                             fm,
                             "SurveyDialog"
                         )
                     else {
                         showPermissionDeniedGPS()
                     } */
                    if (CheckGpsStatus())
                    // SurveyCheckDialog(latitude, Longitude,navController).show(fm, "SurveyDialog")
                    {
                        setUpLocationListener()

                    } else {
                        showPermissionDeniedGPS()
                    }

                } else {
                    // permission is denied, you can ask for permission again, if you want
                    askForPermissions()

                }
                return
            }
        }
    }


    fun CheckGpsStatus(): Boolean {
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return GpsStatus
    }


    private fun setUpLocationListener() {


        if (listaTasks.size == 0)
            binding.progressIndicator.visibility = View.VISIBLE
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
            Log.i("entrance", "Bad")
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    if(!LocationValueListener.locationOn)
                    {
                        fusedLocationClient.removeLocationUpdates(this)
                    }

                    for (location in locationResult.locations) {

                        if (location != null) {
                            if (listaTasks.size == 0) {
                                LocationValueListener.myLocation = location
                                getVisites()

                            } else {
                                LocationValueListener.myLocation = location
                                adapterTask.setItems(listaTasks)
                            }
                        } else
                            askForPermissions()
                        /*  if (location != null) {
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
                          }*/


                    }

                }
            },

            Looper.myLooper()!!
        )
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

object StaticMapClicked {
    var mapIsRunning = false
}

object LocationValueListener {
    lateinit var myLocation: Location
    var locationOn = true
}