package com.example.services

import android.util.Log
import com.example.data.WeatherRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OpenMeteoWeatherService : WeatherService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val cityCoordinates = mapOf(
        "coimbatore" to Pair(11.0168, 76.9558),
        "pollachi" to Pair(10.6609, 77.0048),
        "mettupalayam" to Pair(11.3000, 76.9500),
        "salem" to Pair(11.6643, 78.1460),
        "madurai" to Pair(9.9252, 78.1198),
        "chennai" to Pair(13.0827, 80.2707),
        "bengaluru" to Pair(12.9716, 77.5946),
        "hyderabad" to Pair(17.3850, 78.4867),
        "pune" to Pair(18.5204, 73.8567),
        "delhi" to Pair(28.6139, 77.2090),
        "mumbai" to Pair(19.0760, 72.8777),
        "lucknow" to Pair(26.8467, 80.9462)
    )

    override suspend fun getCurrentWeather(location: String): WeatherRecord = withContext(Dispatchers.IO) {
        val (lat, lon) = getCoordinatesForLocation(location)
        try {
            val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,relative_humidity_2m,precipitation,weather_code,wind_speed_10m&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max&timezone=auto"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val json = JSONObject(responseBody)
                val current = json.getJSONObject("current")
                val daily = json.optJSONObject("daily")

                val temp = current.optDouble("temperature_2m", 28.0).toInt()
                val humidity = current.optDouble("relative_humidity_2m", 78.0).toInt()
                val windSpeed = current.optDouble("wind_speed_10m", 12.0).toInt()
                val weatherCode = current.optInt("weather_code", 3)
                val rainChance = daily?.optJSONArray("precipitation_probability_max")?.optInt(0, 60) ?: 60

                val (conditionTitle, sceneType) = mapWeatherCode(weatherCode, humidity, rainChance)
                val impact = buildImpactNotice(temp, humidity, rainChance, conditionTitle)
                val recommendation = buildRecommendation(humidity, rainChance)

                return@withContext WeatherRecord(
                    dayLabel = "Today",
                    dateFormatted = "Live Weather • " + location.take(20),
                    temperatureC = temp,
                    humidityPercent = humidity,
                    conditionTitle = conditionTitle,
                    forecastText = if (rainChance > 50) "Rain Expected ($rainChance% chance)" else "Stable Conditions ($rainChance% rain chance)",
                    rainfallChance = rainChance,
                    windSpeedKmh = windSpeed,
                    cropImpactNotice = impact,
                    recommendation = recommendation,
                    sceneType = sceneType
                )
            }
        } catch (e: Exception) {
            Log.w("OpenMeteoWeatherService", "Failed to fetch live weather, returning fallback: ${e.message}")
        }

        // Fallback if network is unavailable
        MockWeatherService().getCurrentWeather(location)
    }

    override suspend fun get7DayForecast(location: String): List<WeatherRecord> = withContext(Dispatchers.IO) {
        val (lat, lon) = getCoordinatesForLocation(location)
        try {
            val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max&timezone=auto"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val json = JSONObject(responseBody)
                val daily = json.optJSONObject("daily")
                if (daily != null) {
                    val dates = daily.optJSONArray("time")
                    val codes = daily.optJSONArray("weather_code")
                    val maxTemps = daily.optJSONArray("temperature_2m_max")
                    val rainChances = daily.optJSONArray("precipitation_probability_max")

                    val records = mutableListOf<WeatherRecord>()
                    val dayLabels = listOf("Today", "Tomorrow", "Day 3", "Day 4", "Day 5", "Day 6", "Day 7")

                    if (dates != null) {
                        for (i in 0 until minOf(dates.length(), 7)) {
                            val dateStr = dates.optString(i, "Day ${i+1}")
                            val code = codes?.optInt(i, 3) ?: 3
                            val tempMax = maxTemps?.optDouble(i, 28.0)?.toInt() ?: 28
                            val rainProb = rainChances?.optInt(i, 30) ?: 30
                            val (title, scene) = mapWeatherCode(code, 65, rainProb)

                            records.add(
                                WeatherRecord(
                                    dayLabel = dayLabels.getOrElse(i) { "Day ${i+1}" },
                                    dateFormatted = dateStr,
                                    temperatureC = tempMax,
                                    humidityPercent = if (rainProb > 50) 80 else 65,
                                    conditionTitle = title,
                                    forecastText = if (rainProb > 50) "Rain Likely ($rainProb%)" else "Clear / Mild ($rainProb%)",
                                    rainfallChance = rainProb,
                                    windSpeedKmh = 12,
                                    cropImpactNotice = buildImpactNotice(tempMax, if (rainProb > 50) 80 else 65, rainProb, title),
                                    recommendation = buildRecommendation(if (rainProb > 50) 80 else 65, rainProb),
                                    sceneType = scene
                                )
                            )
                        }
                        if (records.isNotEmpty()) return@withContext records
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("OpenMeteoWeatherService", "Failed 7-day forecast: ${e.message}")
        }

        MockWeatherService().get7DayForecast(location)
    }

    private fun getCoordinatesForLocation(location: String): Pair<Double, Double> {
        val lower = location.lowercase()
        for ((key, coords) in cityCoordinates) {
            if (lower.contains(key)) return coords
        }
        // Try regex extract lat/long if formatted like "11.0168° N, 76.9558° E"
        val regex = """([\d.]+)[^\d.]+([\d.]+)""".toRegex()
        val match = regex.find(location)
        if (match != null) {
            val lat = match.groupValues[1].toDoubleOrNull()
            val lon = match.groupValues[2].toDoubleOrNull()
            if (lat != null && lon != null) return Pair(lat, lon)
        }
        // Default to Coimbatore coordinates
        return Pair(11.0168, 76.9558)
    }

    private fun mapWeatherCode(code: Int, humidity: Int, rainChance: Int): Pair<String, String> {
        return when (code) {
            0 -> "Clear Sunny Sky" to "sunny"
            1, 2 -> "Partly Cloudy" to "sunny"
            3 -> if (humidity > 75) "High Humidity & Clouds" to "humidity" else "Overcast" to "humidity"
            45, 48 -> "Foggy & Damp" to "humidity"
            51, 53, 55, 61, 63, 65, 80, 81 -> "Scattered Rainfall" to "rain"
            95, 96, 99 -> "Thunderstorm & Rain" to "rain"
            else -> if (rainChance > 50) "Rain Expected" to "rain" else "Humid Weather" to "humidity"
        }
    }

    private fun buildImpactNotice(temp: Int, humidity: Int, rainChance: Int, condition: String): String {
        return when {
            humidity > 75 -> "High humidity ($humidity%) and warm temperature ($temp°C) promote foliar condensation, increasing fungal spore development."
            rainChance > 60 -> "High precipitation probability ($rainChance%) expected. Field drainage furrows should be cleared to prevent waterlogging."
            temp > 35 -> "Elevated temperature ($temp°C) increases soil evapotranspiration; ensure drip irrigation schedules meet water demand."
            else -> "Stable weather conditions ($temp°C, $humidity% humidity) favor steady crop growth and nutrient assimilation."
        }
    }

    private fun buildRecommendation(humidity: Int, rainChance: Int): String {
        return when {
            humidity > 75 -> "Prune bottom soil-touching leaves to maximize airflow; spray 5% NSKE during early morning."
            rainChance > 60 -> "Clear drainage outlets, suspend evening sprinkler/foliar sprays, and hold off fertilizer application until rain passes."
            else -> "Maintain standard drip fertigation schedule and inspect foliage weekly."
        }
    }
}
