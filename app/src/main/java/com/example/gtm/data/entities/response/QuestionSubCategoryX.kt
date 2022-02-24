package com.example.gtm.data.entities.response

data class QuestionSubCategoryX(
    val createdAt: String,
    val id: Int,
    val name: String,
    val questionCategory: QuestionCategoryX,
    val questionCategoryId: Int,
    val updatedAt: String
)