package com.svape.masterunalapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.svape.masterunalapp.data.model.Company
import com.svape.masterunalapp.data.model.CompanyClassification
import com.svape.masterunalapp.ui.viewmodel.CompanyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyFormScreen(
    viewModel: CompanyViewModel,
    company: Company? = null,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf(company?.name ?: "") }
    var website by remember { mutableStateOf(company?.website ?: "") }
    var phone by remember { mutableStateOf(company?.phone ?: "") }
    var email by remember { mutableStateOf(company?.email ?: "") }
    var productsServices by remember { mutableStateOf(company?.productsServices ?: "") }
    var classification by remember { mutableStateOf(company?.classification ?: CompanyClassification.CONSULTORIA) }

    var showClassificationMenu by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val isEditing = company != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Empresa" else "Nueva Empresa") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF003366),
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la empresa *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showError && name.isBlank()
            )

            OutlinedTextField(
                value = website,
                onValueChange = { website = it },
                label = { Text("Sitio web") },
                placeholder = { Text("https://ejemplo.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                placeholder = { Text("3001234567") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("contacto@empresa.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = productsServices,
                onValueChange = { productsServices = it },
                label = { Text("Productos y servicios") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            ExposedDropdownMenuBox(
                expanded = showClassificationMenu,
                onExpandedChange = { showClassificationMenu = it }
            ) {
                OutlinedTextField(
                    value = classification.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Clasificación *") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showClassificationMenu)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = showClassificationMenu,
                    onDismissRequest = { showClassificationMenu = false }
                ) {
                    CompanyClassification.values().forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.displayName) },
                            onClick = {
                                classification = option
                                showClassificationMenu = false
                            }
                        )
                    }
                }
            }

            if (showError && errorMessage.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(12.dp),
                        color = Color(0xFFC62828)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        showError = true
                        errorMessage = "El nombre de la empresa es obligatorio"
                        return@Button
                    }

                    val newCompany = Company(
                        id = company?.id ?: 0,
                        name = name.trim(),
                        website = website.trim(),
                        phone = phone.trim(),
                        email = email.trim(),
                        productsServices = productsServices.trim(),
                        classification = classification
                    )

                    if (isEditing) {
                        viewModel.updateCompany(newCompany) { success ->
                            if (success) {
                                onNavigateBack()
                            } else {
                                showError = true
                                errorMessage = "Error al actualizar la empresa"
                            }
                        }
                    } else {
                        viewModel.insertCompany(newCompany) { success ->
                            if (success) {
                                onNavigateBack()
                            } else {
                                showError = true
                                errorMessage = "Error al guardar la empresa"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
            ) {
                Text(
                    text = if (isEditing) "Actualizar" else "Guardar",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cancelar",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}