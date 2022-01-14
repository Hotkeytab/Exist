package com.example.gtm.data.entities.response

data class SignInResponse(
    val success: Int,
    val message: String,
    val token: String
) {
}