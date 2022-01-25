package com.example.gtm.data.repository

import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.entities.ui.User
import com.example.gtm.data.remote.BaseRemoteDataSource
import com.example.gtm.data.remote.auth.AuthRemoteDataSource
import com.example.gtm.data.remote.user.UserRemoteDataSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDataSource: UserRemoteDataSource
) {

    suspend fun getUser(username: String) = userDataSource.getUser(username)

    suspend fun changeProfile(file: RequestBody?, user: RequestBody)= userDataSource.changeProfile(file,user)
}