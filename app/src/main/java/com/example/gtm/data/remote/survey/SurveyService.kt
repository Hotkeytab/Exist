package com.example.gtm.data.remote.survey

import com.example.gtm.data.entities.remote.VisitPost
import com.example.gtm.data.entities.response.kpi.Quiz
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.compterendu.SubjectCompteRendu
import com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.category.SuccessResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface SurveyService {
    @GET("survey/")
    suspend fun getSurvey() : Response<Quiz>

    @Multipart
    @POST("surveyResponse")
    suspend fun postSurveyResponse(@Part files: ArrayList<MultipartBody.Part?>, @Part("surveyResponse") surveyResponse: RequestBody ,@Part("report") report: RequestBody) : Response<SuccessResponse>

    @POST("visit")
    suspend fun addVisit(@Body visitPost: ArrayList<VisitPost>) : Response<SuccessResponse>

    @GET("reportSubject")
    suspend fun getSubjectReport() : Response<SubjectCompteRendu>
    abstract fun postSurveyResponse(files: ArrayList<MultipartBody.Part?>, surveyResponse: RequestBody): Response<SuccessResponse>

}