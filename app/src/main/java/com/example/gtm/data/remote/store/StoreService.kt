package com.example.gtm.data.remote.store


import com.example.gtm.data.entities.response.*
import com.example.gtm.data.entities.ui.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import okhttp3.RequestBody
import retrofit2.Call

import retrofit2.http.POST

import retrofit2.http.Multipart


interface StoreService {
    @GET("store/")
    suspend fun getStores() : Response<GetStore>

    @Multipart
    @POST("store/")
    suspend fun modifyStore(@Part("store") store:RequestBody) : Response<ModifyStoreResponse>
}