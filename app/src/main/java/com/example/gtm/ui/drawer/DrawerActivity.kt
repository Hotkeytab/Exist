package com.example.gtm.ui.drawer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.gtm.R
import com.example.gtm.data.entities.response.SignInResponse
import com.example.gtm.data.entities.response.UserResponse
import com.example.gtm.ui.auth.AuthActivity
import com.example.gtm.ui.drawer.profile.EditProfileDialog
import com.example.gtm.ui.home.mytask.TaskFragment
import com.example.gtm.ui.home.planning.PlanningFragment
import com.example.gtm.utils.animations.UiAnimations
import com.example.gtm.utils.extensions.trimStringEditText
import com.example.gtm.utils.resources.Resource
import com.example.gtm.utils.token.SessionManager
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val uiAnimations = UiAnimations(this)
    private lateinit var fm: FragmentManager
    private val viewModel: DrawerActivityViewModel by viewModels()
    lateinit var sharedPref: SharedPreferences
    lateinit var responseData: Resource<UserResponse>
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)



        sessionManager = SessionManager(this)

        fm = this.supportFragmentManager

        val mDrawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val mNavigationView = findViewById<NavigationView>(R.id.nav_view)


        mNavigationView.setNavigationItemSelectedListener(this)


        getProfile()

        //Hide Nav Bar
        // uiAnimations.hideNavBar()

        //Default Fragment
        supportFragmentManager.beginTransaction().replace(
            R.id.nav_acceuil_fragment,
            TaskFragment()
        ).commit()


        //Top Bar
        topAppBar.setNavigationOnClickListener {
            mDrawerLayout.openDrawer(Gravity.LEFT)
        }


        //Bottom Nav Bar Listener
        bottom_nav.setOnItemSelectedListener {
            var selectedFragment: Fragment? = null
            when (it.itemId) {

                R.id.task -> {
                    selectedFragment = TaskFragment()
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
        when(item.itemId)
        {
            R.id.task -> {
                print("1")
            }
            R.id.nav_profile -> {
                EditProfileDialog().show(fm,"EditProfileFRagment")
            }
            R.id.nav_logout -> {
                val intent = Intent(this, AuthActivity::class.java)
                this.startActivity(intent)
                this.finish()
            }
        }
        return true
    }


    @DelicateCoroutinesApi
    private fun getProfile() {

        sharedPref = this.getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        val username = sharedPref.getString("username", "")
        val tokenAfter = sharedPref.getString("token", "")




        GlobalScope.launch(Dispatchers.Main) {
            responseData = viewModel.getUser("test.t")
            if (responseData.responseCode == 200) {
                saveUser(responseData.data!!.data.first_name,responseData.data!!.data.last_name,responseData.data!!.data.email,responseData.data!!.data.phone_number,responseData.data!!.data.profile_picture,responseData.data!!.data.id,responseData.data!!.data.enabled,responseData.data!!.data.gender,responseData.data!!.data.roleId)
            } else {
                Log.i("User", "${responseData}")
            }
        }
    }

    private fun saveUser(
        firstname: String,
        lastname: String,
        email: String,
        phone: String,
        picture: String,
        id: Int,
        enabled:Boolean,
        gender:String,
        roleId:Int
    ) {

        sharedPref =
            this.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE)!!
        with(sharedPref.edit()) {
            this?.putString("firstname", firstname)
            this?.putString("lastname",lastname)
            this?.putString("email",email)
            this?.putString("phone",phone)
            this?.putString("picture",picture)
            this?.putString("id",id.toString())
            this?.putString("enabled",enabled.toString())
            this?.putString("gender",gender)
            this?.putString("roleId",roleId.toString())
        }?.commit()

        name_lastname.text = "$firstname $lastname"
        email_profile.text = email
        number_profile.text = phone

        Glide.with(this)
            .load(picture)
            .transform(CircleCrop())
            .into(profile_picture)
    }






}