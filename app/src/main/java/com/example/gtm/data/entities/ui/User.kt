package com.example.gtm.data.entities.ui

data class User(
    val id: Int,
    var first_name: String,
    var last_name:String,
    var email: String,
    val password: String,
    var phone_number: String,
    val enabled: Boolean,
    val gender: String,
    val roleId: Int

) {
}