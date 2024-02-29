package com.example.projectm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AnalyticsViewModel : ViewModel() {
    private val _manufacturers = MutableLiveData<List<String>>()
    val manufacturers: LiveData<List<String>> = _manufacturers

    var selectedManufacturer = "";

    fun onManufacturerSelected(manufacturer:String){}

}