package com.example.gtm.ui.drawer

import androidx.lifecycle.ViewModel
import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.repository.AuthRepository
import com.example.gtm.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DrawerActivityViewModel  @Inject constructor(
    private val userRepository: UserRepository
):ViewModel(){
    suspend fun getUser(username: String) = userRepository.getUser(username)
}