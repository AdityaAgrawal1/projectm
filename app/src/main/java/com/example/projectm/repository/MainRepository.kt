package com.example.projectm.repository

import com.example.projectm.utils.resource.Resource
import com.example.projectm.BaseRepository
import com.example.projectm.data.models.GetAnalyticDataResposne
import com.example.projectm.data.models.GetBuildingDataResponseItem
import com.example.projectm.network.ApiService
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ActivityScoped
class MainRepository @Inject constructor(
    private val api: ApiService
) : BaseRepository() {

    suspend fun fetchBuildings():
            Flow<Resource<List<GetBuildingDataResponseItem>>> =
        flow {
            val response = safeApiCall {
                api.getBuildingData()
            }
            when (response) {
                is Resource.Success -> {
                    emit(Resource.Success(response.value))
                }

                is Resource.Failure -> emit(response)
                is Resource.Loading -> emit(Resource.Loading)
            }
        }

    suspend fun fetchAnalytics():
            Flow<Resource<List<GetAnalyticDataResposne>>> =
        flow {
            val response = safeApiCall {
                api.getAnalyticData()
            }
            when (response) {
                is Resource.Success -> {
                    emit(Resource.Success(response.value))
                }

                is Resource.Failure -> emit(response)
                is Resource.Loading -> emit(Resource.Loading)
            }
        }
}