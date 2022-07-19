package com.example.gtm.data.entities.response.kpi.piechart

data class QuestionChart(
    val coef: Int,
    val createdAt: String,
    val id: Int,
    val images: Boolean,
    val imagesRequired: Boolean,
    val name: String,
    val questionSubCategory: QuestionSubCategoryChart,
    val questionSubCategoryId: Int,
    val required: Boolean,
    val updatedAt: String
)