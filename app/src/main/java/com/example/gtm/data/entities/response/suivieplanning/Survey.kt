package com.example.gtm.data.entities.response.suivieplanning

data class Survey(
    val createdAt: String,
    val id: Int,
    val name: String,
    var average: Double,
    val questionCategories: List<QuestionCategorySurveySuiviePlanning>,
    val updatedAt: String
)