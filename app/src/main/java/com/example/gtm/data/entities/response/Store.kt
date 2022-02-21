package com.example.gtm.data.entities.response

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Store(
    val address: String,
    val createdAt: String,
    val date: String,
    val email: String,
    val enabled: Boolean,
    val governorate: String,
    val id: Int,
    var lat: Double,
    var lng: Double,
    val name: String,
    val path: String,
    val phone_number: String,
    val postal_code: Int,
    val size: String,
    val storeGroupId: Any,
    val type: String,
    val updatedAt: String
) {
    fun calculateDistance(lat_b: Float, lng_b: Float): Float {
        val earthRadius = 3958.75
        val latDiff = Math.toRadians((lat_b - lat))
        val lngDiff = Math.toRadians((lng_b - lng))
        val a = sin(latDiff / 2) * sin(latDiff / 2) +
                cos(Math.toRadians(lat.toDouble())) * cos(Math.toRadians(lat_b.toDouble())) *
                sin(lngDiff / 2) * sin(lngDiff / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = earthRadius * c
        val meterConversion = 1609
        return (distance * meterConversion.toFloat()).toFloat()
    }
}