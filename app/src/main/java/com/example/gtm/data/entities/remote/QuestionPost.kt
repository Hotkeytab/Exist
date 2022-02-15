package com.example.gtm.data.entities.remote


data class QuestionPost(
    val questionId: Long? = null,
    val rate: Long? = null,
    val description: Any? = null,
    val images: List<ImagePath?>? = null
)
