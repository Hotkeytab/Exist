package com.example.gtm.ui.home.suivie

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.gtm.R
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
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
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.BuildConfig
import com.example.gtm.data.entities.response.Visite
import com.example.gtm.data.entities.ui.Image
import com.example.gtm.ui.home.mytask.survey.SurveyCheckDialog
import kotlinx.android.synthetic.main.dialog_choix_image.*
import kotlinx.android.synthetic.main.dialog_choix_visitee.*
import java.io.File


@AndroidEntryPoint
class ChoixImageDialogSuivie(
    pe1: Int,
    ps1: Int,
    navController1: NavController,
    visite1 : Visite,
    view1 : View
) :
    DialogFragment() {

    val pe = pe1
    val ps = ps1
    val navController = navController1
    var visite = visite1
    var ourview = view1


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


        initImages()

        questionnaire.setOnClickListener {
            dismiss()
            SurveyCheckDialog(navController,3,requireView()).show(
                requireActivity().supportFragmentManager,
                "SurveyDialog"
            )
        }


        p_entre.setOnClickListener {
            dismiss()
            SurveyCheckDialog(navController,1,ourview).show(
                requireActivity().supportFragmentManager,
                "SurveyDialog"
            )
            visite.pe = 1
        }

        p_sortie.setOnClickListener {
            dismiss()
            SurveyCheckDialog(navController,2,ourview).show(
                requireActivity().supportFragmentManager,
                "SurveyDialog"
            )
            visite.ps = 1
        }


    }


    private fun initImages() {

        if (pe == 0) {
            p_entre.visibility = View.VISIBLE
        } else {
            p_entre.visibility = View.GONE
        }

        if (ps == 0) {
            p_sortie.visibility = View.VISIBLE
        } else {
            p_sortie.visibility = View.GONE
        }

    }

}



