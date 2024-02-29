package com.example.projectm.network

import com.example.projectm.data.models.GetAnalyticDataResposne
import com.example.projectm.data.models.GetBuildingDataResponseItem
import retrofit2.http.GET

interface ApiService {

    @GET("GetBuildingData")
    suspend fun getBuildingData():List<GetBuildingDataResponseItem>

    @GET("GetAnalyticData")
    suspend fun getAnalyticData():List<GetAnalyticDataResposne>

}