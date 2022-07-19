package com.example.gtm.data.entities.response.suivieplanning

data class QuestionCategorySurveySuiviePlanning(
    val createdAt: String,
    val id: Int,
    val name: String,
    val questionSubCategories: List<QuestionSubCategorySurvey>,
    val surveyId: Int,
    val updatedAt: String
)