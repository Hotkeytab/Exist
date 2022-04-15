package com.example.gtm.data.entities.custom.chart

data class DataChart(
    val average: Double,
    val createdAt: String,
    val id: Int,
    val respons: List<ResponseChart>,
    val store: StoreChart,
    val storeId: Int,
    val surveyId: Int,
    val updatedAt: String,
    val userChart: UserChart,
    val userId: Int,
    val visitId: Any
)