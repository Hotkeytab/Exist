package com.example.gtm.ui.home.suivie

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
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.BuildConfig
import com.example.gtm.data.entities.remote.VisitPost
import com.example.gtm.data.entities.response.GetStore
import com.example.gtm.data.entities.response.SuccessResponse
import com.example.gtm.data.entities.response.Visite
import com.example.gtm.data.entities.ui.Image
import com.example.gtm.ui.home.mytask.TaskAdapter
import com.example.gtm.ui.home.mytask.TaskFragment
import com.example.gtm.ui.home.mytask.survey.SurveyCheckDialog
import com.example.gtm.ui.home.mytask.survey.quiz.MyQuizViewModel
import com.example.gtm.utils.resources.Resource
import kotlinx.android.synthetic.main.dialog_choix_image.*
import kotlinx.android.synthetic.main.dialog_choix_visitee.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class ChoixImageDialogSuivie(
    listener2: TaskFragment,
    pe1: Int,
    ps1: Int,
    navController1: NavController,
    visite1: Visite,
    view1: View,
    adapterTask1: TaskAdapter,
    listatasks1: ArrayList<Visite>
) :
    DialogFragment() {


    val pe = pe1
    val ps = ps1
    val navController = navController1
    var visite = visite1
    var ourview = view1
    val adapterTask = adapterTask1
    val listaTasks = listatasks1
    var loading = 0
    var listener = listener2
    private var userId = 0
    lateinit var sharedPref: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_choix_visitee, container, false)
    }

    override fun onStart() {
        super.onStart()

        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.6).toInt()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        userId = sharedPref.getInt("id", 0)

        initImages()

        questionnaire.setOnClickListener {


            dismiss()
            SurveyCheckDialog(
                listener,
                navController,
                3,
                requireView(),
                adapterTask,
                listaTasks,
                visite
            ).show(
                requireActivity().supportFragmentManager,
                "SurveyDialog"
            )
        }


        p_entre.setOnClickListener {


              dismiss()
              SurveyCheckDialog(listener,navController, 1, ourview, adapterTask, listaTasks, visite).show(
                  requireActivity().supportFragmentManager,
                  "SurveyDialog"
              )

              progress_indicator_choix.visibility = View.VISIBLE
        }

        p_sortie.setOnClickListener {
            dismiss()
            SurveyCheckDialog(listener,navController, 2, ourview, adapterTask, listaTasks, visite).show(
                requireActivity().supportFragmentManager,
                "SurveyDialog"
            )

        }


    }


    private fun initImages() {

        if (visite.start == null) {
            p_entre.visibility = View.VISIBLE
        } else {
            p_entre.visibility = View.GONE
        }

        if (visite.end  == null) {
            p_sortie.visibility = View.VISIBLE
        } else {
            p_sortie.visibility = View.GONE
        }


        if (visite.start == null) {
            questionnaire.visibility = View.GONE
        }

    }



}



