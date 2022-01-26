package com.example.gtm.ui.home.mytask

import androidx.lifecycle.ViewModel
import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.repository.AuthRepository
import com.example.gtm.data.repository.VisiteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MyTaskViewModel  @Inject constructor(
    private val visiteRepository: VisiteRepository
):ViewModel(){

    suspend fun getVisites(user_id: String, date_begin: String, date_end: String) =
        visiteRepository.getVisites(user_id, date_begin, date_end)
}