package com.example.gtm.data.remote.user


import com.example.gtm.data.entities.response.profile.EditProfileResponse
import com.example.gtm.data.entities.response.login.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import okhttp3.RequestBody

import retrofit2.http.POST

import retrofit2.http.Multipart




interface UserService {

    @GET("/user/{user_username}")
    suspend fun getUser(@Path("user_username") username: String?): Response<UserResponse>

    @Multipart
    @POST("/user")
    suspend fun changeProfile(@Part file:MultipartBody.Part?,@Part("user") user:RequestBody):Response<EditProfileResponse>

}