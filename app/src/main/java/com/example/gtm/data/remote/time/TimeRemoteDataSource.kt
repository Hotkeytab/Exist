package com.example.gtm.data.remote.time

import com.example.gtm.data.remote.BaseRemoteDataSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class TimeRemoteDataSource  @Inject constructor(
    private val timeService: TimeService
):BaseRemoteDataSource(){

    suspend fun getCurrentTime() = getResult { timeService.getCurrentTime() }
}