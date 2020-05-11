

package com.yusufkaran.android.weather.model

import com.google.gson.annotations.SerializedName

data class Main(val temp: String, val pressure: String, val humidity: String,
    @SerializedName("temp_min") val tempMin: String,
    @SerializedName("temp_max") val tempMax: String)