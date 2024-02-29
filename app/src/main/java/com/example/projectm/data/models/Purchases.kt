package com.example.projectm.data.models

import com.google.gson.annotations.SerializedName


data class Purchases(

    @SerializedName("item_id") var itemId: Int? = null,
    @SerializedName("item_category_id") var itemCategoryId: Int? = null,
    @SerializedName("cost") var cost: Double? = null

)