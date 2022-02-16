package com.example.gtm.data.remote.survey

import com.example.gtm.data.entities.response.Quiz
import com.example.gtm.data.entities.response.SuccessResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SurveyService {
    @GET("survey/")
    suspend fun getSurvey() : Response<Quiz>

    @Multipart
    @POST("surveyResponse")
    suspend fun postSurveyResponse(@Part files: ArrayList<MultipartBody.Part?>, @Part("surveyResponse") surveyResponse: RequestBody) : Response<SuccessResponse>

}