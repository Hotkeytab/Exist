package com.example.gtm.ui.home.mytask

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog.show
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtm.R
import com.example.gtm.data.entities.response.DataX
import com.example.gtm.data.entities.response.VisiteResponse
import com.example.gtm.databinding.FragmentTaskBinding
import com.example.gtm.ui.home.mytask.positionmap.PositionMapDialog
import com.example.gtm.ui.home.mytask.survey.SurveyCheckDialog
import com.example.gtm.utils.resources.Resource
import com.fasterxml.jackson.databind.util.ClassUtil.getPackageName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_quiz_confirmation.*
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class TaskFragment : Fragment(), TaskAdapter.TaskItemListener {


    private lateinit var binding: FragmentTaskBinding
    private lateinit var adapterTask: TaskAdapter
    private var listaTasks = ArrayList<DataX>()
    lateinit var sharedPref: SharedPreferences
    private lateinit var responseData: Resource<VisiteResponse>
    private val viewModel: MyTaskViewModel by viewModels()
    private var userId = 0
    private lateinit var dateTime: String
    private lateinit var fm: FragmentManager
    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    private val REQUEST_CODE = 2
    private var GpsStatus = false


    override fun onStart() {
        super.onStart()
        getVisites()
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

    override fun onClickedTask(taskId: Int,latitude:Double,Longitude:Double) {
        // PositionMapDialog().show(fm,"PositionMapDialog")
        askForPermissions(latitude,Longitude)
    }

    @DelicateCoroutinesApi
    private fun getVisites() {
        GlobalScope.launch(Dispatchers.Main) {

            responseData = viewModel.getVisites(userId.toString(), dateTime, dateTime)

            if (responseData.responseCode == 200) {
                listaTasks = responseData.data!!.data as ArrayList<DataX>
                setupRecycleViewPredictionDetail()
            }

        }
    }


    fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun askForPermissions(latitude:Double,Longitude:Double): Boolean {
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
                SurveyCheckDialog(latitude, Longitude).show(fm, "SurveyDialog")
            else {
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
            .setNegativeButton("Cancel", null)
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

                } else {
                    // permission is denied, you can ask for permission again, if you want
                    askForPermissions(0.00,0.00)

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


}

object StaticMapClicked {
    var mapIsRunning = false
}