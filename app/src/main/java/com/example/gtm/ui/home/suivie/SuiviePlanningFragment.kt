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
    private lateinit var responseData2 : Resource<SurveyResponse>
    private val viewModel: MyTaskViewModel by viewModels()
    private var userId = 0
    private lateinit var dateTimeBegin: String
    private lateinit var dateTimeEnd: String
    private lateinit var fm: FragmentManager
    private lateinit var locationManager: LocationManager
    private val REQUEST_CODE = 2
    private var GpsStatus = false
    private lateinit var navController: NavController
    private lateinit var d: Date


    override fun onStart() {
        super.onStart()

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



        if (isAdded && activity != null) {

            Log.i("repeat", "0")
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


        //    navController = NavHostFragment.findNavController(this)

            correctFilters()


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


        }

        binding.swiperefreshlayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {

            getVisites()
            swiperefreshlayout.isRefreshing = false
        })

        val mDrawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        //Top Bar
        topAppBar.setNavigationOnClickListener {
            mDrawerLayout.openDrawer(Gravity.LEFT)
        }

    }


    private fun setupRecycleViewPredictionDetail() {


        Log.i("repeat", "1")
        if(isAdded) {
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
            // (activity as DrawerActivity).listOfTriDates = ArrayList<String>()
            adapterTask.setItems(listaTasks)
        }


    }

    override fun onClickedTask(taskId: Int, distance: String) {

     //   askForPermissionsDialog()
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


            Log.i("repeat", "1")

            responseData = viewModel.getVisites(userId.toString(), dateTimeBegin, dateTimeEnd)


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


                if (daysFilter.weekFilter == 1 || daysFilter.monthFilter == 1) {
                    Collections.sort(listaTasks, SortByDate())
                    //  transformListToHashMapDate()
                }

                if (isAdded && activity != null)
                    getSurveyResponses()







            }

        }
    }


    @DelicateCoroutinesApi
    private fun getSurveyResponses() {
        GlobalScope.launch(Dispatchers.Main) {


            responseData2 = viewModel.getSurveyResponse(userId.toString(), dateTimeBegin, dateTimeEnd)


            if (responseData2.responseCode == 200) {
              listaSurveyResponse = responseData2.data!!.data as ArrayList<DataX>
                setupRecycleViewPredictionDetail()
                /*    if (daysFilter.dayFilter == 1)
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
                      //  transformListToHashMapDate()
                  }

                  if (isAdded && activity != null)
                      setupRecycleViewPredictionDetail()


                  */


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
            {
                binding.progressIndicator.visibility = View.VISIBLE
                getVisites()

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
              //  SurveyCheckDialog(navController,3,requireView(),adapterTask,listaTasks).show(fm, "SurveyDialog")
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


}