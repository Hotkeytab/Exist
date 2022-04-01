package com.example.gtm.ui.home.mytask.survey.quiz

import androidx.lifecycle.ViewModel
import com.example.gtm.data.entities.remote.SignInPost
import com.example.gtm.data.entities.remote.VisitPost
import com.example.gtm.data.repository.AuthRepository
import com.example.gtm.data.repository.SurveyRepository
import com.example.gtm.data.repository.VisiteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MyQuizViewModel  @Inject constructor(
    private val surveyRepository: SurveyRepository
):ViewModel(){

    suspend fun getSurvey() = surveyRepository.getSurvey()
    suspend fun addVisite(visitPost: ArrayList<VisitPost>)  = surveyRepository.addVisite(visitPost)

}