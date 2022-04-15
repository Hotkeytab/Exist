package com.example.gtm.data.remote.chart

import com.example.gtm.data.remote.BaseRemoteDataSource
import okhttp3.RequestBody
import retrofit2.http.Part
import javax.inject.Inject

class ChartRemoteDataSource @Inject constructor(
    private val chartService: ChartService
) : BaseRemoteDataSource() {

    suspend fun getStatTable(from: String,to: String,stores:ArrayList<Int>,surveyId:ArrayList<Int>,supervisors:ArrayList<Int>,governorates:ArrayList<String>) = getResult { chartService.getStatTable(from,to,stores,surveyId,supervisors,governorates) }


    suspend fun getStatChart(stores:ArrayList<Int>,surveyId:Int,from: String,to: String,supervisors:ArrayList<Int>,governorates:ArrayList<String>) = getResult { chartService.getStatChart(stores,surveyId,from,to,supervisors,governorates) }
}