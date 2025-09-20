package com.svape.masterunalapp.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svape.masterunalapp.R
import com.svape.masterunalapp.data.TicTacToeGame
import com.svape.masterunalapp.ui.theme.MasterUnalAppTheme
import com.svape.masterunalapp.ui.viewmodel.TicTacToeViewModel
import com.svape.masterunalapp.ui.viewmodel.TicTacToeEvent
import kotlinx.coroutines.delay

class TicTacToeActivity : ComponentActivity() {

    private val viewModel: TicTacToeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MasterUnalAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TicTacToeScreen(
                        viewModel = viewModel,
                        onQuitRequested = { finish() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicTacToeScreen(
    viewModel: TicTacToeViewModel,
    onQuitRequested: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showMenu by remember { mutableStateOf(false) }

    // Efecto para el movimiento de la computadora
    LaunchedEffect(uiState.isComputerTurn) {
        if (uiState.isComputerTurn && !uiState.isGameOver) {
            delay(1000)
            viewModel.onEvent(TicTacToeEvent.ComputerMoveCompleted)
        }
    }

    // Efecto para manejar el quit
    LaunchedEffect(uiState) {
        // Este LaunchedEffect se puede usar para otras acciones si es necesario
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Barra superior con menú
        TopAppBar(
            title = {
                Text(
                    text = "Triqui",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003366)
                )
            },
            actions = {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Menú",
                            tint = Color(0xFF003366)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Nuevo Juego") },
                            onClick = {
                                showMenu = false
                                viewModel.onEvent(TicTacToeEvent.NewGameRequested)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Dificultad") },
                            onClick = {
                                showMenu = false
                                viewModel.onEvent(TicTacToeEvent.ShowDifficultyDialog)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Acerca de") },
                            onClick = {
                                showMenu = false
                                viewModel.onEvent(TicTacToeEvent.ShowAboutDialog)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Salir") },
                            onClick = {
                                showMenu = false
                                viewModel.onEvent(TicTacToeEvent.ShowQuitDialog)
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Indicador de dificultad actual
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Text(
                text = "Dificultad: ${viewModel.getDifficultyDisplayName(uiState.currentDifficulty)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1976D2),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tablero de juego
        Card(
            modifier = Modifier
                .padding(16.dp)
                .size(320.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                for (row in 0..2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (col in 0..2) {
                            val position = row * 3 + col
                            val cellValue = uiState.board[position]

                            GameCell(
                                value = cellValue,
                                onClick = {
                                    viewModel.onEvent(TicTacToeEvent.CellClicked(position))
                                },
                                enabled = !uiState.isGameOver && !uiState.isComputerTurn && cellValue == ' '
                            )
                        }
                    }
                }
            }
        }

        // Mensaje del juego
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Text(
                text = uiState.gameMessage,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF003366),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        // Botones
        Row(
            modifier = Modifier.padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    viewModel.onEvent(TicTacToeEvent.NewGameRequested)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF003366),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = "Nuevo Juego",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            val context = LocalContext.current
            OutlinedButton(
                onClick = {
                    (context as? ComponentActivity)?.finish()
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF4A90E2)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = "Volver",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    // Diálogos
    if (uiState.showDifficultyDialog) {
        DifficultyDialog(
            currentDifficulty = uiState.currentDifficulty,
            onDifficultySelected = { difficulty ->
                viewModel.onEvent(TicTacToeEvent.SetDifficulty(difficulty))
            },
            onDismiss = {
                viewModel.onEvent(TicTacToeEvent.DismissDialogs)
            },
            viewModel = viewModel
        )
    }

    if (uiState.showQuitDialog) {
        QuitDialog(
            onConfirm = {
                viewModel.onEvent(TicTacToeEvent.DismissDialogs)
                onQuitRequested()
            },
            onDismiss = {
                viewModel.onEvent(TicTacToeEvent.DismissDialogs)
            }
        )
    }

    if (uiState.showAboutDialog) {
        AboutDialog(
            onDismiss = {
                viewModel.onEvent(TicTacToeEvent.DismissDialogs)
            }
        )
    }
}

@Composable
fun GameCell(
    value: Char,
    onClick: () -> Unit,
    enabled: Boolean
) {
    val backgroundColor = when (value) {
        'X' -> Color(0xFFC8E6C9)
        'O' -> Color(0xFFFFCDD2)
        else -> Color(0xFFE3F2FD)
    }

    val textColor = when (value) {
        'X' -> Color(0xFF2E7D32)
        'O' -> Color(0xFFD32F2F)
        else -> Color.Transparent
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor,
            disabledContainerColor = backgroundColor,
            disabledContentColor = textColor
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.size(80.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = if (value != ' ') value.toString() else "",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DifficultyDialog(
    currentDifficulty: TicTacToeGame.DifficultyLevel,
    onDifficultySelected: (TicTacToeGame.DifficultyLevel) -> Unit,
    onDismiss: () -> Unit,
    viewModel: TicTacToeViewModel
) {
    val difficulties = listOf(
        TicTacToeGame.DifficultyLevel.Easy,
        TicTacToeGame.DifficultyLevel.Harder,
        TicTacToeGame.DifficultyLevel.Expert
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Elegir nivel de dificultad",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                difficulties.forEach { difficulty ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = difficulty == currentDifficulty,
                            onClick = { onDifficultySelected(difficulty) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = viewModel.getDifficultyDisplayName(difficulty),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun QuitDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "¿Estás seguro?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("¿Quieres salir del juego?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Sí")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@Composable
fun AboutDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Acerca de Triqui",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                // Imagen del ícono (opcional)
                /*
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Ícono de la app",
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                */

                Text(
                    text = "Triqui (Tic-Tac-Toe)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Desarrollado por:\nSergio Vargas Pedraza",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Desarrollo de Aplicaciones\npara Dispositivos Móviles",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Elige uno de tres niveles de dificultad.\n¡No dejes que Android gane!",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TicTacToeScreenPreview() {
    MasterUnalAppTheme {
        TicTacToeScreen(viewModel = TicTacToeViewModel())
    }
}