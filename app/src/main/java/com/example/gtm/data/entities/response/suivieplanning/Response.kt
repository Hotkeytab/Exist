package com.example.gtm.data.entities.response.suivieplanning

data class Response(
    val createdAt: String,
    val description: String,
    val id: Int,
    val question: QuestionSuiviePlanning,
    val questionId: Int,
    val rate: Float,
    val responsePictures: List<ResponsePicture>,
    val surveyResponseId: Int,
    val updatedAt: String
)