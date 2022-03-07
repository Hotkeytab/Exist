package com.example.gtm.ui.home.mytask.addvisite

import androidx.lifecycle.ViewModel
import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.repository.AuthRepository
import com.example.gtm.data.repository.StoreRepository
import com.example.gtm.data.repository.TimeRepository
import com.example.gtm.data.repository.VisiteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AddVisiteDialogViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {
    suspend fun getStores() = storeRepository.getStores()
}