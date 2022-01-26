package com.example.gtm.data.remote.visite

import com.example.gtm.data.remote.BaseRemoteDataSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class VisiteRemoteDataSource @Inject constructor(
    private val visiteService: VisiteService
) : BaseRemoteDataSource() {
    suspend fun getVisites(user_id: String, date_begin: String, date_end: String) =
        getResult { visiteService.getVisites(user_id, date_begin, date_end) }

}