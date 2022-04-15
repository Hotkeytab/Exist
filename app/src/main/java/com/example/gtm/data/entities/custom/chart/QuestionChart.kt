package com.example.gtm.data.entities.custom.chart

data class QuestionChart(
    val coef: Int,
    val createdAt: String,
    val id: Int,
    val images: Boolean,
    val imagesRequired: Boolean,
    val name: String,
    val questionSubCategoryChart: QuestionSubCategoryChart,
    val questionSubCategoryId: Int,
    val required: Boolean,
    val updatedAt: String
)