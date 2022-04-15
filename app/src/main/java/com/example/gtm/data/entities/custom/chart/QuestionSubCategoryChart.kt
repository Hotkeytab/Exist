package com.example.gtm.data.entities.custom.chart

data class QuestionSubCategoryChart(
    val createdAt: String,
    val id: Int,
    val name: String,
    val questionCategoryChart: QuestionCategoryChart,
    val questionCategoryId: Int,
    val updatedAt: String
)