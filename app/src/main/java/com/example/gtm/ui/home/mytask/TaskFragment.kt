package com.example.gtm.ui.home.mytask

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.data.entities.response.Visite
import com.example.gtm.data.entities.response.VisiteResponse
import com.example.gtm.databinding.FragmentTaskBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.mytask.survey.SurveyCheckDialog
import com.example.gtm.utils.resources.Resource
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
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
import android.view.*
import com.example.gtm.R
import android.view.MenuInflater
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.example.gtm.data.entities.response.TimeClass
import com.example.gtm.ui.home.mytask.addvisite.AddVisteDialog
import com.example.gtm.ui.home.suivie.ChoixImageDialogSuivie


@AndroidEntryPoint
class TaskFragment : Fragment(), TaskAdapter.TaskItemListener,
    SurveyCheckDialog.CloseCheckDialogListener {


    private lateinit var binding: FragmentTaskBinding
    private lateinit var adapterTask: TaskAdapter
    private var listaTasks = ArrayList<Visite>()
    lateinit var sharedPref: SharedPreferences
    private lateinit var responseData: Resource<VisiteResponse>
    private lateinit var responseTime: Resource<TimeClass>
    private val viewModel: MyTaskViewModel by viewModels()
    private var userId = 0
    private lateinit var dateTimeBegin: String
    private lateinit var dateTimeEnd: String
    private lateinit var fm: FragmentManager
    private lateinit var locationManager: LocationManager
    private val REQUEST_CODE = 2
    private var GpsStatus = false
    private lateinit var navController: NavController
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var d: Date
    private lateinit var visite: Visite
    private lateinit var addVisiteDialog: AddVisteDialog


    override fun onStart() {
        super.onStart()

        DrawerActivity.trackState.currentOne = "planning du jour"

        if (isAdded && activity != null) {

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



        if (isAdded && activity != null) {


            d = Date()

            fm = requireActivity().supportFragmentManager

            sharedPref = requireContext().getSharedPreferences(
                R.string.app_name.toString(),
                Context.MODE_PRIVATE
            )
            userId = sharedPref.getInt("id", 0)


            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            dateTimeBegin = simpleDateFormat.format(d.time).toString()
            dateTimeEnd = simpleDateFormat.format(d.time).toString()

        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // This callback will only be called when MyFragment is at least Started.
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)



        if (isAdded && activity != null) {
            Log.i("repeat", "1")
            val mDrawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
            //Top Bar
            topAppBar.setNavigationOnClickListener {
                mDrawerLayout.openDrawer(Gravity.LEFT)
            }

            requireActivity().bottom_nav.visibility = View.VISIBLE


            navController = NavHostFragment.findNavController(this)

            correctFilters()


            setTodayLight()
            setTopDate()

            binding.dayfiltercard.setOnClickListener {
                daysFilter.dayFilter = 1
                daysFilter.weekFilter = 0
                daysFilter.monthFilter = 0
                correctFilters()
                setTopDate()
                binding.progressIndicator.visibility = View.VISIBLE
                getVisites()
            }
            binding.weekfilterward.setOnClickListener {
                daysFilter.dayFilter = 0
                daysFilter.weekFilter = 1
                daysFilter.monthFilter = 0
                correctFilters()
                binding.progressIndicator.visibility = View.VISIBLE
                setTopDate()

            }

            binding.montherfiltercard.setOnClickListener {
                daysFilter.dayFilter = 0
                daysFilter.weekFilter = 0
                daysFilter.monthFilter = 1
                correctFilters()
                binding.progressIndicator.visibility = View.VISIBLE
                setTopDate()
            }




            binding.nextDate.setOnClickListener {
                nextDate()
            }
            binding.previousDate.setOnClickListener {
                previousDate()
            }

            binding.today.setOnClickListener {
                setToday()
            }


            binding.fab.setOnClickListener {

                addVisiteDialog = AddVisteDialog()
                addVisiteDialog.show(fm, "AddVisteDialog")

                fm.executePendingTransactions()
                addVisiteDialog.dialog!!.setOnDismissListener {
                    getVisites()
                }

            }


        }


    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.top_app_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return super.onOptionsItemSelected(item)
    }


    private fun setupRecycleViewPredictionDetail() {


        Log.i("repeat", "1")
        adapterTask = TaskAdapter(this, requireActivity(), activity as DrawerActivity, listaTasks)
        binding.taskRecycleview.isMotionEventSplittingEnabled = false
        binding.taskRecycleview.layoutManager = LinearLayoutManager(requireContext())
        binding.taskRecycleview.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.taskRecycleview.adapter = adapterTask
        // (activity as DrawerActivity).listOfTriDates = ArrayList<String>()
        Log.i("triggered", "triggered")
        adapterTask.setItems(listaTasks)


    }

    override fun onClickedTask(taskId: Int, distance: String, visite2: Visite) {

        visite = visite2
        askForPermissionsDialog()
        sharedPref =
            requireContext().getSharedPreferences(
                R.string.app_name.toString(),
                Context.MODE_PRIVATE
            )!!
        with(sharedPref.edit()) {
            this?.putInt("storeId", taskId)
        }?.commit()
    }

    @DelicateCoroutinesApi
    private fun getVisites() {
        lifecycleScope.launch(Dispatchers.Main) {

            progress_indicator.visibility = View.VISIBLE

            if (!isDetached) {
                Log.i("repeat", "1")

                if (isAdded) {
                    responseData =
                        viewModel.getVisites(userId.toString(), dateTimeBegin, dateTimeEnd)


                    if (responseData.responseCode == 200) {
                        listaTasks = responseData.data!!.data as ArrayList<Visite>
                        /* listaTasks[0].store.lat = 22.3
                     listaTasks[0].store.lng = 22.3*/

                        if (daysFilter.dayFilter == 1)
                            listaTasks =
                                listaTasks.filter { list -> compareDatesDay(list.day) } as ArrayList<Visite>
                        else if (daysFilter.weekFilter == 1)
                            listaTasks =
                                listaTasks.filter { list -> compareDatesMonth(list.day) } as ArrayList<Visite>
                        else if (daysFilter.monthFilter == 1)
                            listaTasks =
                                listaTasks.filter { list -> compareDatesMonth(list.day) } as ArrayList<Visite>

                        if (listaTasks.size == 0)
                            binding.novisit.visibility = View.VISIBLE
                        else
                            binding.novisit.visibility = View.GONE

                        if (daysFilter.dayFilter == 1)
                            listaTasks.sortBy { list ->
                                list.store.calculateDistance(
                                    LocationValueListener.myLocation.latitude.toFloat(),
                                    LocationValueListener.myLocation.longitude.toFloat()
                                )
                            }

                        if (daysFilter.weekFilter == 1 || daysFilter.monthFilter == 1) {
                            Collections.sort(listaTasks, SortByDate())
                            //  transformListToHashMapDate()
                        }

                        if (isAdded && activity != null)
                            setupRecycleViewPredictionDetail()
                        binding.progressIndicator.visibility = View.GONE

                    }
                }

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
                if (visite.pe == 1 && visite.ps == 1)
                    SurveyCheckDialog(
                        this,
                        navController,
                        3,
                        requireView(),
                        adapterTask,
                        listaTasks,
                        visite
                    ).show(fm, "SurveyDialog")
                else
                    ChoixImageDialogSuivie(
                        this,
                        visite.pe,
                        visite.ps,
                        navController,
                        visite,
                        requireView(),
                        adapterTask,
                        listaTasks
                    ).show(fm, "ChoixImageSuivi")

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


    internal class SortByDate : Comparator<Visite?> {
        override fun compare(p0: Visite?, p1: Visite?): Int {
            return p0!!.day.compareTo(p1!!.day)
        }
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

                    if (!LocationValueListener.locationOn) {
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


    private fun compareDatesDay(simpleDate: String): Boolean {


        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date: Date = format.parse(simpleDate)
        format.applyPattern("dd-MM-yyyy")
        val dateformat = format.format(date)

        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(d)

        if (dateformat == currentDate) {

            return true
        }

        return false

    }


    private fun compareDatesMonth(simpleDate: String): Boolean {


        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date: Date = format.parse(simpleDate)

        format.applyPattern("yyyy-MM-dd")
        val dateformat = format.format(date)

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate1 = sdf.parse(dateTimeBegin)
        val currentDate2 = sdf.parse(dateTimeEnd)

        if (sdf.parse(dateformat) < currentDate1 || sdf.parse(dateformat) > currentDate2) {

            return false
        }

        return true

    }


    private fun correctFilters() {
        setFilters(binding.dayfiltercard, binding.dayfiltertext, daysFilter.dayFilter)
        setFilters(binding.weekfilterward, binding.weektextfilter, daysFilter.weekFilter)
        setFilters(binding.montherfiltercard, binding.monthfiltertext, daysFilter.monthFilter)
    }


    @SuppressLint("ResourceAsColor")
    private fun setFilters(cardview: CardView, textView: TextView, filter: Int) {
        if (filter == 1) {
            cardview.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.white)

            textView.setTextColor(resources.getColor(R.color.purpleLogin))
        } else {
            cardview.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.purpleLogin)

            textView.setTextColor(resources.getColor(R.color.white))

        }
    }


    private fun setTopDate() {

        if (daysFilter.dayFilter == 1) {
            val sdf = SimpleDateFormat("EEEE")
            val sdf2 = SimpleDateFormat("dd MMM")
            val dayOfTheWeek = sdf.format(d)
            val dayOfTheWeek2 = sdf2.format(d)
            binding.topAppBar.title = "$dayOfTheWeek $dayOfTheWeek2"
            Log.i("repeat", "2")

        } else if (daysFilter.weekFilter == 1) {

            val sdf = SimpleDateFormat("yyyy-MM-dd")
            //For DateTimeBegin
            val cal = Calendar.getInstance()
            cal.time = d
            cal.add(Calendar.DATE, -dayOfWeek())
            val d2 = cal.time
            dateTimeBegin = sdf.format(d2.time).toString()
            //For DateTimeEnd
            val cal2 = Calendar.getInstance()
            cal2.time = d
            cal2.add(Calendar.DATE, 5 - dayOfWeek() + 1)
            val d3 = cal2.time
            dateTimeEnd = sdf.format(d3.time).toString()


            //change title for week
            val sdf3 = SimpleDateFormat("dd MMM")


            val dayOfTheWeek = sdf3.format(sdf.parse(dateTimeBegin))
            val dayOfTheWeek2 = sdf3.format(sdf.parse(dateTimeEnd))

            binding.topAppBar.title = "$dayOfTheWeek - $dayOfTheWeek2"

            getVisites()


        } else if (daysFilter.monthFilter == 1) {

            val sdf = SimpleDateFormat("MMMM")
            val sdf1 = SimpleDateFormat("yyyy")
            val dayOfTheWeek = sdf.format(d)
            val dayOfTheWeek2 = sdf1.format(d)
            binding.topAppBar.title = "$dayOfTheWeek $dayOfTheWeek2"

            val cal = Calendar.getInstance()
            cal.time = d
            var daysMin = cal.getActualMinimum(Calendar.DAY_OF_MONTH).toString()
            var daysMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH).toString()

            if (daysMin.length == 1)
                daysMin = "0$daysMin"

            if (daysMax.length == 1)
                daysMax = "0$daysMax"


            val sdfMonth = SimpleDateFormat("MM")
            val month = sdfMonth.format(d)
            val sdfYear = SimpleDateFormat("yyyy")
            val year = sdfYear.format(d)

            dateTimeBegin = "$year-$month-$daysMin"
            dateTimeEnd = "$year-$month-$daysMax"

            getVisites()
        }
    }


    private fun nextDate() {
        if (daysFilter.dayFilter == 1) {
            val cal = Calendar.getInstance()
            cal.time = d
            cal.add(Calendar.DATE, 1)
            d = cal.time
            setTopDate()
            binding.progressIndicator.visibility = View.VISIBLE
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            dateTimeBegin = simpleDateFormat.format(d.time).toString()
            dateTimeEnd = simpleDateFormat.format(d.time).toString()
            getVisites()
        } else if (daysFilter.weekFilter == 1) {
            val cal = Calendar.getInstance()
            cal.time = d
            cal.add(Calendar.DATE, 7)
            d = cal.time
            setTopDate()
            binding.progressIndicator.visibility = View.VISIBLE
        } else if (daysFilter.monthFilter == 1) {
            val cal = Calendar.getInstance()
            cal.time = d
            cal.add(Calendar.MONTH, 1)
            d = cal.time
            setTopDate()
            binding.progressIndicator.visibility = View.VISIBLE
        }
    }

    private fun previousDate() {
        if (daysFilter.dayFilter == 1) {
            val cal = Calendar.getInstance()
            cal.time = d
            cal.add(Calendar.DATE, -1)
            d = cal.time
            setTopDate()
            binding.progressIndicator.visibility = View.VISIBLE
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            dateTimeBegin = simpleDateFormat.format(d.time).toString()
            dateTimeEnd = simpleDateFormat.format(d.time).toString()
            getVisites()
        } else if (daysFilter.weekFilter == 1) {
            val cal = Calendar.getInstance()
            cal.time = d
            cal.add(Calendar.DATE, -7)
            d = cal.time
            setTopDate()
            binding.progressIndicator.visibility = View.VISIBLE
        } else if (daysFilter.monthFilter == 1) {
            val cal = Calendar.getInstance()
            cal.time = d
            cal.add(Calendar.MONTH, -1)
            d = cal.time
            setTopDate()
            binding.progressIndicator.visibility = View.VISIBLE
        }

    }


    private fun setToday() {
        daysFilter.dayFilter = 1
        daysFilter.weekFilter = 0
        daysFilter.monthFilter = 0
        correctFilters()
        val cal = Calendar.getInstance()
        d = cal.time
        setTopDate()
        binding.progressIndicator.visibility = View.VISIBLE
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        dateTimeBegin = simpleDateFormat.format(d.time).toString()
        dateTimeEnd = simpleDateFormat.format(d.time).toString()
        getVisites()
    }

    private fun setTodayLight() {
        daysFilter.dayFilter = 1
        daysFilter.weekFilter = 0
        daysFilter.monthFilter = 0
        correctFilters()
        val cal = Calendar.getInstance()
        d = cal.time
        setTopDate()
        binding.progressIndicator.visibility = View.VISIBLE
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        dateTimeBegin = simpleDateFormat.format(d.time).toString()
        dateTimeEnd = simpleDateFormat.format(d.time).toString()
    }


    private fun dayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        calendar.time = d
        val day = calendar[Calendar.DAY_OF_WEEK]

        return if (day in 2..7)
            day - 2
        else
            6
    }


    private fun transformListToHashMapDate() {
        Log.i("myhashmap", "${(listaTasks.size)}")
        var exist = false

        if (listaTasks.size != 0) {


            for (i in listaTasks) {

                exist = false

                if ((activity as DrawerActivity).HashMaplistaTasksDate.size == 0) {
                    val tempAray = ArrayList<Visite>()
                    tempAray.add(i)
                    (activity as DrawerActivity).HashMaplistaTasksDate.put(
                        i.day,
                        tempAray
                    )
                } else {

                    (activity as DrawerActivity).HashMaplistaTasksDate.forEach { (v, k) ->
                        if (i.day == v) {
                            k.add(i)
                            exist = true
                        }
                    }

                    if (!exist) {
                        val tempAray = ArrayList<Visite>()
                        tempAray.add(i)
                        (activity as DrawerActivity).HashMaplistaTasksDate.put(i.day, tempAray)
                    }
                }
            }

        }

        sortHashMapVisite()
        /* (activity as DrawerActivity).HashMaplistaTasksDate.forEach { (v,k) ->
             Log.i("myhashmap","$v")
             Log.i("myhashmap","$k")
         }*/
        //  Log.i("myhashmap", "${(activity as DrawerActivity).HashMaplistaTasksDate}")
    }


    private fun sortHashMapVisite() {
        val entries: Set<Map.Entry<String, ArrayList<Visite>>> =
            (activity as DrawerActivity).HashMaplistaTasksDate.entries

        val sorted: TreeMap<String, ArrayList<Visite>> =
            TreeMap((activity as DrawerActivity).HashMaplistaTasksDate)

        val mappings: Set<Map.Entry<String, ArrayList<Visite>>> = sorted.entries

        mappings.forEach { (k, v) ->
            Log.i("myhashmap", "$k")
            Log.i("myhashmap", "$v")
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        LocationValueListener.locationOn = false
    }

    override fun onClosedCheckDialog() {
        getVisites()
    }

}

object StaticMapClicked {
    var mapIsRunning = false
}

object LocationValueListener {
    lateinit var myLocation: Location
    var locationOn = true
}


object daysFilter {
    var dayFilter = 1
    var weekFilter = 0
    var monthFilter = 0
}