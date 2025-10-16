package com.svape.masterunalapp.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.svape.masterunalapp.data.model.Company
import com.svape.masterunalapp.ui.screens.CompanyFormScreen
import com.svape.masterunalapp.ui.screens.CompanyListScreen
import com.svape.masterunalapp.ui.theme.MasterUnalAppTheme
import com.svape.masterunalapp.ui.viewmodel.CompanyViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: CompanyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = CompanyViewModel(application)

        enableEdgeToEdge()
        setContent {
            MasterUnalAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompanyDirectoryApp(viewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onCleared()
    }
}

sealed class Screen {
    object List : Screen()
    object Add : Screen()
    data class Edit(val company: Company) : Screen()
}

@Composable
fun CompanyDirectoryApp(viewModel: CompanyViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.List) }

    when (val screen = currentScreen) {
        is Screen.List -> {
            CompanyListScreen(
                viewModel = viewModel,
                onAddCompany = { currentScreen = Screen.Add },
                onEditCompany = { company -> currentScreen = Screen.Edit(company) }
            )
        }
        is Screen.Add -> {
            CompanyFormScreen(
                viewModel = viewModel,
                company = null,
                onNavigateBack = { currentScreen = Screen.List }
            )
        }
        is Screen.Edit -> {
            CompanyFormScreen(
                viewModel = viewModel,
                company = screen.company,
                onNavigateBack = { currentScreen = Screen.List }
            )
        }
    }
}