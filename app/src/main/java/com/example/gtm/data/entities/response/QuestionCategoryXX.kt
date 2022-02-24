package com.example.gtm.data.entities.response

data class QuestionCategoryXX(
    val createdAt: String,
    val id: Int,
    val name: String,
    val questionSubCategories: List<QuestionSubCategoryXX>,
    val surveyId: Int,
    val updatedAt: String
)