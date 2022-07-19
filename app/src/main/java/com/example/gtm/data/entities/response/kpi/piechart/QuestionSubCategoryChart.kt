package com.example.gtm.data.entities.response.kpi.piechart

data class QuestionSubCategoryChart(
    val createdAt: String,
    val id: Int,
    val name: String,
    val questionCategory: QuestionCategoryChart,
    val questionCategoryId: Int,
    val updatedAt: String
)