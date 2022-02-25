package com.example.gtm.data.entities.ui


data class Survey(
    val id: Int,
    val coef : Double,
    val rate: Float,
    val description: String,
    val urls: ArrayList<Image>?
    ) {
}