package com.svape.masterunalapp.ui.viewmodel

import android.Manifest
import android.app.Application
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.mapbox.geojson.Point
import com.svape.masterunalapp.data.api.Element
import com.svape.masterunalapp.data.api.MockPoiService
import com.svape.masterunalapp.data.api.OverpassApiService
import com.svape.masterunalapp.data.api.OverpassQueryBuilder
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

val Application.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class MapState(
    val currentLocation: Point? = null,
    val pointsOfInterest: List<PointOfInterest> = emptyList(),
    val searchRadius: Float = 1.0f,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedPoiTypes: Set<String> = setOf("hospital", "tourism", "restaurant")
)

data class PointOfInterest(
    val id: Long,
    val name: String,
    val type: String,
    val location: Point,
    val address: String? = null,
    val phone: String? = null,
    val website: String? = null,
    val openingHours: String? = null
)

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    private val overpassApi = OverpassApiService.create()

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    companion object {
        val SEARCH_RADIUS_KEY = floatPreferencesKey("search_radius")
        val POI_TYPES_KEY = stringPreferencesKey("poi_types")
    }

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            getApplication<Application>().dataStore.data.collect { preferences ->
                val radius = preferences[SEARCH_RADIUS_KEY] ?: 1.0f
                val poiTypes = preferences[POI_TYPES_KEY]?.split(",")?.toSet()
                    ?: setOf("hospital", "tourism", "restaurant")

                _state.update {
                    it.copy(
                        searchRadius = radius,
                        selectedPoiTypes = poiTypes
                    )
                }
            }
        }
    }

    fun updateSearchRadius(radius: Float) {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { preferences ->
                preferences[SEARCH_RADIUS_KEY] = radius
            }
            _state.update { it.copy(searchRadius = radius) }
            state.value.currentLocation?.let {
                searchNearbyPOIs(it.latitude(), it.longitude())
            }
        }
    }

    fun updatePoiTypes(types: Set<String>) {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { preferences ->
                preferences[POI_TYPES_KEY] = types.joinToString(",")
            }
            _state.update { it.copy(selectedPoiTypes = types) }
            state.value.currentLocation?.let {
                searchNearbyPOIs(it.latitude(), it.longitude())
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getCurrentLocation(): Point? {
        return try {
            _state.update { it.copy(isLoading = true, error = null) }

            val cancellationTokenSource = CancellationTokenSource()
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()

            if (location != null) {
                val point = Point.fromLngLat(location.longitude, location.latitude)
                _state.update { state ->
                    state.copy(
                        currentLocation = point,
                        isLoading = false
                    )
                }

                searchNearbyPOIs(location.latitude, location.longitude)

                point
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "No se pudo obtener la ubicación"
                    )
                }
                null
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = "Error al obtener ubicación: ${e.message}"
                )
            }
            null
        }
    }


    private suspend fun searchNearbyPOIs(lat: Double, lon: Double) {
        try {
            _state.update { it.copy(isLoading = true) }

            val useMockData = true

            val pois = if (useMockData) {
                MockPoiService.generateMockPOIs(
                    centerLat = lat,
                    centerLon = lon,
                    radiusKm = state.value.searchRadius,
                    types = state.value.selectedPoiTypes
                )
            } else {
                val poiList = mutableListOf<PointOfInterest>()

                if (state.value.selectedPoiTypes.contains("hospital")) {
                    val hospitalQuery = OverpassQueryBuilder.buildSpecificPOIQuery(
                        lat, lon, state.value.searchRadius, "amenity", "hospital"
                    )
                    val hospitals = overpassApi.getNearbyPOIs(hospitalQuery)
                    poiList.addAll(hospitals.elements.mapToPOIs("hospital"))
                }

                if (state.value.selectedPoiTypes.contains("tourism")) {
                    val tourismQuery = OverpassQueryBuilder.buildPOIQuery(
                        lat, lon, state.value.searchRadius, listOf("tourism")
                    )
                    val tourism = overpassApi.getNearbyPOIs(tourismQuery)
                    poiList.addAll(tourism.elements.mapToPOIs("tourism"))
                }

                if (state.value.selectedPoiTypes.contains("restaurant")) {
                    val restaurantQuery = OverpassQueryBuilder.buildSpecificPOIQuery(
                        lat, lon, state.value.searchRadius, "amenity", "restaurant"
                    )
                    val restaurants = overpassApi.getNearbyPOIs(restaurantQuery)
                    poiList.addAll(restaurants.elements.mapToPOIs("restaurant"))
                }

                poiList
            }

            _state.update {
                it.copy(
                    pointsOfInterest = pois,
                    isLoading = false
                )
            }

        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = "Error al buscar lugares: ${e.message}"
                )
            }
        }
    }

    private fun List<Element>.mapToPOIs(type: String): List<PointOfInterest> {
        return this.mapNotNull { element ->
            if (element.lat != null && element.lon != null) {
                PointOfInterest(
                    id = element.id,
                    name = element.tags?.name ?: "Sin nombre",
                    type = type,
                    location = Point.fromLngLat(element.lon, element.lat),
                    address = buildAddress(element.tags),
                    phone = element.tags?.phone,
                    website = element.tags?.website,
                    openingHours = element.tags?.opening_hours
                )
            } else null
        }
    }

    private fun buildAddress(tags: com.svape.masterunalapp.data.api.Tags?): String? {
        if (tags == null) return null
        val parts = listOfNotNull(tags.street, tags.houseNumber)
        return if (parts.isNotEmpty()) parts.joinToString(" ") else null
    }
}