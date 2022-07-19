package com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.quiz


data class QuestionCategory(
    val createdAt: String,
    val id: Int,
    val name: String,
    val questionSubCategories: List<QuestionSubCategory>,
    val surveyId: Int,
    val updatedAt: String
)