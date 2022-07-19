package com.example.gtm.data.remote.auth

import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.entities.response.login.SignInResponse
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body signinObject: SignInPost) : Response<SignInResponse>

}