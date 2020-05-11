
package com.yusufkaran.android.weather

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yusufkaran.android.weather.api.IWeatherDataAvailable
import com.yusufkaran.android.weather.api.OpenWeatherAPI
import com.yusufkaran.android.weather.model.WeatherData
import com.yusufkaran.android.weather.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main Screen
 */
private const val TAG = "MainActivity"
private const val REQUEST_PERMISSIONS = 200

class MainActivity : AppCompatActivity(), IWeatherDataAvailable {

  private lateinit var fusedLocationClient: FusedLocationProviderClient

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.AppTheme)

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    btn_request.setOnClickListener {
      requestLocationPermission()
    }

    btn_retry.setOnClickListener {
      getLastKnownLocation()
    }

    if (!Utils.hasLocationPermission(baseContext)) {
      requestLocationPermission()
    }
  }

  override fun onResume() {
    super.onResume()

    if (Utils.hasLocationPermission(baseContext)) {
      btn_request.visibility = View.GONE
      getLastKnownLocation()
    }
  }

  override fun onRequestPermissionsResult(code: Int, perm: Array<out String>, result: IntArray) {
    if (code == REQUEST_PERMISSIONS) {
      if (result[0] == PackageManager.PERMISSION_DENIED) {
        btn_request.visibility = View.VISIBLE
      } else {
        btn_request.visibility = View.GONE
        getLastKnownLocation()
      }
    }
    super.onRequestPermissionsResult(code, perm, result)
  }

  private fun requestLocationPermission() {
    ActivityCompat.requestPermissions(this,
        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSIONS)
  }

  private fun getLastKnownLocation() {
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
          Log.d(TAG, "Last known location=$location")

          if (location == null) {
            if (Utils.isLocationServiceEnabled(baseContext)) {
              tv_description.text = getString(R.string.no_location)
            } else {
              tv_description.text = getString(R.string.not_available)
            }

            btn_retry.visibility = View.VISIBLE
            return@addOnSuccessListener
          }

          btn_retry.visibility = View.GONE

          val weather = OpenWeatherAPI()
          weather.getForecastInformation(location.latitude, location.longitude, this)
        }
        .addOnFailureListener { exception ->
          Log.w(TAG, "Unable to get last known location")

          when (exception) {
            is SecurityException -> {
              tv_description.text = getString(R.string.no_permission)
            }
            else -> {
              tv_description.text = getString(R.string.not_available)
            }
          }
        }
  }

  //region IWeatherDataAvailable

  override fun onNewWeatherDataUnavailable() {
    Log.w(TAG, "No data available for current location")
    tv_temperature.text = getString(R.string.default_temp)
    tv_description.text = getString(R.string.no_connection)
  }

  override fun onNewWeatherDataAvailable(data: WeatherData) {
    Log.w(TAG, "On new data available")

    tv_location.text = data.name
    tv_date.text = Utils.getCurrentDate()
    tv_temperature.text = getString(R.string.temp_celsius, Utils.formatTemp(data.main.temp))
    tv_description.text = data.weather[0].description

    val minTemp = Utils.formatTemp(data.main.tempMin)
    val maxTemp = Utils.formatTemp(data.main.tempMax)
    tv_delta.text = getString(R.string.temp_delta, minTemp, maxTemp)
    tv_wind.text = getString(R.string.temp_wind, Utils.getWindInKmh(data.wind.speed))
    tv_humidity.text = getString(R.string.temp_humidity, data.main.humidity)
  }

  //endregion
}
