package com.example.gtm.ui.home.mytask.survey.compterendu

import androidx.lifecycle.ViewModel
import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.repository.AuthRepository
import com.example.gtm.data.repository.SurveyRepository
import com.example.gtm.data.repository.VisiteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


@HiltViewModel
class CompteRenduViewModel  @Inject constructor(
    private val surveyRepository: SurveyRepository
):ViewModel(){


    suspend fun getSubjectReport() = surveyRepository.getSubjectReport()


}