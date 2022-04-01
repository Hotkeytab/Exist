package com.example.gtm.data.remote.chart


import com.example.gtm.data.entities.response.*
import com.example.gtm.data.entities.ui.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import okhttp3.RequestBody
import retrofit2.Call

import retrofit2.http.POST

import retrofit2.http.Multipart


interface ChartService {


   // @Headers("Content-Type:application/json; charset=UTF-8")
    @GET("/chart/supervisorPerformance/{from}/{to}/{stores}/{surveyId}/{supervisors}/{governorates}")
    suspend fun getStatTable(@Path("from") from: String,@Path("to") to: String,@Path("stores") stores: ArrayList<Int>,@Path("surveyId") surveyId: ArrayList<Int>,@Path("supervisors") supervisors: ArrayList<Int>,@Path("governorates") governorates: ArrayList<String>): Response<AnalyseKpi>
}