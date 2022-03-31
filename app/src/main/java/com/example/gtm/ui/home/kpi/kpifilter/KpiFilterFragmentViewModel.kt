package com.example.gtm.ui.home.kpi.kpifilter

import androidx.lifecycle.ViewModel
import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import retrofit2.http.Part
import javax.inject.Inject


@HiltViewModel
class KpiFilterFragmentViewModel  @Inject constructor(
    private val chartRepository: ChartRepository,
):ViewModel(){

    suspend fun getStatTable(from: String,to: String,stores:ArrayList<Int>,surveyId:ArrayList<Int>,supervisors:ArrayList<Int>,governorates:ArrayList<String>) = chartRepository.getStatTable(from,to,stores,surveyId,supervisors,governorates)

}