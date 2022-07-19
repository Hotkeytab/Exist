package com.example.gtm.data.entities.response.kpi.analysesupervisuer

data class ResponsesAnalyse(
    val createdAt: String,
    val description: String,
    val id: Int,
    val questionId: Int,
    val rate: Int,
    val responsePictures: List<ResponsePictureAnalyse>,
    val surveyResponseId: Int,
    val updatedAt: String
)