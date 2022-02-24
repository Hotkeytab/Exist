package com.example.gtm.data.entities.response

data class Survey(
    val createdAt: String,
    val id: Int,
    val name: String,
    val questionCategories: List<QuestionCategoryXX>,
    val updatedAt: String
)