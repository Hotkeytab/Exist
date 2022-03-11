package com.example.gtm.data.entities.remote

data class PointVisitPost(
    var day : String,
    var order : Int,
    var id: Int,
    var userId: Int,
    var planned: Boolean,
    var type: String?
)
