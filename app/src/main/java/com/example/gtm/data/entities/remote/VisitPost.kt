package com.example.gtm.data.entities.remote

data class VisitPost(
    var id: Int?,
    var day : String,
    var order : Int,
    var storeId: Int,
    var userId: Int,
    var planned: Boolean,
    var type: String?
)
