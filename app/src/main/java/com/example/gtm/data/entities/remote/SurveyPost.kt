package com.example.gtm.data.entities.remote

data class SurveyPost(
    val userId: Long,
    val storeId: Long,
    val visitId: Int,
    val surveyId: Long,
    val average: Double = 0.0,
    val responses: List<QuestionPost>?
)
