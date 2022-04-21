package com.example.gtm.ui.auth.signin

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.example.gtm.R
import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.entities.response.SignInResponse
import com.example.gtm.data.entities.response.UserResponse
import com.example.gtm.databinding.FragmentSignInBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.utils.extensions.trimStringEditText
import com.example.gtm.utils.remote.Internet.InternetCheck
import com.example.gtm.utils.remote.Internet.InternetCheckDialog
import com.example.gtm.utils.resources.Resource
import com.example.gtm.utils.token.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SignInFragment : Fragment(), DialogInterface.OnDismissListener {

    private lateinit var binding: FragmentSignInBinding
    private val viewModel: SignInFragmentViewModel by viewModels()
    private lateinit var responseDataSignIn: Resource<SignInResponse>
    private var responseDataUser: Resource<UserResponse>? = null
    lateinit var sharedPref: SharedPreferences
    private lateinit var sessionManager: SessionManager
    private lateinit var dialog: InternetCheckDialog
    private lateinit var fm: FragmentManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        //If Fragment is Added
        if (isAdded) {

            //Init session manager , fragment manager and Internet Dialog
            sessionManager = SessionManager(requireContext())
            fm = requireActivity().supportFragmentManager
            dialog = InternetCheckDialog()

            //Fill SignIn Edittext with sharedPref
            initSignIn()


            //Init All Edittext Animations
            //Left to right animation
            val animationLeftToRight =
                AnimationUtils.loadAnimation(requireContext(), R.anim.left_to_right)
            //Right to left animation
            val animationRightToLeft =
                AnimationUtils.loadAnimation(requireContext(), R.anim.right_to_left)
            //Bottom to top animation
            val animationBottomToTop =
                AnimationUtils.loadAnimation(requireContext(), R.anim.bottom_to_top)

            //Start Animation
            animationSignin(animationLeftToRight, animationRightToLeft, animationBottomToTop)


            //SignIn Button Click Listener
            //We check Internet , if everything is good then we login
            binding.signinButton.setOnClickListener {
                //Disable signin button until we get a response from webservice
                binding.signinButton.isEnabled = false
                checkInternet()
            }


        }

        return binding.root
    }


    //SignIn function
    @DelicateCoroutinesApi
    private fun signIn() {

        //If Username is empty
        if (binding.username.editText!!.trimStringEditText().isEmpty()) {
            //Prepare Error
            clearError()
            binding.username.error = "Username is Empty"
            binding.signinButton.isEnabled = true
            //If password is empty
        } else if (binding.password.editText!!.trimStringEditText().isEmpty()) {
            //Prepare Error
            clearError()
            binding.password.error = "Password is Empty"
            binding.signinButton.isEnabled = true
        } else {

            //Activate Progress Indicator
            binding.progressIndicator.visibility = View.VISIBLE


            //Call CheckInternet function
            InternetCheck { internet ->
                //If Internet is Good
                if (internet) {
                    //Create SignIn Object
                    val sinInObject = SignInPost(
                        binding.username.editText!!.trimStringEditText(),
                        binding.password.editText!!.trimStringEditText()
                    )

                    //If fragment is Added
                    if (isAdded) {
                        //Launch Couroutine
                        GlobalScope.launch(Dispatchers.Main) {
                            //Response from SignIn Webservice
                            responseDataSignIn = viewModel.login(sinInObject)
                            //If response is good
                            if (responseDataSignIn.responseCode == 200) {
                                //Save Login Infos to shared pref
                                rememberMe()
                                sessionManager.saveToken(responseDataSignIn.data!!.token)
                                //Fetch DataUser
                                responseDataUser =
                                    viewModel.getUser(binding.username.editText!!.trimStringEditText())

                                //Save User ID to shared pref
                                sharedPref =
                                    requireActivity().getSharedPreferences(
                                        R.string.app_name.toString(),
                                        Context.MODE_PRIVATE
                                    )!!
                                with(sharedPref.edit()) {
                                    this?.putInt("id", responseDataUser!!.data!!.data.id)
                                }?.commit()

                                //Start Intent after everything is good
                                val intent = Intent(activity, DrawerActivity::class.java)
                                activity?.startActivity(intent)
                                activity?.finish()


                                //Password is wrong
                            } else if (responseDataUser != null) {

                                if (responseDataUser!!.responseCode == 401) {
                                    binding.signinButton.isEnabled = true
                                    binding.progressIndicator.visibility = View.INVISIBLE
                                    clearError()
                                    binding.password.error =
                                        "Mot de passe ou nom d'utilisateur Erroné "
                                    //Connection Problems
                                } else {
                                    binding.signinButton.isEnabled = true
                                    binding.progressIndicator.visibility = View.INVISIBLE
                                    clearError()
                                    binding.password.error = "Erreur Connexion "
                                }
                                //Connection Problems
                            } else {
                                binding.signinButton.isEnabled = true
                                binding.progressIndicator.visibility = View.INVISIBLE
                                clearError()
                                binding.password.error = "Erreur Connexion"
                            }
                        }
                    }


                }
            }
        }
    }


    //Animate Signin editexts
    private fun animationSignin(
        leftToRight: Animation,
        rightToLeft: Animation,
        bottomTopTop: Animation
    ) {
        binding.username.animation = leftToRight
        binding.password.animation = rightToLeft
        binding.signinButton.animation = bottomTopTop
        binding.signuplinear.animation = bottomTopTop
    }

    //Clear SignIn errors
    private fun clearError() {
        binding.username.error = null
        binding.password.error = null
    }

    //Remember me function
    private fun rememberMe() {

        //If remember me is checked then put data inside username and password
        if (binding.remembermebox.isChecked) {
            sharedPref =
                context?.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE)!!
            with(sharedPref.edit()) {
                this?.putString("username", binding.username.editText!!.trimStringEditText())
                this?.putString("passwordNC", binding.password.editText!!.trimStringEditText())
            }?.commit()

        } else {
            //Clear shared pref from username and password
            sharedPref =
                context?.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE)!!
            with(sharedPref.edit()) {
                this?.putString("username", null)
                this?.putString("password", null)

            }?.commit()
        }

    }

    //Fill SignIn Editexts with sharedPref
    private fun initSignIn() {
        sharedPref = requireContext().getSharedPreferences(
            R.string.app_name.toString(),
            Context.MODE_PRIVATE
        )

        val username = sharedPref.getString("username", "")
        val password = sharedPref.getString("passwordNC", "")

        if (username != "" && password != "") {
            binding.username.editText?.setText(username)
            binding.password.editText?.setText(password)
            binding.remembermebox.isChecked = true
        }
    }

    //OnDismissDialog (check internet dialog)
    //SignIn Again if dialog is closed
    override fun onDismiss(p0: DialogInterface?) {
        signIn()
    }


    //Check Internet
    private fun checkInternet() {
        InternetCheck { internet ->
            //If Internet Good
            if (internet)
                signIn()

            //If There is no Internet
            else {

                //Progress bar invisible
                binding.progressIndicator.visibility = View.INVISIBLE

                //Show Internet check Dialog
                dialog.show(
                    fm,
                    "Internet check"
                )
                fm.executePendingTransactions()

                //Dialog cancel listener
                dialog.dialog!!.setOnCancelListener {
                    checkInternet()
                }

            }
        }
    }

}

