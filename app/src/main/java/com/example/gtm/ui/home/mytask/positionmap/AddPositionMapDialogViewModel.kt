package com.example.gtm.ui.home.mytask.positionmap

import androidx.lifecycle.ViewModel
import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.repository.AuthRepository
import com.example.gtm.data.repository.StoreRepository
import com.example.gtm.data.repository.TimeRepository
import com.example.gtm.data.repository.VisiteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import retrofit2.http.Part
import javax.inject.Inject


@HiltViewModel
class AddPositionMapDialogViewModel  @Inject constructor(
    private val storeRepository: StoreRepository,
):ViewModel(){

    suspend fun modifyStore(@Part store:RequestBody) =
        storeRepository.modifyStore(store)

}