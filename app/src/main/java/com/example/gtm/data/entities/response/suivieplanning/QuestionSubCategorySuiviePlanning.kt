package com.example.gtm.data.entities.response.suivieplanning

data class QuestionSubCategorySuiviePlanning(
    val createdAt: String,
    val id: Int,
    val name: String,
    val questionCategory: QuestionCategorySuiviePlanning,
    val questionCategoryId: Int,
    val updatedAt: String
)