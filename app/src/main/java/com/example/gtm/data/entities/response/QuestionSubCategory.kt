package com.example.gtm.data.entities.response


data class QuestionSubCategory(
    val createdAt: String,
    val id: Int,
    val name: String,
    val questionCategoryId: Int,
    val questions: List<Question>,
    val updatedAt: String
)