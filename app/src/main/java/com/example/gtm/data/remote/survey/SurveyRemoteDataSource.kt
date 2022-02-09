package com.example.gtm.data.remote.survey

import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.remote.BaseRemoteDataSource
import javax.inject.Inject

class SurveyRemoteDataSource  @Inject constructor(
    private val surveyService: SurveyService
):BaseRemoteDataSource(){

    suspend fun getSurvey() = getResult { surveyService.getSurvey() }
}