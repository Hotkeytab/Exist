package com.example.gtm.ui.drawer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.gtm.R
import com.example.gtm.ui.home.map.MapFragment
import com.example.gtm.ui.home.mytask.TaskFragment
import com.example.gtm.ui.home.planning.PlanningFragment
import com.example.gtm.utils.animations.UiAnimations
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*


@AndroidEntryPoint
class DrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val uiAnimations = UiAnimations(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)


        val mDrawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val mNavigationView = findViewById<NavigationView>(R.id.nav_view)


        mNavigationView.setNavigationItemSelectedListener(this)


        //Hide Nav Bar
        // uiAnimations.hideNavBar()

        //Default Fragment
        supportFragmentManager.beginTransaction().replace(
            R.id.nav_acceuil_fragment,
            TaskFragment()
        ).commit()


        //Top Bar
        topAppBar.setNavigationOnClickListener {
            Log.i("Drawer","Drawer")
            mDrawerLayout.openDrawer(Gravity.LEFT)
        }


        //Bottom Nav Bar Listener
        bottom_nav.setOnItemSelectedListener {
            var selectedFragment: Fragment? = null
            when (it.itemId) {

                R.id.task -> {
                    selectedFragment = TaskFragment()
                }

                R.id.calendar -> {
                    selectedFragment = PlanningFragment()
                }
                R.id.map -> {
                    selectedFragment = MapFragment()
                }
            }

            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction().replace(
                    R.id.nav_acceuil_fragment,
                    selectedFragment
                ).commit()
            }
            true
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }
}