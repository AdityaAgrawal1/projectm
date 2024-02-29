package com.example.projectm.data.models

import com.google.gson.annotations.SerializedName


data class UsageStatistics(

    @SerializedName("session_infos")
    var sessionInfos: ArrayList<SessionInfos> = arrayListOf()

)