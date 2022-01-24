package com.example.gtm.data.remote.user


import com.example.gtm.data.entities.response.UserResponse
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Path

interface UserService {

    @GET("/user/{user_username}")
    suspend fun getUser(@Path("user_username") username: String?): Response<UserResponse>

}