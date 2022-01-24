package com.example.gtm.data.remote.user

import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.remote.BaseRemoteDataSource
import javax.inject.Inject

class UserRemoteDataSource  @Inject constructor(
    private val userService: UserService
):BaseRemoteDataSource(){

    suspend fun getUser(username:String) = getResult { userService.getUser(username) }
}