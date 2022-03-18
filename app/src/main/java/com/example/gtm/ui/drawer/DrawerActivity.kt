package com.example.gtm.ui.drawer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.gtm.R
import com.example.gtm.data.entities.remote.QuestionPost
import com.example.gtm.data.entities.response.UserResponse
import com.example.gtm.data.entities.response.Visite
import com.example.gtm.data.entities.ui.Survey
import com.example.gtm.data.entities.ui.User
import com.example.gtm.ui.auth.AuthActivity
import com.example.gtm.ui.drawer.profile.EditProfileDialog
import com.example.gtm.ui.home.HomeActivity
import com.example.gtm.ui.home.kpi.KpiFragment
import com.example.gtm.ui.home.mytask.BeforeHomeFragment
import com.example.gtm.ui.home.suivie.SuiviePlanningFragment
import com.example.gtm.utils.animations.UiAnimations
import com.example.gtm.utils.resources.Resource
import com.example.gtm.utils.token.SessionManager
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_suivie_detail.*
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.android.synthetic.main.fragment_task.progress_indicator
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
    private lateinit var user: User
    private var picture: String? = null
    private var defaultInterval: Int = 500
    private var lastTimeClicked: Long = 0
    var listOfQuestionsPerSc = HashMap<Int, HashMap<Int, Survey?>>()
    var envoyerTest = true
    var loading = false
    var listOfTriDates: ArrayList<String> = ArrayList<String>()
    var HashMaplistaTasksDate: HashMap<String, ArrayList<Visite>> =
        HashMap<String, ArrayList<Visite>>()
    var SetlistaTasksDate: Set<Map.Entry<String, ArrayList<Visite>>>? = null


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


        supportFragmentManager.beginTransaction().replace(
            R.id.nav_acceuil_fragment,
            BeforeHomeFragment()
        ).commit()

        //Bottom Nav Bar Listener
        bottom_nav.setOnItemSelectedListener {
            var selectedFragment: Fragment? = null


            if (progress_indicator.visibility == View.GONE) {
                when (it.itemId) {


                    R.id.task -> {

                        if (SystemClock.elapsedRealtime() - lastTimeClicked > defaultInterval) {
                            selectedFragment = BeforeHomeFragment()

                        }

                        lastTimeClicked = SystemClock.elapsedRealtime()
                    }
                    R.id.suivie -> {
                        if (SystemClock.elapsedRealtime() - lastTimeClicked > defaultInterval) {
                            selectedFragment = SuiviePlanningFragment()
                        }
                        lastTimeClicked = SystemClock.elapsedRealtime()
                    }

                    R.id.kpi -> {
                        if (SystemClock.elapsedRealtime() - lastTimeClicked > defaultInterval) {
                            selectedFragment = KpiFragment()
                        }
                        lastTimeClicked = SystemClock.elapsedRealtime()
                    }

                }

                if (selectedFragment != null) {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.nav_acceuil_fragment,
                        selectedFragment
                    ).commit()
                }
            }
                true

            }


    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {
            R.id.nav_profile -> {
                user.first_name = firstname_only.text.toString()
                user.last_name = lastname_only.text.toString()
                user.email = email_profile.text.toString()
                user.phone_number = number_profile.text.toString()

                if (picture == null)
                    picture = ""

                EditProfileDialog(
                    user,
                    picture!!,
                    viewModel,
                    name_lastname,
                    email_profile,
                    number_profile,
                    lastname_only,
                    firstname_only,
                    profile_picture
                ).show(fm, "EditProfileFRagment")
            }
            R.id.nav_logout -> {
              //  sharedPref.edit().clear().apply()
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



        GlobalScope.launch(Dispatchers.Main) {
            responseData = viewModel.getUser(username!!)
            if (responseData.responseCode == 200) {
                saveUser(
                    responseData.data!!.data.first_name,
                    responseData.data!!.data.last_name,
                    responseData.data!!.data.email,
                    responseData.data!!.data.phone_number,
                    responseData.data!!.data.profile_picture,
                    responseData.data!!.data.id,
                    responseData.data!!.data.enabled,
                    responseData.data!!.data.gender,
                    responseData.data!!.data.roleId,
                    responseData.data!!.data.password
                )
                picture = responseData.data!!.data.profile_picture
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
        enabled: Boolean,
        gender: String,
        roleId: Int,
        password: String
    ) {

        sharedPref =
            this.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE)!!
        with(sharedPref.edit()) {
            this?.putString("firstname", firstname)
            this?.putString("lastname", lastname)
            this?.putString("email", email)
            this?.putString("phone", phone)
            this?.putString("picture", picture)
            this?.putInt("id", id)
            this?.putString("enabled", enabled.toString())
            this?.putString("gender", gender)
            this?.putString("roleId", roleId.toString())
            this?.putString("password", password)
        }?.commit()

        user = User(
            id,
            firstname,
            lastname,
            email,
            password,
            phone,
            enabled.toString(),
            gender,
            roleId
        )

        Log.i("userfinal", "$user")

        firstname_only.text = firstname
        lastname_only.text = lastname
        name_lastname.text = "$firstname $lastname"
        email_profile.text = email
        number_profile.text = phone

        Glide.with(this)
            .load(picture)
            .transform(CircleCrop())
            .into(profile_picture)
    }


    override fun onDestroy() {
        super.onDestroy()
        //  sharedPref.edit().clear().apply()
    }


    object trackState {
        var lastOne = ""
        var currentOne = ""

    }

}