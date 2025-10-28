package com.svape.masterunalapp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationResult {
        return try {
            if (!hasLocationPermission()) {
                return LocationResult.Error("No hay permisos de ubicación")
            }

            val cancellationTokenSource = CancellationTokenSource()

            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()

            if (location != null) {
                LocationResult.Success(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            } else {
                val lastLocation = fusedLocationClient.lastLocation.await()
                if (lastLocation != null) {
                    LocationResult.Success(
                        latitude = lastLocation.latitude,
                        longitude = lastLocation.longitude
                    )
                } else {
                    LocationResult.Error("No se pudo obtener la ubicación")
                }
            }
        } catch (e: SecurityException) {
            LocationResult.Error("Error de permisos: ${e.message}")
        } catch (e: Exception) {
            LocationResult.Error("Error al obtener ubicación: ${e.message}")
        }
    }

    sealed class LocationResult {
        data class Success(val latitude: Double, val longitude: Double) : LocationResult()
        data class Error(val message: String) : LocationResult()
    }
}