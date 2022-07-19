package com.example.gtm.data.entities.response.kpi.piechart

data class DataChart(
    val average: Double,
    val createdAt: String,
    val id: Int,
    val responses: List<ResponseChart>,
    val store: StoreChart,
    val storeId: Int,
    val surveyId: Int,
    val updatedAt: String,
    val user: UserChart,
    val userId: Int,
    val visitId: Any
)