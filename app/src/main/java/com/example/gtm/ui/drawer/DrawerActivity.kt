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
import com.example.gtm.data.entities.ui.User
import com.example.gtm.ui.auth.AuthActivity
import com.example.gtm.ui.drawer.profile.EditProfileDialog
import com.example.gtm.ui.home.mytask.TaskFragment
import com.example.gtm.utils.animations.UiAnimations
import com.example.gtm.utils.token.SessionManager
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.nav_header_main.*


@AndroidEntryPoint
class DrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val uiAnimations = UiAnimations(this)
    private lateinit var fm: FragmentManager
    private val viewModel: DrawerActivityViewModel by viewModels()
    lateinit var sharedPref: SharedPreferences
    private lateinit var sessionManager: SessionManager
    private lateinit var user: User
    private lateinit var picture: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)



        sessionManager = SessionManager(this)

        fm = this.supportFragmentManager

        val mDrawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val mNavigationView = findViewById<NavigationView>(R.id.nav_view)


        mNavigationView.setNavigationItemSelectedListener(this)


        //Hide Nav Bar
        // uiAnimations.hideNavBar()


        saveUser()

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
        when (item.itemId) {
            R.id.task -> {
                print("1")
            }
            R.id.nav_profile -> {
                user.first_name = firstname_only.text.toString()
                user.last_name = lastname_only.text.toString()
                user.email = email_profile.text.toString()
                user.phone_number = number_profile.text.toString()



                EditProfileDialog(
                    user,
                    picture,
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
                val intent = Intent(this, AuthActivity::class.java)
                this.startActivity(intent)
                this.finish()
            }
        }
        return true
    }


    private fun saveUser(

    ) {


        sharedPref = getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )
        val id = sharedPref.getInt("id", 0)
        val firstname = sharedPref.getString("firstname", "")
        val lastname = sharedPref.getString("lastname", "")
        val email = sharedPref.getString("email", "")
        val password = sharedPref.getString("password", "")
        val phone = sharedPref.getString("phone", "")
        //  val enabled = sharedPref.getString("enabled", "")
        val gender = sharedPref.getString("gender", "")
        val roleId = sharedPref.getInt("roleId", 0)
        picture = sharedPref.getString("picture", "")!!

        user = User(
            id,
            firstname!!,
            lastname!!,
            email!!,
            password!!,
            phone!!,
            true,
            gender!!,
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


}