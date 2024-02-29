package com.example.projectm.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectm.utils.resource.Resource
import com.example.projectm.data.models.GetAnalyticDataResposne
import com.example.projectm.data.models.GetBuildingDataResponseItem
import com.example.projectm.repository.MainRepository
import com.example.projectm.utils.constants.CurrencyUtils.Companion.toDollarFormat
import com.example.projectm.utils.constants.DropdownType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
):ViewModel(){

    private val buildingList: MutableStateFlow<List<GetBuildingDataResponseItem?>> = MutableStateFlow(listOf())
    private val analyticList: MutableStateFlow<List<GetAnalyticDataResposne?>> = MutableStateFlow(listOf())

    private val calculationCache = mutableMapOf<DropdownType, MutableMap<String, String>>()

    private val _results = MutableStateFlow<Map<DropdownType, Map<String, String>>>(emptyMap())
    val results: StateFlow<Map<DropdownType, Map<String, String>>> = _results

    val buildingName = mutableStateOf("")

    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(true)

    init {
        getBuildings()
    }

    private fun getBuildings() {
      viewModelScope.launch{
          val buildingListResponse = mainRepository.fetchBuildings()
          buildingListResponse.collect{
              withContext(Dispatchers.Main){
                  when(it){
                      is Resource.Success ->{
                          buildingList.value = it.value
                          getAnalytics()
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

    private fun getAnalytics() {
        viewModelScope.launch{
            val analyticsListResponse = mainRepository.fetchAnalytics()
            analyticsListResponse.collect{
                withContext(Dispatchers.Main){
                    when(it){
                        is Resource.Success ->{
                            analyticList.value = it.value
                            setValues()
                            loadError.value = ""
                            isLoading.value = false
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

    private fun updateResultsState() {
        _results.value = calculationCache.mapValues { (_, value) -> value.toMap() }
    }

    private suspend fun setValues() {

        calculationCache.clear()

        coroutineScope {
            val buildingDeferred = async { processBuildingList() }
            val analyticDeferred = async { processAnalyticList() }
            val mostTotalPurchaseDeferred = async { calculateMostTotalPurchases()}


            val buildingResult = buildingDeferred.await()
            val analyticResult = analyticDeferred.await()
            val mostTotalPurchaseResult = mostTotalPurchaseDeferred.await()

            calculationCache.putAll(buildingResult)
            calculationCache.putAll(analyticResult)
            buildingName.value = mostTotalPurchaseResult
        }
        updateResultsState()
    }

    private fun processBuildingList(): Map<DropdownType, MutableMap<String, String>> {
        val buildingCache = mutableMapOf<DropdownType, MutableMap<String, String>>()

        buildingList.value.forEach { building ->
            building?.country?.let { country ->
                buildingCache.getOrPut(DropdownType.COUNTRY) { mutableMapOf() }[country] = ""
            }
            building?.state?.let { state ->
                buildingCache.getOrPut(DropdownType.STATE) { mutableMapOf() }[state] = ""
            }
        }

        return buildingCache
    }

    private fun processAnalyticList(): Map<DropdownType, MutableMap<String, String>> {

        val analyticCache = mutableMapOf<DropdownType, MutableMap<String, String>>()

        analyticList.value.forEach { analytic ->
            analytic?.manufacturer?.let { manufacturer ->
                analyticCache.getOrPut(DropdownType.MANUFACTURER) { mutableMapOf() }[manufacturer] = ""
            }
            analytic?.usageStatistics?.sessionInfos?.forEach { sessionInfo ->
                sessionInfo.purchases.forEach { purchase ->
                    purchase.itemCategoryId?.let { categoryId ->
                        analyticCache.getOrPut(DropdownType.CATEGORY) { mutableMapOf() }[categoryId.toString()] = ""
                    }
                    purchase.itemId?.let { itemId ->
                        analyticCache.getOrPut(DropdownType.ITEM) { mutableMapOf() }[itemId.toString()] = ""
                    }
                }
            }
        }
        return analyticCache
    }


    private fun calculateResultForSelection(type: DropdownType, item: String) {
        viewModelScope.launch {
            val result = when (type) {
                DropdownType.MANUFACTURER -> calculateManufacturerResult(item)
                DropdownType.COUNTRY -> calculateCountryResult(item)
                DropdownType.STATE -> calculateStateResult(item)
                DropdownType.CATEGORY -> calculateCategoryResult(item)
                DropdownType.ITEM -> calculateItemResult(item)
            }

            val currentMapForType = calculationCache[type] ?: mutableMapOf()
            currentMapForType[item] = result
            calculationCache[type] = currentMapForType

            updateResultsState()
        }
    }


    private fun calculateManufacturerResult(manufacturer:String):String{
        return analyticList.value
                .filter { it?.manufacturer == manufacturer }
            .flatMap { it?.usageStatistics?.sessionInfos!! }
            .flatMap { it.purchases }
            .sumOf {it.cost!! }.toDollarFormat()
    }
    private fun calculateCountryResult(country:String):String{
        val buildingIdsInCountry = buildingList.value
            .filter { it?.country == country }
            .map { it?.buildingId }
            .toSet()

       return analyticList.value
            .flatMap { it?.usageStatistics?.sessionInfos!! }
            .filter { sessionInfo -> buildingIdsInCountry.contains(sessionInfo.buildingId) }
            .flatMap { it.purchases }
            .sumOf { it.cost!! }.toDollarFormat()
    }
    private fun calculateStateResult(state:String):String{
        val buildingIdsInState = buildingList.value
            .filter { it?.state == state }
            .map { it?.buildingId }
            .toSet()
        return analyticList.value
            .flatMap { it?.usageStatistics?.sessionInfos!! }
            .filter { sessionInfo -> buildingIdsInState.contains(sessionInfo.buildingId) }
            .flatMap { it.purchases }
            .sumOf { it.cost!! }.toDollarFormat()
    }
    private fun calculateCategoryResult(category:String):String{
        return analyticList.value
                .flatMap { it?.usageStatistics?.sessionInfos!! }
            .flatMap { it.purchases }
            .filter { it.itemCategoryId == category.toInt() }
            .sumOf { it.cost!! }.toDollarFormat()
    }

    private fun calculateItemResult(item:String):String{
          return analyticList.value
            .flatMap { it?.usageStatistics?.sessionInfos!! }
            .flatMap { it.purchases }
            .count { it.itemId == item.toInt() }.toString()
    }

    private fun calculateMostTotalPurchases(): String {
        val totalCostsByBuilding = analyticList.value
            .flatMap { analyticData ->
                analyticData?.usageStatistics!!.sessionInfos.flatMap { sessionInfo ->
                    sessionInfo.purchases.map { purchase ->
                        sessionInfo.buildingId to purchase.cost
                    }
                }
            }
            .groupBy { it.first }  // Group by buildingId
            .mapValues { (_, pairs) -> pairs.sumOf { it.second!! } }

        val highestCostBuildingId =
            totalCostsByBuilding.maxByOrNull { it.value }?.key ?: return "N/A"

        return buildingList.value.find { it?.buildingId == highestCostBuildingId }?.buildingName
            ?: "Unknown Building"
    }

    fun onDropdownSelected(type: DropdownType, item: String) {
        calculateResultForSelection(type, item)
    }
}