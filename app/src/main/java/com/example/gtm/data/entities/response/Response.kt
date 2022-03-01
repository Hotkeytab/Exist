package com.example.gtm.data.entities.response

data class Response(
    val createdAt: String,
    val description: Any,
    val id: Int,
    val question: QuestionX,
    val questionId: Int,
    val rate: Float,
    val responsePictures: List<ResponsePicture>,
    val surveyResponseId: Int,
    val updatedAt: String
)