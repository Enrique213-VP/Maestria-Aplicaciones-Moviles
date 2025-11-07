package com.svape.masterunalapp.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svape.masterunalapp.data.model.HechoDelictivo
import com.svape.masterunalapp.ui.theme.MasterUnalAppTheme
import com.svape.masterunalapp.ui.viewmodel.DataCatalogViewModel
import com.svape.masterunalapp.ui.viewmodel.UiState
import java.text.SimpleDateFormat
import java.util.*

class DataCatalogActivity : ComponentActivity() {
    private val viewModel: DataCatalogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MasterUnalAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DataCatalogScreen(
                        viewModel = viewModel,
                        onBackPressed = { finish() }
                    )
                }
            }
        }
    }
}

val departamentos = listOf(
    "TODOS",
    "AMAZONAS", "ANTIOQUIA", "ARAUCA", "ATLANTICO", "BOLIVAR",
    "BOYACA", "CALDAS", "CAQUETA", "CASANARE", "CAUCA",
    "CESAR", "CHOCO", "CORDOBA", "CUNDINAMARCA", "GUAINIA",
    "GUAVIARE", "HUILA", "LA GUAJIRA", "MAGDALENA", "META",
    "NARIÑO", "NORTE DE SANTANDER", "PUTUMAYO", "QUINDIO", "RISARALDA",
    "SAN ANDRES", "SANTANDER", "SUCRE", "TOLIMA", "VALLE DEL CAUCA",
    "VAUPES", "VICHADA"
)

val municipiosPorDepartamento = mapOf(
    "CUNDINAMARCA" to listOf("TODOS", "BOGOTA", "SOACHA", "FUSAGASUGA", "FACATATIVA", "ZIPAQUIRA", "GIRARDOT", "CHIA"),
    "ANTIOQUIA" to listOf("TODOS", "MEDELLIN", "BELLO", "ITAGUI", "ENVIGADO", "APARTADO", "TURBO", "RIONEGRO"),
    "VALLE DEL CAUCA" to listOf("TODOS", "CALI", "PALMIRA", "BUENAVENTURA", "TULUA", "CARTAGO", "JAMUNDI"),
    "ATLANTICO" to listOf("TODOS", "BARRANQUILLA", "SOLEDAD", "MALAMBO", "SABANALARGA", "PUERTO COLOMBIA"),
    "SANTANDER" to listOf("TODOS", "BUCARAMANGA", "FLORIDABLANCA", "GIRON", "PIEDECUESTA", "BARRANCABERMEJA"),
    "BOLIVAR" to listOf("TODOS", "CARTAGENA", "MAGANGUE", "TURBACO", "ARJONA"),
    "TOLIMA" to listOf("TODOS", "IBAGUE", "ESPINAL", "MELGAR", "HONDA", "CHAPARRAL"),
    "NARIÑO" to listOf("TODOS", "PASTO", "TUMACO", "IPIALES", "SAMANIEGO"),
    "NORTE DE SANTANDER" to listOf("TODOS", "CUCUTA", "OCAÑA", "PAMPLONA", "VILLA DEL ROSARIO"),
    "CALDAS" to listOf("TODOS", "MANIZALES", "VILLAMARIA", "LA DORADA", "CHINCHINA"),
    "RISARALDA" to listOf("TODOS", "PEREIRA", "DOSQUEBRADAS", "LA VIRGINIA", "SANTA ROSA DE CABAL"),
    "QUINDIO" to listOf("TODOS", "ARMENIA", "CALARCA", "MONTENEGRO", "LA TEBAIDA"),
    "META" to listOf("TODOS", "VILLAVICENCIO", "ACACIAS", "GRANADA", "SAN MARTIN"),
    "CESAR" to listOf("TODOS", "VALLEDUPAR", "AGUACHICA", "BOSCONIA", "CHIMICHAGUA"),
    "MAGDALENA" to listOf("TODOS", "SANTA MARTA", "CIENAGA", "FUNDACION", "PLATO"),
    "HUILA" to listOf("TODOS", "NEIVA", "PITALITO", "GARZON", "LA PLATA"),
    "LA GUAJIRA" to listOf("TODOS", "RIOHACHA", "MAICAO", "URIBIA", "MANAURE")
)

val aniosDisponibles = listOf("TODOS") + (2003..2024).map { it.toString() }.reversed()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataCatalogScreen(
    viewModel: DataCatalogViewModel,
    onBackPressed: () -> Unit
) {
    var selectedDepartamento by remember { mutableStateOf("TODOS") }
    var selectedMunicipio by remember { mutableStateOf("TODOS") }
    var selectedAnio by remember { mutableStateOf("TODOS") }

    var expandedDepartamento by remember { mutableStateOf(false) }
    var expandedMunicipio by remember { mutableStateOf(false) }
    var expandedAnio by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val municipiosDisponibles = remember(selectedDepartamento) {
        if (selectedDepartamento == "TODOS") {
            listOf("TODOS")
        } else {
            municipiosPorDepartamento[selectedDepartamento] ?: listOf("TODOS")
        }
    }

    LaunchedEffect(selectedDepartamento) {
        if (selectedDepartamento != "TODOS" && !municipiosDisponibles.contains(selectedMunicipio)) {
            selectedMunicipio = "TODOS"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Datos Abiertos Colombia",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Consultar Hechos Delictivos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )

                    Text(
                        text = "Ministerio de Defensa Nacional",
                        fontSize = 12.sp,
                        color = Color(0xFF718096),
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    Text(
                        text = "Departamento",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4A5568),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedDepartamento,
                        onExpandedChange = { expandedDepartamento = it }
                    ) {
                        OutlinedTextField(
                            value = selectedDepartamento,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    if (expandedDepartamento) Icons.Default.KeyboardArrowUp
                                    else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedDepartamento,
                            onDismissRequest = { expandedDepartamento = false }
                        ) {
                            departamentos.forEach { depto ->
                                DropdownMenuItem(
                                    text = { Text(depto) },
                                    onClick = {
                                        selectedDepartamento = depto
                                        expandedDepartamento = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Municipio",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4A5568),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedMunicipio,
                        onExpandedChange = { expandedMunicipio = it }
                    ) {
                        OutlinedTextField(
                            value = selectedMunicipio,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    if (expandedMunicipio) Icons.Default.KeyboardArrowUp
                                    else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(),
                            enabled = selectedDepartamento != "TODOS"
                        )

                        ExposedDropdownMenu(
                            expanded = expandedMunicipio,
                            onDismissRequest = { expandedMunicipio = false }
                        ) {
                            municipiosDisponibles.forEach { muni ->
                                DropdownMenuItem(
                                    text = { Text(muni) },
                                    onClick = {
                                        selectedMunicipio = muni
                                        expandedMunicipio = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Año",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4A5568),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedAnio,
                        onExpandedChange = { expandedAnio = it }
                    ) {
                        OutlinedTextField(
                            value = selectedAnio,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    if (expandedAnio) Icons.Default.KeyboardArrowUp
                                    else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedAnio,
                            onDismissRequest = { expandedAnio = false }
                        ) {
                            aniosDisponibles.forEach { anio ->
                                DropdownMenuItem(
                                    text = { Text(anio) },
                                    onClick = {
                                        selectedAnio = anio
                                        expandedAnio = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.fetchHechosDelictivos(
                                    departamento = if (selectedDepartamento == "TODOS") "" else selectedDepartamento,
                                    municipio = if (selectedMunicipio == "TODOS") "" else selectedMunicipio,
                                    anio = if (selectedAnio == "TODOS") "" else selectedAnio
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1976D2)
                            )
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buscar")
                        }

                        OutlinedButton(
                            onClick = {
                                selectedDepartamento = "TODOS"
                                selectedMunicipio = "TODOS"
                                selectedAnio = "TODOS"
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Limpiar")
                        }
                    }
                }
            }

            when (val state = uiState) {
                is UiState.Idle -> {
                    EmptyStateView(
                        message = "Selecciona los filtros y\npresiona Buscar"
                    )
                }
                is UiState.Loading -> {
                    LoadingView()
                }
                is UiState.Success -> {
                    if (state.hechos.isEmpty()) {
                        EmptyStateView(
                            message = "No se encontraron registros\ncon los criterios seleccionados"
                        )
                    } else {
                        HechosListView(hechos = state.hechos)
                    }
                }
                is UiState.Error -> {
                    ErrorView(message = state.message)
                }
            }
        }
    }
}

@Composable
fun HechosListView(hechos: List<HechoDelictivo>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "${hechos.size} registros encontrados",
            fontSize = 14.sp,
            color = Color(0xFF718096),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(hechos) { hecho ->
                HechoCard(hecho = hecho)
            }
        }
    }
}

@Composable
fun HechoCard(hecho: HechoDelictivo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hecho.municipio,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748),
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = when {
                        hecho.cantidad.toIntOrNull() ?: 0 > 10 -> Color(0xFFEF4444)
                        hecho.cantidad.toIntOrNull() ?: 0 > 5 -> Color(0xFFF59E0B)
                        else -> Color(0xFF10B981)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = hecho.cantidad,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = hecho.departamento,
                    fontSize = 14.sp,
                    color = Color(0xFF4A5568)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Color(0xFF718096),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatearFecha(hecho.fechaHecho),
                    fontSize = 13.sp,
                    color = Color(0xFF718096)
                )
            }
        }
    }
}

fun formatearFecha(fecha: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
        val date = inputFormat.parse(fecha)
        date?.let { outputFormat.format(it) } ?: fecha
    } catch (e: Exception) {
        fecha.take(10)
    }
}

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFF1976D2),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Consultando datos...",
                fontSize = 16.sp,
                color = Color(0xFF718096)
            )
        }
    }
}

@Composable
fun EmptyStateView(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFCBD5E0)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color(0xFF718096),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(32.dp)
            )
        }
    }
}

@Composable
fun ErrorView(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Error",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = Color(0xFF991B1B),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}