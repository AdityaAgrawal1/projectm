package com.example.projectm.network

import com.example.projectm.data.models.GetAnalyticDataResposne
import com.example.projectm.data.models.GetBuildingDataResponse
import retrofit2.http.GET

interface ApiService {

    @GET("GetBuildingData")
    suspend fun getBuildingData():GetBuildingDataResponse

    @GET("GetAnalyticData")
    suspend fun getAnalyticData():GetAnalyticDataResposne

}