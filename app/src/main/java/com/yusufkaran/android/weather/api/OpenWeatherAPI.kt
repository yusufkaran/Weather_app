

package com.yusufkaran.android.weather.api

import android.util.Log
import com.google.gson.GsonBuilder
import com.yusufkaran.android.weather.model.WeatherData
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Hava durumu ile ilgili bilgileri aşağıdaki siteden alıyoruz
 * https://openweathermap.org/
 *
 * API anahtarını buradan alabilirsin: https://openweathermap.org/appid
 */

private const val TAG = "RestAPI"
private const val API_KEY = "" //OpenWeatherMap den aldığın API anahtarını gir
private const val URL = "http://api.openweathermap.org/"
private const val CNT = "10"
private const val UNITS = "metric"
class OpenWeatherAPI {

  fun getForecastInformation(lat: Double, lon: Double, listener: IWeatherDataAvailable) {
    val retrofit = Retrofit.Builder()
        .client(OkHttpClient.Builder().build())
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()

    val weatherAPI = retrofit.create(IWeatherAPI::class.java)
    weatherAPI.getWeatherInfo(lat, lon, CNT, UNITS,
        API_KEY).enqueue(object : Callback<WeatherData?> {

      override fun onResponse(call: Call<WeatherData?>, response: Response<WeatherData?>) {
        Log.d(TAG, "response=${response.message()} | code=${response.code()}")
        if (response.body() == null) {
          listener.onNewWeatherDataUnavailable()
        } else {
          listener.onNewWeatherDataAvailable(response.body()!!)
        }
      }

      override fun onFailure(call: Call<WeatherData?>, t: Throwable) {
        Log.d(TAG, "failure=${t.localizedMessage}")
        listener.onNewWeatherDataUnavailable()
      }
    })
  }
}