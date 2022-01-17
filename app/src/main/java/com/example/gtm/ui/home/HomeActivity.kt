package com.example.gtm.ui.home


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gtm.R
import com.example.gtm.ui.home.map.MapFragment
import com.example.gtm.utils.animations.UiAnimations
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val uiAnimations = UiAnimations(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        supportFragmentManager.beginTransaction().replace(
            R.id.nav_acceuil_fragment,
            MapFragment()
        ).commit()


    }


}