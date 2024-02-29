package com.example.projectm.data.models

import com.google.gson.annotations.SerializedName

data class GetBuildingDataResponse(

	@field:SerializedName("GetBuildingDataResponse")
	val getBuildingDataResponse: List<GetBuildingDataResponseItem?>? = null
)

data class GetBuildingDataResponseItem(

	@field:SerializedName("building_id")
	val buildingId: Int? = null,

	@field:SerializedName("building_name")
	val buildingName: String? = null,

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("state")
	val state: String? = null
)
