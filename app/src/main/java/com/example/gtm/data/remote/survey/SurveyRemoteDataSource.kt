package com.example.gtm.data.remote.survey

import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.entities.remote.VisitPost
import com.example.gtm.data.remote.BaseRemoteDataSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class SurveyRemoteDataSource  @Inject constructor(
    private val surveyService: SurveyService
):BaseRemoteDataSource(){

    suspend fun getSurvey() = getResult { surveyService.getSurvey() }

    suspend fun postSurveyResponse(files: ArrayList<MultipartBody.Part?>, surveyResponse: RequestBody) = getResult { surveyService.postSurveyResponse(files,surveyResponse) }

    suspend fun addVisit(visitPost: VisitPost) = getResult { surveyService.addVisit(visitPost) }
}