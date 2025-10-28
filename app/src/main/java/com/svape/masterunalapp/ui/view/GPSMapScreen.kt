package com.svape.masterunalapp.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.*
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotationGroup
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationGroup
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.svape.masterunalapp.ui.viewmodel.MapViewModel
import com.svape.masterunalapp.ui.viewmodel.PointOfInterest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun GPSMapScreen(
    navController: NavHostController,
    viewModel: MapViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current




    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    val mapViewportState = rememberMapViewportState()

    var showSettings by remember { mutableStateOf(false) }
    var showPoiList by remember { mutableStateOf(false) }
    var selectedPoi by remember { mutableStateOf<PointOfInterest?>(null) }

    LaunchedEffect(state.currentLocation) {
        state.currentLocation?.let { location ->
            mapViewportState.setCameraOptions {
                center(location)
                zoom(14.0)
            }
        }
    }

    LaunchedEffect(locationPermissionState.allPermissionsGranted) {

        if (locationPermissionState.allPermissionsGranted) {
            val hasFine = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val hasCoarse = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasFine || hasCoarse) {
                viewModel.getCurrentLocation()
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa GPS - Puntos de Interés") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, "Configuración")
                    }
                    IconButton(onClick = { showPoiList = true }) {
                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Text("${state.pointsOfInterest.size}")
                        }
                        Icon(Icons.Default.List, "Lista de POIs")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!locationPermissionState.allPermissionsGranted) {
                PermissionRequestScreen(
                    onRequestPermission = {
                        locationPermissionState.launchMultiplePermissionRequest()
                    }
                )
            } else {
                MapboxMap(
                    modifier = Modifier.fillMaxSize(),
                    mapViewportState = mapViewportState,
                    style = { MapStyle(style = "mapbox://styles/mapbox/streets-v12") }
                ) {
                    state.currentLocation?.let { location ->
                        CircleAnnotationGroup(
                            annotations = listOf(
                                CircleAnnotationOptions()
                                    .withPoint(location)
                                    .withCircleColor(Color.Blue.toArgb())
                                    .withCircleRadius(10.0)
                                    .withCircleStrokeColor(Color.White.toArgb())
                                    .withCircleStrokeWidth(2.0),
                                CircleAnnotationOptions()
                                    .withPoint(location)
                                    .withCircleColor(Color.Blue.copy(alpha = 0.1f).toArgb())
                                    .withCircleRadius(state.searchRadius * 10.0)
                                    .withCircleStrokeColor(Color.Blue.copy(alpha = 0.3f).toArgb())
                                    .withCircleStrokeWidth(1.0)
                            )
                        )
                    }

                    PointAnnotationGroup(
                        annotations = state.pointsOfInterest.map { poi ->
                            PointAnnotationOptions()
                                .withPoint(poi.location)
                                .withIconImage(getIconForPoiType(poi.type))
                                .withTextField(poi.name)
                                .withTextSize(12.0)
                                .withTextHaloWidth(1.0)
                                .withTextHaloColor("#FFFFFF")
                        },
                        onClick = { annotation ->
                            val poi = state.pointsOfInterest.find {
                                it.location == annotation.point
                            }
                            selectedPoi = poi
                            true
                        }
                    )
                }

                Card(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Radio: ${state.searchRadius} km", style = MaterialTheme.typography.bodyMedium)
                        Text("POIs encontrados: ${state.pointsOfInterest.size}", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                val location = viewModel.getCurrentLocation()
                                location?.let {
                                    mapViewportState.setCameraOptions {
                                        center(it)
                                        zoom(14.0)
                                    }
                                }
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Icon(Icons.Default.LocationOn, contentDescription = "Mi ubicación")
                        }
                    }
                }

                state.error?.let { error ->
                    Snackbar(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        action = {
                            TextButton(onClick = { }) { Text("OK") }
                        }
                    ) {
                        Text(error)
                    }
                }
            }
        }
    }

    if (showSettings) {
        SettingsDialog(
            currentRadius = state.searchRadius,
            selectedTypes = state.selectedPoiTypes,
            onRadiusChange = { viewModel.updateSearchRadius(it) },
            onTypesChange = { viewModel.updatePoiTypes(it) },
            onDismiss = { showSettings = false }
        )
    }

    if (showPoiList) {
        PoiListDialog(
            pois = state.pointsOfInterest,
            onSelectPoi = { poi ->
                mapViewportState.setCameraOptions {
                    center(poi.location)
                    zoom(16.0)
                }
                selectedPoi = poi
                showPoiList = false
            },
            onDismiss = { showPoiList = false }
        )
    }

    selectedPoi?.let { poi ->
        PoiDetailsDialog(poi = poi, onDismiss = { selectedPoi = null })
    }
}


@Composable
fun PermissionRequestScreen(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Permisos de ubicación", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Esta aplicación necesita acceso a tu ubicación para mostrar puntos de interés cercanos.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRequestPermission, modifier = Modifier.fillMaxWidth()) {
            Text("Conceder permisos")
        }
    }
}


@Composable
fun SettingsDialog(
    currentRadius: Float,
    selectedTypes: Set<String>,
    onRadiusChange: (Float) -> Unit,
    onTypesChange: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var tempRadius by remember { mutableStateOf(currentRadius) }
    var tempTypes by remember { mutableStateOf(selectedTypes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configuración") },
        text = {
            Column {
                Text("Radio de búsqueda: ${tempRadius.toInt()} km")
                Slider(
                    value = tempRadius,
                    onValueChange = { tempRadius = it },
                    valueRange = 0.5f..10f,
                    steps = 19
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Tipos de lugares:")
                val poiTypes = mapOf(
                    "hospital" to "⛟ Hospitales",
                    "tourism" to "️⛱ Turismo",
                    "restaurant" to "⛾ Restaurantes"
                )

                poiTypes.forEach { (key, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = tempTypes.contains(key),
                            onCheckedChange = { checked ->
                                tempTypes = if (checked) tempTypes + key else tempTypes - key
                            }
                        )
                        Text(label)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onRadiusChange(tempRadius)
                onTypesChange(tempTypes)
                onDismiss()
            }) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}


@Composable
fun PoiListDialog(
    pois: List<PointOfInterest>,
    onSelectPoi: (PointOfInterest) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lugares encontrados (${pois.size})") },
        text = {
            LazyColumn {
                items(pois) { poi ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        onClick = { onSelectPoi(poi) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(getEmojiForPoiType(poi.type), style = MaterialTheme.typography.headlineSmall)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(poi.name, fontWeight = FontWeight.Bold)
                                poi.address?.let {
                                    Text(it, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}


@Composable
fun PoiDetailsDialog(
    poi: PointOfInterest,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(getEmojiForPoiType(poi.type))
                Spacer(modifier = Modifier.width(8.dp))
                Text(poi.name)
            }
        },
        text = {
            Column {
                poi.address?.let {
                    Text("☞ $it")
                    Spacer(modifier = Modifier.height(4.dp))
                }
                poi.phone?.let {
                    Text("☏ $it")
                    Spacer(modifier = Modifier.height(4.dp))
                }
                poi.website?.let {
                    Text("⌨ $it", color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    "Coordenadas:\nLat: ${poi.location.latitude()}\nLon: ${poi.location.longitude()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}

fun getIconForPoiType(type: String): String {
    return when (type.lowercase()) {
        "restaurant" -> "restaurant-15"
        "hospital" -> "hospital-15"
        "tourism" -> "monument-15"
        else -> "marker-15"
    }
}

fun getEmojiForPoiType(type: String): String {
    return when (type.lowercase()) {
        "restaurant" -> "⛾"
        "hospital" -> "⛟"
        "tourism" -> "⛱"
        else -> "☯"
    }
}
