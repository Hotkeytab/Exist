package com.example.gtm.ui.home


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.gtm.R
import com.example.gtm.ui.home.map.MapFragment
import com.example.gtm.ui.home.planning.PlanningFragment
import com.example.gtm.utils.animations.UiAnimations
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }


    //Override onBackPressed function
    override fun onBackPressed() {
    }


}