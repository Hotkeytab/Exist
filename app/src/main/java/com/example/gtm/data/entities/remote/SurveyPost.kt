package com.example.gtm.data.entities.remote

import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.compterendu.SubjectCompteRendu

data class SurveyPost(
    val userId: Long,
    val storeId: Long,
    val visitId: Int,
    val surveyId: Long,
    val average: Double = 0.0,
    val responses: List<QuestionPost?>?,
    //val Report : SubjectCompteRendu
)
