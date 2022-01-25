package com.example.gtm.data.remote.user

import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.entities.ui.User
import com.example.gtm.data.remote.BaseRemoteDataSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val userService: UserService
) : BaseRemoteDataSource() {

    suspend fun getUser(username: String) = getResult { userService.getUser(username) }

    suspend fun changeProfile(file: RequestBody?, user: RequestBody) =
        getResult { userService.changeProfile(file, user) }
}