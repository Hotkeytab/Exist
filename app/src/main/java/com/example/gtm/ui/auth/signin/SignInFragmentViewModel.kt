package com.example.gtm.ui.auth.signin

import androidx.lifecycle.ViewModel
import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.repository.AuthRepository
import com.example.gtm.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SignInFragmentViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository

) : ViewModel() {
    suspend fun login(signinObject: SignInPost) = authRepository.login(signinObject)
    suspend fun getUser(username: String) = userRepository.getUser(username)
}