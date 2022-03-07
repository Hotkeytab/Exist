package com.example.gtm.data.remote.store

import com.example.gtm.data.remote.BaseRemoteDataSource
import javax.inject.Inject

class StoreRemoteDataSource @Inject constructor(
    private val storeService: StoreService
) : BaseRemoteDataSource() {
    suspend fun getStores() = getResult { storeService.getStores() }
}