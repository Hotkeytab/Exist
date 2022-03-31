package com.example.gtm.data.entities.response

data class ResponseX(
    val createdAt: String,
    val description: String,
    val id: Int,
    val questionId: Int,
    val rate: Int,
    val responsePictures: List<ResponsePictureX>,
    val surveyResponseId: Int,
    val updatedAt: String
)