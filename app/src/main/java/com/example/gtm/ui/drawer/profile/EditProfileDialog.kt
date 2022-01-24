package com.example.gtm.ui.drawer.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.gtm.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditProfileDialog : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.corned_white_purple)
        return inflater.inflate(R.layout.dialog_edit_profile,container,false)
    }

    override fun onStart() {
        super.onStart()

        val width =  (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.6).toInt()

        dialog!!.window?.setLayout(width,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog!!.window!!.setWindowAnimations(R.style.AnimationsForDialog)
    }
}