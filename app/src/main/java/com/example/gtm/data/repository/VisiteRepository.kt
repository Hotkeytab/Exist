package com.example.gtm.data.repository

import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.entities.ui.User
import com.example.gtm.data.remote.BaseRemoteDataSource
import com.example.gtm.data.remote.auth.AuthRemoteDataSource
import com.example.gtm.data.remote.user.UserRemoteDataSource
import com.example.gtm.data.remote.visite.VisiteRemoteDataSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class VisiteRepository @Inject constructor(
    private val visiteDataSource: VisiteRemoteDataSource
) {

    suspend fun getVisites(user_id: String, date_begin: String, date_end: String) =
        visiteDataSource.getVisites(user_id, date_begin, date_end)
}