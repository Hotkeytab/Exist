package com.example.gtm.data.remote.chart


import com.example.gtm.data.entities.response.kpi.piechart.ChartResponse
import com.example.gtm.data.entities.response.kpi.analysesupervisuer.AnalyseKpi
import retrofit2.Response
import retrofit2.http.*


interface ChartService {


   // @Headers("Content-Type:application/json; charset=UTF-8")
    @GET("/chart/supervisorPerformance/{from}/{to}/{stores}/{surveyId}/{supervisors}/{governorates}")
    suspend fun getStatTable(@Path("from") from: String,@Path("to") to: String,@Path("stores") stores: ArrayList<Int>,@Path("surveyId") surveyId: ArrayList<Int>,@Path("supervisors") supervisors: ArrayList<Int>,@Path("governorates") governorates: ArrayList<String>): Response<AnalyseKpi>

    @GET("/chart/{stores}/{surveyId}/{from}/{to}/{supervisors}/{governorates}")
    suspend fun getStatChart(@Path("stores") stores: ArrayList<Int>,@Path("surveyId") surveyId: Int,@Path("from") from: String,@Path("to") to: String,@Path("supervisors") supervisors: ArrayList<Int>,@Path("governorates") governorates: ArrayList<String>): Response<ChartResponse>

}