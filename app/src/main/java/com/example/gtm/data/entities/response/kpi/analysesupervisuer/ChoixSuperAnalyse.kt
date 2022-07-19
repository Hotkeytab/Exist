package com.example.gtm.data.entities.response.kpi.analysesupervisuer

data class ChoixSuperAnalyse(
    val createdAt: String,
    val day: String,
    val end: String,
    val id: Int,
    val order: Int,
    val planned: Boolean,
    val start: String,
    val storeId: Int,
    val surveyResponses: List<SurveyResponsesAnalyseKpi>,
    val updatedAt: String,
    val user: UserAnalyse,
    val userId: Int
)