package com.example.gtm.data.entities.response.suivieplanning

data class QuestionSuiviePlanning(
    val coef: Int,
    val createdAt: String,
    val id: Int,
    val images: Boolean,
    val imagesRequired: Boolean,
    val name: String,
    val questionSubCategory: QuestionSubCategorySuiviePlanning,
    val questionSubCategoryId: Int,
    val required: Boolean,
    val updatedAt: String
)