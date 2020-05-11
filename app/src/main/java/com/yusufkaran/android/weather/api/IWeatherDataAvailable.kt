

package com.yusufkaran.android.weather.api

import com.yusufkaran.android.weather.model.WeatherData

interface IWeatherDataAvailable {

  /**
   * Yeni hava durumu verileri olduğunda tetiklenir.
   * Belirli bir konum için hava durumu ile ilgili tüm bilgileri içerir - sıcaklık, rüzgar, nem, vb.
   * etc.
   *
   * @see WeatherData
   */
  fun onNewWeatherDataAvailable(data: WeatherData)

  /**
   * OpenWeather sunucularına bağlanmakta sıkıntı olduğu zaman tetiklenir.
   */
  fun onNewWeatherDataUnavailable()
}