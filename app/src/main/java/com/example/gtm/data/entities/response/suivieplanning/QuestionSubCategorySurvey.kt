package com.example.gtm.data.entities.response.suivieplanning

data class QuestionSubCategorySurvey(
    val createdAt: String,
    val id: Int,
    val name: String,
    val questionCategoryId: Int,
    val questions: List<QuestionSurvey>,
    val updatedAt: String
)