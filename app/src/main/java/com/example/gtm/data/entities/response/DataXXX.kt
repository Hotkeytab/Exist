package com.example.gtm.data.entities.response

data class DataXXX(
    val createdAt: String,
    val day: String,
    val end: String,
    val id: Int,
    val order: Int,
    val planned: Boolean,
    val start: String,
    val storeId: Int,
    val surveyResponses: List<SurveyResponseX>,
    val updatedAt: String,
    val user: UserX,
    val userId: Int
)