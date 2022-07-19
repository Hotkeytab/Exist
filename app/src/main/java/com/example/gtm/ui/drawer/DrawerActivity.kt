package com.example.gtm.ui.drawer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.gtm.R
import com.example.gtm.data.entities.custom.QuestionNewPost
import com.example.gtm.data.entities.custom.UserInf
import com.example.gtm.data.entities.remote.ImagePath
import com.example.gtm.data.entities.response.login.UserResponse
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.compterendu.PostSubjectCompteRendu
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.compterendu.SubjectCompteRendu
import com.example.gtm.data.entities.response.mytaskplanning.getvisite.Visite
import com.example.gtm.data.entities.ui.Survey
import com.example.gtm.data.entities.ui.User
import com.example.gtm.ui.auth.AuthActivity
import com.example.gtm.ui.drawer.profile.EditProfileDialog
import com.example.gtm.ui.home.kpi.KpiGraphFragment
import com.example.gtm.ui.home.mytask.BeforeHomeFragment
import com.example.gtm.ui.home.suivie.SuiviePlanningFragment
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

    private lateinit var fm: FragmentManager
    private val viewModel: DrawerActivityViewModel by viewModels()
    lateinit var sharedPref: SharedPreferences
    lateinit var responseDataUser: Resource<UserResponse>
    private lateinit var sessionManager: SessionManager
    private lateinit var user: User
    private var picture: String? = null

    //List Of Questions per sub category
    var listOfQuestionsPerSc = HashMap<Int, HashMap<Int, Survey?>>()
    var loading = false

    //Hashmap visit per day
    var HashMaplistaTasksDate: HashMap<String, ArrayList<Visite>> =
        HashMap<String, ArrayList<Visite>>()

    //This array is used  to post final response for quiz
    var surveyPostArrayList: HashMap<UserInf, QuestionNewPost> = HashMap<UserInf, QuestionNewPost>()

     var SubjectCompteRendu: ArrayList<PostSubjectCompteRendu>? = ArrayList<PostSubjectCompteRendu>()

    //Current fragment
    var nowFragment = 0

    //Last fragment
    var lastFragment = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        //Init session manager + fragment manager
        sessionManager = SessionManager(this)
        fm = this.supportFragmentManager
        //Init Navigation View
        val mNavigationView = findViewById<NavigationView>(R.id.nav_view)
        mNavigationView.setNavigationItemSelectedListener(this)

        //Get Profile Service
        getProfile()


        //Init the first fragment
        supportFragmentManager.beginTransaction().replace(
            R.id.nav_acceuil_fragment,
            BeforeHomeFragment()
        ).commit()

        //Bottom Nav Bar Listener
        bottom_nav.setOnItemSelectedListener {
            var selectedFragment: Fragment? = null


            when (it.itemId) {

                R.id.task -> {

                    // if (SystemClock.elapsedRealtime() - lastTimeClicked > defaultInterval) {
                    selectedFragment = BeforeHomeFragment()
                    nowFragment = 1

                    //  lastTimeClicked = SystemClock.elapsedRealtime()

                }
                R.id.suivie -> {
                    //   if (SystemClock.elapsedRealtime() - lastTimeClicked > defaultInterval) {
                    selectedFragment = SuiviePlanningFragment()
                    nowFragment = 2
                    //     lastTimeClicked = SystemClock.elapsedRealtime()

                }

                R.id.kpi -> {
                    //   if (SystemClock.elapsedRealtime() - lastTimeClicked > defaultInterval) {
                    selectedFragment = KpiGraphFragment()
                    nowFragment = 3
                    //  selectedFragment = KpiFragment()
                    //  lastTimeClicked = SystemClock.elapsedRealtime()
                }

            }

            if (selectedFragment != null) {

                //we are checking for fragment , if the same fragment is already present we don't load it again
                if (checkForFragment())
                    supportFragmentManager.beginTransaction().replace(
                        R.id.nav_acceuil_fragment,
                        selectedFragment
                    ).commit()
            }
            true
        }

    }


    //Check if selectedFragment is the currentFragment
    private fun checkForFragment(): Boolean {
        if (nowFragment == lastFragment)
            return false
        else {
            lastFragment = nowFragment
            return true
        }
    }


    //Side Nav Bar Menu Item Listener
    override fun onNavigationItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {
            //Go to Profile Section
            R.id.nav_profile -> {
                //Fill Profile Info
                user.first_name = firstname_only.text.toString()
                user.last_name = lastname_only.text.toString()
                user.email = email_profile.text.toString()
                user.phone_number = number_profile.text.toString()

                if (picture == null)
                    picture = ""

                //Show Profile Detail Profile
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
            //Logout Button
            R.id.nav_logout -> {
                //  sharedPref.edit().clear().apply()
                val intent = Intent(this, AuthActivity::class.java)
                this.startActivity(intent)
                this.finish()
            }
        }
        return true
    }


    //Get Profile Service
    @DelicateCoroutinesApi
    private fun getProfile() {

        //Get username from shared Pref
        sharedPref = this.getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        val username = sharedPref.getString("username", "")

        //Get User Couroutine
        GlobalScope.launch(Dispatchers.Main) {
            //getUser Response service
            responseDataUser = viewModel.getUser(username!!)

            //If everything is good we save user
            if (responseDataUser.responseCode == 200) {
                saveUser(
                    responseDataUser.data!!.data.first_name,
                    responseDataUser.data!!.data.last_name,
                    responseDataUser.data!!.data.email,
                    responseDataUser.data!!.data.phone_number,
                    responseDataUser.data!!.data.profile_picture,
                    responseDataUser.data!!.data.id,
                    responseDataUser.data!!.data.enabled,
                    responseDataUser.data!!.data.gender,
                    responseDataUser.data!!.data.roleId,
                    responseDataUser.data!!.data.password
                )
                picture = responseDataUser.data!!.data.profile_picture
            }
        }
    }


    // Save User to SharedPref
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

        //Load User To UI
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

}