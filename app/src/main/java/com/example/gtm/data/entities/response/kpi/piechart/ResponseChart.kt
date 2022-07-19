package com.example.gtm.data.entities.response.kpi.piechart

data class ResponseChart(
    val createdAt: String,
    val description: String,
    val id: Int,
    val question: QuestionChart,
    val questionId: Int,
    val rate: Int,
    val surveyResponseId: Int,
    val updatedAt: String
)