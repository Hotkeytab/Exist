package com.example.gtm.ui.home.suivie


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.gtm.R
import com.example.gtm.data.entities.response.DataX
import com.example.gtm.data.entities.response.SurveyResponse
import com.example.gtm.data.entities.response.Visite
import com.example.gtm.data.entities.response.VisiteResponse
import com.example.gtm.databinding.FragmentSuiviePlanningBinding
import com.example.gtm.databinding.FragmentTaskBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.mytask.*
import com.example.gtm.ui.home.mytask.TaskFragment
import com.example.gtm.ui.home.mytask.survey.SurveyCheckDialog
import com.example.gtm.utils.resources.Resource
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.dialog_add_visite.*
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.android.synthetic.main.fragment_task.swiperefreshlayout
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
class SuiviePlanningFragment : Fragment(), SuiviePlanningBlocAdapter.TaskItemListener {


    private lateinit var binding: FragmentSuiviePlanningBinding
    private lateinit var adapterTask: SuiviePlanningBlocAdapter
    private var listaTasks = ArrayList<Visite>()
    private var listaSurveyResponse = ArrayList<DataX>()
    lateinit var sharedPref: SharedPreferences
    private lateinit var responseData: Resource<VisiteResponse>
    private lateinit var responseData2: Resource<SurveyResponse>
    private val viewModel: MyTaskViewModel by viewModels()
    private var userId = 0
    private lateinit var dateTimeBegin: String
    private lateinit var dateTimeEnd: String
    private lateinit var fm: FragmentManager
    private lateinit var locationManager: LocationManager
    private val REQUEST_CODE = 2
    private var GpsStatus = false
    private lateinit var d: Date




    override fun onStart() {
        super.onStart()


        //If Fragment os Added then Verify Gps Permission
        if (isAdded && activity != null) {
            askForPermissions()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSuiviePlanningBinding.inflate(inflater, container, false)


        //If Fragment is added and Activity isn't null
        if (isAdded && activity != null) {

            //Get Current date
            d = Date()

            //Init Fragment Manager
            fm = requireActivity().supportFragmentManager

            //Get User ID from shared Pref
            sharedPref = requireContext().getSharedPreferences(
                R.string.app_name.toString(),
                Context.MODE_PRIVATE
            )
            userId = sharedPref.getInt("id", 0)


            //Prepare Date Format for dates
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            dateTimeBegin = simpleDateFormat.format(d.time).toString()
            dateTimeEnd = simpleDateFormat.format(d.time).toString()

        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Override Bottom back button
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        //If Fragment is Added and activity isn't null
        if (isAdded && activity != null) {
            //Init drawerlayout
            val mDrawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)

            //Top Bar open with left animation
            topAppBar.setNavigationOnClickListener {
                mDrawerLayout.openDrawer(Gravity.LEFT)
            }

            //Make bottom nav visible
            requireActivity().bottom_nav.visibility = View.VISIBLE


            //Correct top day,week,month filter
            correctFilters()


            //Calculate Top Date After switch day or week or month
            setTopDate()


            //>Day filter clicked
            binding.dayfiltercard.setOnClickListener {
                daysFilter.dayFilter = 1
                daysFilter.weekFilter = 0
                daysFilter.monthFilter = 0
                correctFilters()
                setTopDate()
                binding.progressIndicator.visibility = View.VISIBLE
                getVisites()
            }
            // >> Week Filter Clicked
            binding.weekfilterward.setOnClickListener {
                daysFilter.dayFilter = 0
                daysFilter.weekFilter = 1
                daysFilter.monthFilter = 0
                correctFilters()
                binding.progressIndicator.visibility = View.VISIBLE
                setTopDate()

            }

            // Month Filetr Clicked
            binding.montherfiltercard.setOnClickListener {
                daysFilter.dayFilter = 0
                daysFilter.weekFilter = 0
                daysFilter.monthFilter = 1
                correctFilters()
                binding.progressIndicator.visibility = View.VISIBLE
                setTopDate()
            }


            //Next date button listener
            binding.nextDate.setOnClickListener {
                nextDate()
            }

            //previous date button clicked
            binding.previousDate.setOnClickListener {
                previousDate()
            }

            //today button listener clicked
            binding.today.setOnClickListener {
                setToday()
            }


        }


        //Swipe down refresh layout
        binding.swiperefreshlayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getVisites()
            swiperefreshlayout.isRefreshing = false
        })

        //Init drawer layout
        val mDrawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)

        //Top Bar with left animation
        topAppBar.setNavigationOnClickListener {
            mDrawerLayout.openDrawer(Gravity.LEFT)
        }

    }


    //Set Visite RecycleView
    private fun setupRecycleView() {

        //If fragment is added
        if (isAdded) {
            adapterTask = SuiviePlanningBlocAdapter(
                this,
                requireActivity(),
                activity as DrawerActivity,
                listaSurveyResponse
            )
            binding.suivieRecycleview.isMotionEventSplittingEnabled = false
            binding.suivieRecycleview.layoutManager = LinearLayoutManager(requireContext())
            binding.suivieRecycleview.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            binding.suivieRecycleview.adapter = adapterTask
            adapterTask.setItems(listaTasks)
        }


    }

    override fun onClickedTask(taskId: Int, distance: String) {

        //Save storeId to sharedPref
        sharedPref =
            requireContext().getSharedPreferences(
                R.string.app_name.toString(),
                Context.MODE_PRIVATE
            )!!
        with(sharedPref.edit()) {
            this?.putInt("storeId", taskId)
        }?.commit()
    }


    //Get Visits Service
    @DelicateCoroutinesApi
    private fun getVisites() {

        //Launch Coroutine
        lifecycleScope.launch(Dispatchers.Main) {

            //Save ResponseData of get Visits
            responseData = viewModel.getVisites(userId.toString(), dateTimeBegin, dateTimeEnd)

            //If response is good
            if (responseData.responseCode == 200) {

                //get List of Visit from response
                listaTasks = responseData.data!!.data as ArrayList<Visite>


                //Filter List with day , week , month
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


                if (daysFilter.weekFilter == 1 || daysFilter.monthFilter == 1) {
                    Collections.sort(listaTasks, SortByDate())
                }

                //If fragment is added and activity isn't null we get all responses of every quiz inside store
                if (isAdded && activity != null)
                    getSurveyResponses()

            }

        }
    }


    //Get survey response of every quiz inside store .. why ? ask backend developper
    @DelicateCoroutinesApi
    private fun getSurveyResponses() {
        GlobalScope.launch(Dispatchers.Main) {
            //Get response service
            responseData2 =
                viewModel.getSurveyResponse(userId.toString(), dateTimeBegin, dateTimeEnd)

            //If response is good
            if (responseData2.responseCode == 200) {
                listaSurveyResponse = responseData2.data!!.data as ArrayList<DataX>
                setupRecycleView()
                binding.progressIndicator.visibility = View.GONE

            }

        }
    }

    //test if Permission GPS is granted
    fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    //Ask for permission for gps
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
                binding.progressIndicator.visibility = View.VISIBLE
                //Get All Visites Then filter them with all responses
                getVisites()

            } else {
                showPermissionDeniedGPS()
            }
        }
        return true
    }


    //SHow Permission Gps dialog
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


    //Show Permission Dienied dialog
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
                    if (CheckGpsStatus()) {
                        binding.progressIndicator.visibility = View.VISIBLE
                        getVisites()

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


    //Check Gps status if activated or not
    fun CheckGpsStatus(): Boolean {
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return GpsStatus
    }


    //Sort dates by day
    internal class SortByDate : Comparator<Visite?> {
        override fun compare(p0: Visite?, p1: Visite?): Int {
            return p0!!.day.compareTo(p1!!.day)
        }
    }


    //Compare between two Dates
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


    //Compare Between two months
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


    //Correct top day,week,month filter
    private fun correctFilters() {
        setFilters(binding.dayfiltercard, binding.dayfiltertext, daysFilter.dayFilter)
        setFilters(binding.weekfilterward, binding.weektextfilter, daysFilter.weekFilter)
        setFilters(binding.montherfiltercard, binding.monthfiltertext, daysFilter.monthFilter)
    }


    //Set top filter color , texts , titles ....
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


    //Calculate Top Date After switch day or week or month
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

            binding.progressIndicator.visibility = View.VISIBLE
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

            binding.progressIndicator.visibility = View.VISIBLE
            getVisites()
        }
    }


    //Next date button
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

    //Previous date button function
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


    //Today button function
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


    //Get curretn day of the week
    private fun dayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        calendar.time = d
        val day = calendar[Calendar.DAY_OF_WEEK]

        return if (day in 2..7)
            day - 2
        else
            6
    }


}