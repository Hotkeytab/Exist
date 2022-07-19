package com.example.gtm.data.entities.response.suivieplanning

import com.example.gtm.data.entities.response.suivieplanning.ResponsesOfAllQuestions

data class SurveyResponse(
    val data: List<ResponsesOfAllQuestions>,
    val succes: Int
)