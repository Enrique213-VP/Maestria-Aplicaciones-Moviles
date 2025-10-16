package com.svape.masterunalapp.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svape.masterunalapp.data.model.Company
import com.svape.masterunalapp.data.model.CompanyClassification
import com.svape.masterunalapp.ui.viewmodel.CompanyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyListScreen(
    viewModel: CompanyViewModel,
    onAddCompany: () -> Unit,
    onEditCompany: (Company) -> Unit
) {
    val companies by viewModel.companies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showFilterDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var companyToDelete by remember { mutableStateOf<Company?>(null) }

    var searchName by remember { mutableStateOf("") }
    var filterClassification by remember { mutableStateOf<CompanyClassification?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Directorio de Empresas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF003366),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filtrar",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        searchName = ""
                        filterClassification = null
                        viewModel.loadCompanies()
                    }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Actualizar",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCompany,
                containerColor = Color(0xFF4A90E2)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar empresa")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchName,
                onValueChange = {
                    searchName = it
                    viewModel.searchCompanies(it.ifBlank { null }, filterClassification)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar por nombre...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            if (filterClassification != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Filtro: ${filterClassification?.displayName}",
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            filterClassification = null
                            viewModel.searchCompanies(searchName.ifBlank { null }, null)
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Quitar filtro")
                        }
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (companies.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Business,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No hay empresas registradas", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(companies) { company ->
                        CompanyCard(
                            company = company,
                            onEdit = { onEditCompany(company) },
                            onDelete = {
                                companyToDelete = company
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filtrar por clasificación") },
            text = {
                Column {
                    CompanyClassification.values().forEach { classification ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    filterClassification = classification
                                    viewModel.searchCompanies(
                                        searchName.ifBlank { null },
                                        classification
                                    )
                                    showFilterDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = filterClassification == classification,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(classification.displayName)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (showDeleteDialog && companyToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro que desea eliminar la empresa '${companyToDelete?.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        companyToDelete?.let { company ->
                            viewModel.deleteCompany(company.id) { success ->
                                if (success) {
                                    showDeleteDialog = false
                                    companyToDelete = null
                                }
                            }
                        }
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CompanyCard(
    company: Company,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = company.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003366)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = when (company.classification) {
                            CompanyClassification.CONSULTORIA -> Color(0xFFE3F2FD)
                            CompanyClassification.DESARROLLO_MEDIDA -> Color(0xFFFFF3E0)
                            CompanyClassification.FABRICA_SOFTWARE -> Color(0xFFE8F5E9)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = company.classification.displayName,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color(0xFF4A90E2)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color(0xFFE53935)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (company.website.isNotBlank()) {
                InfoRow(Icons.Default.Language, company.website)
            }

            if (company.phone.isNotBlank()) {
                InfoRow(Icons.Default.Phone, company.phone)
            }

            if (company.email.isNotBlank()) {
                InfoRow(Icons.Default.Email, company.email)
            }

            if (company.productsServices.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Productos y servicios:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Text(
                    text = company.productsServices,
                    fontSize = 14.sp,
                    color = Color(0xFF424242)
                )
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF424242)
        )
    }
}