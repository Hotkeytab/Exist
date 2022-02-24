package com.example.gtm.data.entities.response

data class QuestionX(
    val coef: Int,
    val createdAt: String,
    val id: Int,
    val images: Boolean,
    val imagesRequired: Boolean,
    val name: String,
    val questionSubCategory: QuestionSubCategoryX,
    val questionSubCategoryId: Int,
    val required: Boolean,
    val updatedAt: String
)