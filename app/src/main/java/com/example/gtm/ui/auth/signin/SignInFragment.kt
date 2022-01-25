package com.example.gtm.ui.auth.signin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import com.example.gtm.R
import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.entities.response.SignInResponse
import com.example.gtm.databinding.FragmentSignInBinding
import com.example.gtm.ui.drawer.DrawerActivity
import com.example.gtm.ui.home.HomeActivity
import com.example.gtm.utils.extensions.trimStringEditText
import com.example.gtm.utils.resources.Resource
import com.example.gtm.utils.token.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    private val viewModel: SignInFragmentViewModel by viewModels()
    lateinit var responseData: Resource<SignInResponse>
    lateinit var sharedPref: SharedPreferences
    private lateinit var sessionManager: SessionManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        sessionManager = SessionManager(requireContext())

        initSignIn()

        val animationLeftToRight =
            AnimationUtils.loadAnimation(requireContext(), R.anim.left_to_right)

        val animationRightToLeft =
            AnimationUtils.loadAnimation(requireContext(), R.anim.right_to_left)

        val animationBottomToTop =
            AnimationUtils.loadAnimation(requireContext(), R.anim.bottom_to_top)

        animationSignin(animationLeftToRight, animationRightToLeft, animationBottomToTop)



        binding.signinButton.setOnClickListener {
            // signInOffline()
            signIn()
        }

        return binding.root
    }


    private fun signInOffline() {
        val intent = Intent(activity, DrawerActivity::class.java)
        activity?.startActivity(intent)
        activity?.finish()
    }

    @DelicateCoroutinesApi
    private fun signIn() {

        if (binding.username.editText!!.trimStringEditText().isEmpty()) {
            clearError()
            binding.username.error = "Username is Empty"
        } else if (binding.password.editText!!.trimStringEditText().isEmpty()) {
            clearError()
            binding.password.error = "Password is Empty"
        } else {

            binding.signinButton.isEnabled = false
            binding.progressIndicator.visibility = View.VISIBLE


            val sinInObject = SignInPost(
                binding.username.editText!!.trimStringEditText(),
                binding.password.editText!!.trimStringEditText()
            )

            GlobalScope.launch(Dispatchers.Main) {
                responseData = viewModel.login(sinInObject)
                if (responseData.responseCode == 200) {
                    rememberMe()
                     sessionManager.saveToken(responseData.data!!.token)
                    val intent = Intent(activity, DrawerActivity::class.java)
                    activity?.startActivity(intent)
                    activity?.finish()
                } else {
                    binding.signinButton.isEnabled = true
                    binding.progressIndicator.visibility = View.INVISIBLE
                    clearError()
                    binding.password.error = "Username or Password is Incorrect"
                }
            }
        }
    }

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

    private fun clearError() {
        binding.username.error = null
        binding.password.error = null
    }

    private fun rememberMe() {
        if (binding.remembermebox.isChecked) {
            sharedPref =
                context?.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE)!!
            with(sharedPref.edit()) {
                this?.putString("username", binding.username.editText!!.trimStringEditText())
                this?.putString("passwordNC", binding.password.editText!!.trimStringEditText())
            }?.commit()

        } else {
            sharedPref =
                context?.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE)!!
            with(sharedPref.edit()) {
                this?.putString("username", null)
                this?.putString("password", null)

            }?.commit()
        }

    }


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
}