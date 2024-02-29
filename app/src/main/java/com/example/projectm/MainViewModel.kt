package com.example.projectm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neko.utils.resource.Resource
import com.example.projectm.data.models.GetBuildingDataResponseItem
import com.example.projectm.data.models.SessionInfos
import com.example.projectm.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val buildingsRepository: MainRepository
):ViewModel(){

    init {
        getBuildings()
    }

    val buildingList: MutableStateFlow<List<GetBuildingDataResponseItem?>> = MutableStateFlow(listOf())
    val sessionInfos: MutableStateFlow<List<SessionInfos?>> = MutableStateFlow(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)

    fun getBuildings() {
      viewModelScope.launch{
          val catListResponse = buildingsRepository.fetchBuildings()
          catListResponse.collect{
              withContext(Dispatchers.Main){
                  when(it){
                      is Resource.Success ->{
                          val building = it.value.getBuildingDataResponse
                          loadError.value = ""
                          isLoading.value = false
                          buildingList.value = building?.toList()!!
                      }
                      is Resource.Failure ->{
                          loadError.value = it.errorMsg.toString()
                          isLoading.value = false
                      }
                      is Resource.Loading ->{
                          isLoading.value = true
                      }
                  }
              }
          }
        }
    }

    fun getAnalytics() {
        viewModelScope.launch{
            val catListResponse = buildingsRepository.fetchAnalytics()
            catListResponse.collect{
                withContext(Dispatchers.Main){
                    when(it){
                        is Resource.Success ->{
                            val sessionInfo = it.value.usageStatistics?.sessionInfos
                            loadError.value = ""
                            isLoading.value = false
                            sessionInfos.value = sessionInfo?.toList()!!
                        }
                        is Resource.Failure ->{
                            loadError.value = it.errorMsg.toString()
                            isLoading.value = false
                        }
                        is Resource.Loading ->{
                            isLoading.value = true
                        }
                    }
                }
            }
        }
    }

}