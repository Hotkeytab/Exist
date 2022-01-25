package com.example.gtm.data.remote.user


import com.example.gtm.data.entities.response.EditProfileResponse
import com.example.gtm.data.entities.response.UserResponse
import com.example.gtm.data.entities.ui.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import okhttp3.RequestBody
import retrofit2.Call

import retrofit2.http.POST

import retrofit2.http.Multipart




interface UserService {

    @GET("/user/{user_username}")
    suspend fun getUser(@Path("user_username") username: String?): Response<UserResponse>

    @Multipart
    @POST("/user")
    suspend fun changeProfile(@Part("file") file:RequestBody?,@Part("user") user:RequestBody):Response<EditProfileResponse>

}