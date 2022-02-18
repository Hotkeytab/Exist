package com.example.gtm.ui.home.mytask.survey.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.gtm.R
import dagger.hilt.android.AndroidEntryPoint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.data.entities.ui.Image
import kotlinx.android.synthetic.main.dialog_afficher_image.*


@AndroidEntryPoint
class TransparentDialog(


) :
    DialogFragment() {




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_progress_upload, container, false)
    }

    override fun onStart() {
        super.onStart()

        val width = (resources.displayMetrics.widthPixels).toInt()
        val height = (resources.displayMetrics.heightPixels).toInt()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window?.setLayout(width, height)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}



