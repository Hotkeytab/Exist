package com.example.gtm.data.repository

import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.remote.BaseRemoteDataSource
import com.example.gtm.data.remote.auth.AuthRemoteDataSource
import javax.inject.Inject

class AuthRepository  @Inject constructor(
    private val  authDataSource: AuthRemoteDataSource
) {
    suspend fun login (signinObject: SignInPost) = authDataSource.login(signinObject)


}