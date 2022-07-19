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

    suspend fun postSurveyResponse(files: ArrayList<MultipartBody.Part?>, surveyResponse: RequestBody , report : RequestBody) = getResult { surveyService.postSurveyResponse(files,surveyResponse,report) }

    suspend fun addVisit(visitPost: ArrayList<VisitPost>) = getResult { surveyService.addVisit(visitPost) }

    suspend fun getSubjectReport() = getResult { surveyService.getSubjectReport() }
}