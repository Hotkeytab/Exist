package com.example.gtm.ui.home.mytask.survey.question

import androidx.lifecycle.ViewModel
import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.repository.AuthRepository
import com.example.gtm.data.repository.SurveyRepository
import com.example.gtm.data.repository.VisiteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject


@HiltViewModel
class QuestionFragmentViewModel  @Inject constructor(
    private val surveyRepository: SurveyRepository
):ViewModel(){

    suspend fun postSurveyResponse(surveyResponse: RequestBody) =
        surveyRepository.postSurveyResponse(surveyResponse)
}