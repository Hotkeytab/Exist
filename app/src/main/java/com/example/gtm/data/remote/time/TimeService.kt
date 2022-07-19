package com.example.gtm.data.remote.time

import com.example.gtm.data.entities.response.mytaskplanning.timeservice.TimeClass
import retrofit2.Response

import retrofit2.http.GET

interface TimeService {
    @GET("Africa/Tunis")
    suspend fun getCurrentTime() : Response<TimeClass>
}