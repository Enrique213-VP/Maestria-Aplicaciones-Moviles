package com.svape.masterunalapp.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svape.masterunalapp.ui.viewmodel.OnlineGameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineGameScreen(
    viewModel: OnlineGameViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }
    var soundEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Juego Online") },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(Icons.Default.ArrowBack, "Salir")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        soundEnabled = !soundEnabled
                        viewModel.setSoundEnabled(soundEnabled)
                    }) {
                        Text(
                            text = if (soundEnabled) "🔊" else "🔇",
                            fontSize = 20.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF003366),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Información del juego
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Jugando como: ${uiState.mySymbol}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Oponente: ${uiState.opponentName}",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mensaje de estado
            Text(
                text = uiState.gameMessage,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = when {
                    uiState.isGameOver -> Color(0xFFD32F2F)
                    uiState.isMyTurn -> Color(0xFF2E7D32)
                    else -> Color.Gray
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tablero
            uiState.currentGame?.let { game ->
                CustomTicTacToeBoard(
                    board = game.board.map { if (it == " ") ' ' else it[0] },
                    onCellClick = { position ->
                        if (uiState.isMyTurn && !uiState.isGameOver) {
                            viewModel.makeMove(position)
                        }
                    },
                    enabled = uiState.isMyTurn && !uiState.isGameOver,
                    modifier = Modifier.size(320.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de nuevo juego (solo cuando termine)
            if (uiState.isGameOver) {
                Button(
                    onClick = {
                        viewModel.leaveGame()
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF003366)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver a la lista de juegos")
                }
            }
        }
    }

    // Diálogo de espera
    if (uiState.showWaitingDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Esperando oponente") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Esperando a que otro jugador se una...")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.leaveGame()
                    onNavigateBack()
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de confirmación de salida
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Abandonar juego") },
            text = { Text("¿Estás seguro de que quieres abandonar este juego?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.leaveGame()
                        onNavigateBack()
                    }
                ) {
                    Text("Sí, abandonar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Mostrar mensajes de error
    errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Mostrar un Snackbar
        }
    }
}