package com.example.gtm.data.entities.response.kpi

import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.quiz.QuizData


data class Quiz (
    val data: List<QuizData>,
    val succes: Int
)