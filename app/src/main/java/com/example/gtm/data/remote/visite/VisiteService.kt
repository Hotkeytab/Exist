package com.example.gtm.data.remote.visite


import com.example.gtm.data.entities.response.*
import com.example.gtm.data.entities.ui.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import okhttp3.RequestBody
import retrofit2.Call

import retrofit2.http.POST

import retrofit2.http.Multipart


interface VisiteService {

    @GET("/visit/{user_id}/{date_begin}/{date_end}")
    suspend fun getVisites(@Path("user_id") user_id: String,@Path("date_begin") date_begin: String,@Path("date_end") date_end: String): Response<VisiteResponse>

    @DELETE("/visit/{visitId}")
    suspend fun deleteVisite(@Path("visitId") visitId : Int) : Response<SuccessResponseWithMessage>

    @GET("/surveyResponse/{user_id}/{date_begin}/{date_end}")
    suspend fun getSurveyResponse(@Path("user_id") user_id: String,@Path("date_begin") date_begin: String,@Path("date_end") date_end: String): Response<SurveyResponse>
}