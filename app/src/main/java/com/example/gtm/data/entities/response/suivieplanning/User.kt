package com.example.gtm.data.entities.response.suivieplanning

data class User(
    val createdAt: String,
    val email: String,
    val enabled: Boolean,
    val first_name: String,
    val gender: String,
    val id: Int,
    val last_name: String,
    val password: String,
    val phone_number: String,
    val profile_picture: Any,
    val roleId: Int,
    val updatedAt: String,
    val username: String
)