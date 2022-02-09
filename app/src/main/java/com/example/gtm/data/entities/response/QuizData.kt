package com.example.gtm.data.entities.response


data class QuizData(
    val createdAt: String,
    val id: Int,
    val name: String,
    val questionCategories: List<QuestionCategory>,
    val updatedAt: String
)