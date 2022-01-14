package com.example.gtm.data.remote.auth

import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.remote.BaseRemoteDataSource
import javax.inject.Inject

class AuthRemoteDataSource  @Inject constructor(
    private val authService: AuthService
):BaseRemoteDataSource(){

    suspend fun login(signinObject: SignInPost) = getResult { authService.login(signinObject) }
}