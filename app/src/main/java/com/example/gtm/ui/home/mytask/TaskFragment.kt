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
import androidx.fragment.app.commitNow
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.gtm.data.entities.response.TimeClass
import com.example.gtm.ui.home.mytask.addvisite.AddVisteDialog
import com.example.gtm.ui.home.suivie.ChoixImageDialogSuivie
import kotlinx.android.synthetic.main.dialog_add_visite.*
import kotlinx.android.synthetic.main.fragment_task.swiperefreshlayout

// In this fragment , I had to do many filters because the backend service is giving me wrong results
// For example : if I ask for  getVisite() Between 6th April and 10th April , the service will give me
// Results Between 5th April and 11th April , that's why I had to filter and verify data before showing it

@AndroidEntryPoint
class TaskFragment : Fragment(), TaskAdapter.TaskItemListener,
    SurveyCheckDialog.CloseCheckDialogListener {


    private lateinit var binding: FragmentTaskBinding
    private lateinit var adapterTask: TaskAdapter
    private var listaTasks = ArrayList<Visite>()
    lateinit var sharedPref: SharedPreferences
    private lateinit var responseData: Resource<VisiteResponse>
    private val viewModel: MyTaskViewModel by viewModels()
    private var userId = 0
    private lateinit var dateTimeBegin: String
    private lateinit var dateTimeEnd: String
    private var fm: FragmentManager? = null
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

        //If Fragment is Added and Activity not null
        if (isAdded && activity != null) {
            //Set Location Fused Listener
            LocationValueListener.locationOn = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

            //Ask Permission GPS
            askForPermissions()
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskBinding.inflate(inflater, container, false)


        //If Fragment is Added and activity is not null
        if (isAdded && activity != null) {

            //Get Current Date
            d = Date()

            //Init ChildFragmentManager
            fm = childFragmentManager


            //Init UserID
            sharedPref = requireContext().getSharedPreferences(
                R.string.app_name.toString(),
                Context.MODE_PRIVATE
            )
            userId = sharedPref.getInt("id", 0)


            //Get Time Begin Date and Time End Date
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            dateTimeBegin = simpleDateFormat.format(d.time).toString()
            dateTimeEnd = simpleDateFormat.format(d.time).toString()


        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)



        //If Fragment is Added and activity not null
        if (isAdded && activity != null) {
            //Get Drawer Layout instance
            val mDrawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)

            //Top Bar
            topAppBar.setNavigationOnClickListener {
                mDrawerLayout.openDrawer(Gravity.LEFT)
            }

            //Make Bottom Nav Visible
            requireActivity().bottom_nav.visibility = View.VISIBLE


            //init Nav Controller
            navController = NavHostFragment.findNavController(this)


            //Correct Top Date Filters
            correctFilters()

            //SetToday Filter and updateUI
            setTodayLight()


            //Set Top Date filters and correct UI
            setTopDate()

            //Day Card Click Listener
            binding.dayfiltercard.setOnClickListener {
                daysFilter.dayFilter = 1
                daysFilter.weekFilter = 0
                daysFilter.monthFilter = 0
                correctFilters()
                setTopDate()
                binding.progressIndicator.visibility = View.VISIBLE
                getVisites()
            }
            //Week Card Click Listener
            binding.weekfilterward.setOnClickListener {
                daysFilter.dayFilter = 0
                daysFilter.weekFilter = 1
                daysFilter.monthFilter = 0
                correctFilters()
                binding.progressIndicator.visibility = View.VISIBLE
                setTopDate()

            }

            //Month Card Click Listener
            binding.montherfiltercard.setOnClickListener {
                daysFilter.dayFilter = 0
                daysFilter.weekFilter = 0
                daysFilter.monthFilter = 1
                correctFilters()
                binding.progressIndicator.visibility = View.VISIBLE
                setTopDate()
            }




            //Next Date Button Listener
            binding.nextDate.setOnClickListener {
                nextDate()
            }

            //previous Date Button Listener
            binding.previousDate.setOnClickListener {
                previousDate()
            }

            //Current day Button Listener
            binding.today.setOnClickListener {
                setToday()
            }


            //Add Visite Button Listener
            binding.fab.setOnClickListener {

                //Init Visite Dialog and SHow it
                addVisiteDialog = AddVisteDialog(listaTasks)
                addVisiteDialog.show(fm!!, "add")
                fm!!.executePendingTransactions()

                //Add Visite Dialog onCLose Listener
                addVisiteDialog.dialog!!.setOnDismissListener {
                    getVisites()
                }


            }


        }

        //onSwipeDown Listener
        binding.swiperefreshlayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getVisites()
            swiperefreshlayout.isRefreshing = false
        })


    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.top_app_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }


    //Set Lista Visite RecycleView
    private fun setupRecycleViewPredictionDetail() {

        adapterTask = TaskAdapter(this, requireActivity(), activity as DrawerActivity, listaTasks)
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



    //OnClicked Visite
    override fun onClickedTask(taskId: Int, distance: String, visite2: Visite, theDistance: Float) {

        if (theDistance < 250) {
            visite = visite2

            //Ask for GPS Permission
            askForPermissionsDialog()

            //Put StoreId in sharedPref
            sharedPref =
                requireContext().getSharedPreferences(
                    R.string.app_name.toString(),
                    Context.MODE_PRIVATE
                )!!
            with(sharedPref.edit()) {
                this?.putInt("storeId", taskId)
            }?.commit()
        }
    }


    //Get All Visites for the current User
    @DelicateCoroutinesApi
    private fun getVisites() {
        lifecycleScope.launch(Dispatchers.Main) {

            //If Fragment is not detached
            if (!isDetached) {

                //If fragment is  Added
                if (isAdded) {

                    //Get response Data of ALl Visite
                    responseData =
                        viewModel.getVisites(userId.toString(), dateTimeBegin, dateTimeEnd)


                    //If response Good
                    if (responseData.responseCode == 200) {

                        //Ftech Lista Visites
                        listaTasks = responseData.data!!.data as ArrayList<Visite>

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

                        //Sort by Distance
                        if (daysFilter.dayFilter == 1)
                            listaTasks.sortBy { list ->
                                list.store.calculateDistance(
                                    LocationValueListener.myLocation.latitude.toFloat(),
                                    LocationValueListener.myLocation.longitude.toFloat()
                                )
                            }

                        //Sort by Date
                        if (daysFilter.weekFilter == 1 || daysFilter.monthFilter == 1) {
                            Collections.sort(listaTasks, SortByDate())
                        }

                        //If Fragment is Added and activity isn't null
                        if (isAdded && activity != null)
                            setupRecycleViewPredictionDetail()
                        binding.progressIndicator.visibility = View.GONE

                    }
                }

            }
        }
    }


    //test if Permission (Gps) Is Allowed
    fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    //Ask for Permission Gps
    fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionDeniedDialog()
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            }
            return false
        } else {
            if (CheckGpsStatus()) {
                setUpLocationListener()

            } else {
                showPermissionDeniedGPS()
            }
        }
        return true
    }

    //SHow the Dialog of Ask Permissions( Like GPS)
    fun askForPermissionsDialog(): Boolean {
        if (!isPermissionsAllowed()) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionDeniedDialog()
            } else {

                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            }
            return false
        } else {
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
                    ).show(fm!!, "SurveyDialog")
                else {
                    if (visite.end == null)
                        ChoixImageDialogSuivie(
                            this,
                            visite.pe,
                            visite.ps,
                            navController,
                            visite,
                            requireView(),
                            adapterTask,
                            listaTasks
                        ).show(fm!!, "ChoixImageSuivi")
                }

            } else {
                showPermissionDeniedGPS()
            }
        }
        return true
    }


    //SHow DIalog Permission Denied
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


    //Show Denied GPS
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


    //Check GPS Status if Activated or not
    fun CheckGpsStatus(): Boolean {
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return GpsStatus
    }


    //Sort by Date internal Class
    internal class SortByDate : Comparator<Visite?> {
        override fun compare(p0: Visite?, p1: Visite?): Int {
            return p0!!.day.compareTo(p1!!.day)
        }
    }

    //SetUpLocation Listener
    private fun setUpLocationListener() {

        if (listaTasks.size == 0)
            binding.progressIndicator.visibility = View.VISIBLE
        // for getting the current location update after every 2 seconds with high accuracy


        val locationRequest = LocationRequest.create().apply {
            interval = 2000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 2000
        }

        /*  val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
              .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) */
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
                    }

                }
            },

            Looper.myLooper()!!
        )
    }


    //Calculate Distance from Lat and Lng
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


    //Compare Date Between Dates
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


    //Compare Dates Between Months
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


    //Correct top filters in UI
    private fun correctFilters() {
        setFilters(binding.dayfiltercard, binding.dayfiltertext, daysFilter.dayFilter)
        setFilters(binding.weekfilterward, binding.weektextfilter, daysFilter.weekFilter)
        setFilters(binding.montherfiltercard, binding.monthfiltertext, daysFilter.monthFilter)
    }


    //Set Filters in UI
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


    //Set Top Date in UI After change
    private fun setTopDate() {

        if (daysFilter.dayFilter == 1) {
            val sdf = SimpleDateFormat("EEEE")
            val sdf2 = SimpleDateFormat("dd MMM")
            val dayOfTheWeek = sdf.format(d)
            val dayOfTheWeek2 = sdf2.format(d)
            binding.topAppBar.title = "$dayOfTheWeek $dayOfTheWeek2"

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


    //Next Date Function After click
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

    //Previous Date function after click
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


    //Set Today function
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

    //SetToday Color after changing date
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


    //Get day of current week
    private fun dayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        calendar.time = d
        val day = calendar[Calendar.DAY_OF_WEEK]

        return if (day in 2..7)
            day - 2
        else
            6
    }


    override fun onDestroy() {
        super.onDestroy()
        LocationValueListener.locationOn = false
        fm = null
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