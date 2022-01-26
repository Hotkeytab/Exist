package com.example.gtm.data.entities.response

data class Store(
    val address: String,
    val createdAt: String,
    val email: String,
    val enabled: Boolean,
    val governorate: String,
    val id: Int,
    val lat: Double,
    val lng: Double,
    val name: String,
    val path: String,
    val phone_number: String,
    val postal_code: Int,
    val size: String,
    val storeGroupId: Any,
    val type: String,
    val updatedAt: String
)