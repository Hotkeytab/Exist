package com.example.gtm.data.entities.remote

import okhttp3.MultipartBody

data class QuestionPost(
    val questionId: Long? = null,
    val rate: Long? = null,
    val description: Any? = null,
    val images: List<MultipartBody.Part?>? = null
)
