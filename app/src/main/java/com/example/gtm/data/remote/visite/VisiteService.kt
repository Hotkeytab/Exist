package com.example.gtm.data.remote.visite


import com.example.gtm.data.entities.response.mytaskplanning.getvisite.VisiteResponse
import com.example.gtm.data.entities.response.mytaskplanning.supprimervisite.SuccessResponseDeleteVisite
import com.example.gtm.data.entities.response.suivieplanning.SurveyResponse
import retrofit2.Response
import retrofit2.http.*


interface VisiteService {

    @GET("/visit/{user_id}/{date_begin}/{date_end}")
    suspend fun getVisites(@Path("user_id") user_id: String,@Path("date_begin") date_begin: String,@Path("date_end") date_end: String): Response<VisiteResponse>

    @DELETE("/visit/{visitId}")
    suspend fun deleteVisite(@Path("visitId") visitId : Int) : Response<SuccessResponseDeleteVisite>

    @GET("/surveyResponse/{user_id}/{date_begin}/{date_end}")
    suspend fun getSurveyResponse(@Path("user_id") user_id: String,@Path("date_begin") date_begin: String,@Path("date_end") date_end: String): Response<SurveyResponse>
}