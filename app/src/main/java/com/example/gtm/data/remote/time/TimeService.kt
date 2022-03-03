package com.example.gtm.data.remote.time

import com.example.gtm.data.entities.response.Quiz
import com.example.gtm.data.entities.response.SuccessResponse
import com.example.gtm.data.entities.response.TimeClass
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface TimeService {
    @GET("Africa/Tunis")
    suspend fun getCurrentTime() : Response<TimeClass>
}