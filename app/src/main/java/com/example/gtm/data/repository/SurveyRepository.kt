package com.example.gtm.data.repository

import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.entities.ui.User
import com.example.gtm.data.remote.BaseRemoteDataSource
import com.example.gtm.data.remote.auth.AuthRemoteDataSource
import com.example.gtm.data.remote.survey.SurveyRemoteDataSource
import com.example.gtm.data.remote.user.UserRemoteDataSource
import com.example.gtm.data.remote.visite.VisiteRemoteDataSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class SurveyRepository @Inject constructor(
    private val surveyDataSource: SurveyRemoteDataSource
) {

    suspend fun getSurvey() =  surveyDataSource.getSurvey()
}