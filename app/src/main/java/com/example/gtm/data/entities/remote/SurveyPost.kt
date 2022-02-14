package com.example.gtm.data.entities.remote

data class SurveyPost(
    val userId: Long,
    val storeId: Long,
    val surveyId: Long,
    val responses: List<QuestionPost>
)
