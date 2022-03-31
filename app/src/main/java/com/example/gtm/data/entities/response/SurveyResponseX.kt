package com.example.gtm.data.entities.response

data class SurveyResponseX(
    val average: Int,
    val createdAt: String,
    val id: Int,
    val responses: List<ResponseX>,
    val store: StoreXX,
    val storeId: Int,
    val surveyId: Int,
    val updatedAt: String,
    val userId: Int,
    val visitId: Int
)