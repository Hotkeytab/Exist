package com.example.gtm.data.entities.response

data class QuestionSubCategoryXX(
    val createdAt: String,
    val id: Int,
    val name: String,
    val questionCategoryId: Int,
    val questions: List<QuestionXX>,
    val updatedAt: String
)