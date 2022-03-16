package com.example.gtm.data.remote.store

import com.example.gtm.data.remote.BaseRemoteDataSource
import okhttp3.RequestBody
import retrofit2.http.Part
import javax.inject.Inject

class StoreRemoteDataSource @Inject constructor(
    private val storeService: StoreService
) : BaseRemoteDataSource() {
    suspend fun getStores() = getResult { storeService.getStores() }

    suspend fun modifyStore(@Part store:RequestBody) = getResult {storeService.modifyStore(store)}
}