

package com.yusufkaran.android.weather.api

import com.yusufkaran.android.weather.model.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IWeatherAPI {

  @GET("data/2.5/weather")
  fun getWeatherInfo(@Query("lat") latitude: Double,
      @Query("lon") longitude: Double,
      @Query("cnt") cnt: String,
      @Query("units") units: String,
      @Query("appid") appId: String): Call<WeatherData?>
}