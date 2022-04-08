package com.example.gtm.data.entities.custom
import com.example.gtm.data.entities.ui.Image


data class QuestionNewPost(
    val questionId: Long? = null,
    val rate: Double? = null,
    val description: String? = null,
    val images: ArrayList<Image>? = null
)
