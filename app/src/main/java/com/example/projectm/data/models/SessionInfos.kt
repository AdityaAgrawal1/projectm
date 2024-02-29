package com.example.projectm.data.models

import com.google.gson.annotations.SerializedName

data class SessionInfos(

    @SerializedName("building_id") var buildingId: Int? = null,
    @SerializedName("purchases") var purchases: ArrayList<Purchases> = arrayListOf()

)