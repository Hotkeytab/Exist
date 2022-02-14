package com.example.gtm.data.remote.survey

import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.remote.BaseRemoteDataSource
import okhttp3.RequestBody
import javax.inject.Inject

class SurveyRemoteDataSource  @Inject constructor(
    private val surveyService: SurveyService
):BaseRemoteDataSource(){

    suspend fun getSurvey() = getResult { surveyService.getSurvey() }

    suspend fun postSurveyResponse(surveyResponse: RequestBody) = getResult { surveyService.postSurveyResponse(surveyResponse) }
}