package com.svape.masterunalapp.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapboxScreen(navController: NavHostController) {
    var currentStyleUrl by remember { mutableStateOf("mapbox://styles/mapbox/streets-v12") }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(14.0)
            center(Point.fromLngLat(-74.0836, 4.6370))
            pitch(0.0)
            bearing(0.0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa Interactivo - UNAL") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapViewportState = mapViewportState,
                style = {
                    MapStyle(style = currentStyleUrl)
                }
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SmallFloatingActionButton(
                            onClick = {
                                currentStyleUrl = "mapbox://styles/mapbox/streets-v12"
                            },
                            containerColor = if (currentStyleUrl.contains("streets"))
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surface
                        ) {
                            Text("⚑")
                        }

                        SmallFloatingActionButton(
                            onClick = {
                                currentStyleUrl = "mapbox://styles/mapbox/satellite-streets-v12"
                            },
                            containerColor = if (currentStyleUrl.contains("satellite"))
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surface
                        ) {
                            Text("✈")
                        }

                        SmallFloatingActionButton(
                            onClick = {
                                currentStyleUrl = "mapbox://styles/mapbox/dark-v11"
                            },
                            containerColor = if (currentStyleUrl.contains("dark"))
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surface
                        ) {
                            Text("☽")
                        }

                        SmallFloatingActionButton(
                            onClick = {
                                currentStyleUrl = "mapbox://styles/mapbox/light-v11"
                            },
                            containerColor = if (currentStyleUrl.contains("light"))
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surface
                        ) {
                            Text("☼")
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "⛫ Universidad Nacional de Colombia",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Text(
                        text = "Sede Bogotá",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Lat: 4.6370, Lon: -74.0836",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            ExtendedFloatingActionButton(
                onClick = {
                    mapViewportState.setCameraOptions {
                        zoom(14.0)
                        center(Point.fromLngLat(-74.0836, 4.6370))
                        pitch(0.0)
                        bearing(0.0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("⛫ Centrar UNAL")
            }
        }
    }
}