package com.example.gtm.data.repository

import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.entities.ui.User
import com.example.gtm.data.remote.BaseRemoteDataSource
import com.example.gtm.data.remote.auth.AuthRemoteDataSource
import com.example.gtm.data.remote.chart.ChartRemoteDataSource
import com.example.gtm.data.remote.user.UserRemoteDataSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class ChartRepository @Inject constructor(
    private val chartRemoteDataSource: ChartRemoteDataSource
) {

    suspend fun getStatTable(from: String,to: String,stores:ArrayList<Int>,surveyId:ArrayList<Int>,supervisors:ArrayList<Int>,governorates:ArrayList<String>) = chartRemoteDataSource.getStatTable(from,to,stores,surveyId,supervisors,governorates)


    suspend fun getStatChart(stores:ArrayList<Int>,surveyId:Int,from: String,to: String,supervisors:ArrayList<Int>,governorates:ArrayList<String>) =   chartRemoteDataSource.getStatChart(stores,surveyId,from,to,supervisors,governorates)

}