package com.example.gtm.data.entities.response.login

data class SignInResponse(
    val success: Int,
    val message: String,
    val token: String
) {
}