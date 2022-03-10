package com.example.gtm.ui.home.mytask.addvisite

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.gtm.R
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.BuildConfig
import com.example.gtm.data.entities.remote.VisitPost
import com.example.gtm.data.entities.response.*
import com.example.gtm.data.entities.ui.Image
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.mytask.MyTaskViewModel
import com.example.gtm.ui.home.mytask.TaskAdapter
import com.example.gtm.ui.home.mytask.survey.SurveyCheckDialog
import com.example.gtm.ui.home.mytask.survey.quiz.MyQuizViewModel
import com.example.gtm.utils.resources.Resource
import kotlinx.android.synthetic.main.dialog_add_visite.*
import kotlinx.android.synthetic.main.dialog_choix_image.*
import kotlinx.android.synthetic.main.dialog_choix_visitee.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.content.DialogInterface
import androidx.fragment.app.Fragment


@AndroidEntryPoint
class AddVisteDialog(


) :
    DialogFragment(), AddVisiteAdapter.TaskItemListener {

    private val viewModel: AddVisiteDialogViewModel by viewModels()
    private val viewModelQuiz: MyQuizViewModel by viewModels()
    private lateinit var responseDataStores: Resource<GetStore>
    private lateinit var responseAdd: Resource<SuccessResponse>
    private lateinit var adapterAddVisite: AddVisiteAdapter
    private var listaDataXX = ArrayList<DataXX>()
    private var userId = 0
    lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_visite, container, false)
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.65).toInt()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //  dialog!!.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog!!.window?.setLayout(width, height)

        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        userId = sharedPref.getInt("id", 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getStores()



        cancel.setOnClickListener {
            dialog!!.dismiss()
        }

    }

    override fun onClickedTask(taskId: Int) {

        dialog!!.setCancelable(false)
        cancel.isEnabled = false
        progress_indicator.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.Main) {
            val visitePost = VisitPost(getDateNow(), 0, taskId, userId,false)
            val arayListViste = ArrayList<VisitPost>()
            arayListViste.add(visitePost)
            responseAdd = viewModelQuiz.addVisite(arayListViste) as Resource<SuccessResponse>


            if (responseAdd.responseCode == 201) {
                dialog!!.setCancelable(true)
                cancel.isEnabled = true
                dialog!!.dismiss()

            } else {
                dialog!!.setCancelable(true)
                cancel.isEnabled = true
                progress_indicator.visibility = View.GONE
            }
            Log.i("ourresponse", "$responseAdd")
        }
    }

    private fun getDateNow(): String {

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val d = Date()
        return sdf.format(d)

    }


    private fun getStores() {
        progress_indicator.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.Main) {


            responseDataStores = viewModel.getStores()

            if (responseDataStores.responseCode == 200) {
                progress_indicator.visibility = View.GONE
                listaDataXX = responseDataStores.data!!.data as ArrayList<DataXX>
                listaDataXX.add(responseDataStores.data!!.data[0])
                listaDataXX.add(responseDataStores.data!!.data[0])
                listaDataXX.add(responseDataStores.data!!.data[0])
                listaDataXX.add(responseDataStores.data!!.data[0])

                setupRecycleViewPredictionDetail()
            } else {
                if (progress_indicator != null)
                    progress_indicator.visibility = View.GONE
            }

        }
    }


    private fun setupRecycleViewPredictionDetail() {

        adapterAddVisite = AddVisiteAdapter(this)
        getstorerecycle.isMotionEventSplittingEnabled = false
        getstorerecycle.layoutManager = LinearLayoutManager(requireContext())
        getstorerecycle.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        getstorerecycle.adapter = adapterAddVisite
        adapterAddVisite.setItems(listaDataXX)
        checkForResearch()

    }

    private fun checkForResearch() {
        if(listaDataXX.size > 3)
            search_bar.visibility = View.VISIBLE
    }


}



