package com.example.projectm.data.models

import com.google.gson.annotations.SerializedName


data class GetAnalyticDataResposne(

    @SerializedName("manufacturer")
    var manufacturer: String? = null,

    @SerializedName("market_name")
    var marketName: String? = null,

    @SerializedName("codename")
    var codename: String? = null,

    @SerializedName("model")
    var model: String? = null,

    @SerializedName("usage_statistics")
    var usageStatistics: UsageStatistics? = UsageStatistics()

)