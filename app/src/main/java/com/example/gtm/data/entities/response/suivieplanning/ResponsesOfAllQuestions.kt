package com.example.gtm.data.entities.response.suivieplanning

data class ResponsesOfAllQuestions(
    val average: Double,
    val createdAt: String,
    val id: Int,
    val responses: List<Response>,
    val store: StoreSuiviePlanning,
    val storeId: Int,
    val survey: Survey,
    val surveyId: Int,
    val updatedAt: String,
    val user: User,
    val userId: Int
)