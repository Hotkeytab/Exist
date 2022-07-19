package com.example.gtm.data.entities.response.kpi.analysesupervisuer

data class SurveyResponsesAnalyseKpi(
    val average: Double,
    val createdAt: String,
    val id: Int,
    val responses: List<ResponsesAnalyse>,
    val store: StoreAnalyse,
    val storeId: Int,
    val surveyId: Int,
    val updatedAt: String,
    val userId: Int,
    val visitId: Int
)