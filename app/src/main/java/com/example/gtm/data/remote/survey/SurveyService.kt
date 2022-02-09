package com.example.gtm.data.remote.survey

import com.example.gtm.data.entities.response.Quiz
import retrofit2.Response

import retrofit2.http.GET

interface SurveyService {
    @GET("survey/")
    suspend fun getSurvey() : Response<Quiz>

}