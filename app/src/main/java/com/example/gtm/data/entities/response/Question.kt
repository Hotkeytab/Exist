package com.example.gtm.data.entities.response





data class Question(
    val coef: Double,
    val createdAt: String,
    val id: Int,
    val images: Boolean,
    val imagesRequired: Boolean,
    val name: String,
    val questionSubCategoryId: Int,
    val required: Boolean,
    val updatedAt: String,
    var state: Int = -1
)