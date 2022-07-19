package com.example.gtm.data.entities.response.mytaskplanning.getvisite

data class Visite(
    val createdAt: String,
    val day: String,
    val id: Int,
    val order: Int,
    val store: Store,
    val storeId: Int,
    val planned: Boolean,
    val start : String?,
    val end : String?,
    val type: String,
    val updatedAt: String,
    val userId: Int,
    var pe: Int = 0,
    var ps: Int = 0,
    var pe_time: String = "",
    var ps_time: String = "",
    var horsPlanning: Boolean =  false
)