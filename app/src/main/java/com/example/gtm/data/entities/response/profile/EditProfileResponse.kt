package com.example.gtm.data.entities.response.profile

data class EditProfileResponse(
    val success: Int,
    val message: String,
    val data: ArrayList<Int>
)