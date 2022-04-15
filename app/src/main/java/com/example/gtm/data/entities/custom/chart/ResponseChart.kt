package com.example.gtm.data.entities.custom.chart

data class ResponseChart(
    val createdAt: String,
    val description: String,
    val id: Int,
    val questionChart: QuestionChart,
    val questionId: Int,
    val rate: Int,
    val surveyResponseId: Int,
    val updatedAt: String
)