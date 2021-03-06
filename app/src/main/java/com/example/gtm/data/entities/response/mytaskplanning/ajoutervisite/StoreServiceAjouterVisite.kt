package com.example.gtm.data.entities.response.mytaskplanning.ajoutervisite

data class StoreServiceAjouterVisite(
    val address: String,
    val createdAt: String,
    val email: String,
    val enabled: Boolean,
    val governorate: String,
    val id: Int,
    var lat: Double?,
    var lng: Double?,
    val name: String,
    val path: String,
    val phone_number: String,
    val postal_code: Int,
    val storePictures: List<StorePicture>,
    val type: String,
    val updatedAt: String
)