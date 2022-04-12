package com.example.gtm.data.entities.custom


data class KpiStats(
    val moyenneRetard: Long,
    val moyenneDernierPointage: String,
    val visitesPlanifie: Int,
    val visitesNonPlanifie: Int,
    val visitesRealise: Int,
    val questionnaireRealise: Int,
    val moyenneQuestionnaire: Double,
    val nombrePhotos: Int,
    val performance: Int
)
