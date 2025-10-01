package com.svape.masterunalapp.ui.view

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svape.masterunalapp.ui.theme.MasterUnalAppTheme
import com.svape.masterunalapp.ui.utils.SoundManager
import com.svape.masterunalapp.ui.viewmodel.TicTacToeEvent
import com.svape.masterunalapp.ui.viewmodel.TicTacToeViewModel
import com.svape.masterunalapp.data.TicTacToeGame

class TicTacToeActivity : ComponentActivity() {

    private val viewModel: TicTacToeViewModel by viewModels()
    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        soundManager = SoundManager(this, this)
        viewModel.setSoundManager(soundManager)
        viewModel.initializeWithContext(this)

        setContent {
            MasterUnalAppTheme {
                AdaptiveTicTacToeScreen(
                    viewModel = viewModel,
                    onQuit = { finish() }
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.savePreferences()
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveInstanceState()
    }
}

@Composable
fun AdaptiveTicTacToeScreen(
    viewModel: TicTacToeViewModel,
    onQuit: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        TicTacToeScreenLandscape(viewModel, onQuit)
    } else {
        TicTacToeScreenPortrait(viewModel, onQuit)
    }
}

@Composable
fun TicTacToeScreenPortrait(
    viewModel: TicTacToeViewModel,
    onQuit: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TicTacToeTopBar(
                onNewGame = { viewModel.onEvent(TicTacToeEvent.NewGameRequested) },
                onDifficultyClick = { viewModel.onEvent(TicTacToeEvent.ShowDifficultyDialog) },
                onResetScores = { viewModel.onEvent(TicTacToeEvent.ResetScores) },
                onAbout = { viewModel.onEvent(TicTacToeEvent.ShowAboutDialog) },
                soundEnabled = uiState.soundEnabled,
                onSoundToggle = {
                    viewModel.onEvent(TicTacToeEvent.SetSoundEnabled(!uiState.soundEnabled))
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Triqui",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF003366)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = uiState.gameMessage,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = if (uiState.isGameOver) Color(0xFFD32F2F) else Color(0xFF2E7D32)
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTicTacToeBoard(
                board = uiState.board,
                onCellClick = { position ->
                    viewModel.onEvent(TicTacToeEvent.CellClicked(position))
                },
                enabled = !uiState.isGameOver && !uiState.isComputerTurn,
                modifier = Modifier.size(320.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            ScoreBoard(
                humanWins = uiState.humanWins,
                computerWins = uiState.computerWins,
                ties = uiState.ties
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Dificultad: ${viewModel.getDifficultyDisplayName(uiState.currentDifficulty)}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }

    ShowDialogs(viewModel, uiState)
}

@Composable
fun TicTacToeScreenLandscape(
    viewModel: TicTacToeViewModel,
    onQuit: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TicTacToeTopBar(
                onNewGame = { viewModel.onEvent(TicTacToeEvent.NewGameRequested) },
                onDifficultyClick = { viewModel.onEvent(TicTacToeEvent.ShowDifficultyDialog) },
                onResetScores = { viewModel.onEvent(TicTacToeEvent.ResetScores) },
                onAbout = { viewModel.onEvent(TicTacToeEvent.ShowAboutDialog) },
                soundEnabled = uiState.soundEnabled,
                onSoundToggle = {
                    viewModel.onEvent(TicTacToeEvent.SetSoundEnabled(!uiState.soundEnabled))
                }
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomTicTacToeBoard(
                board = uiState.board,
                onCellClick = { position ->
                    viewModel.onEvent(TicTacToeEvent.CellClicked(position))
                },
                enabled = !uiState.isGameOver && !uiState.isComputerTurn,
                modifier = Modifier.size(270.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Triqui",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003366)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = uiState.gameMessage,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (uiState.isGameOver) Color(0xFFD32F2F) else Color(0xFF2E7D32)
                )

                Spacer(modifier = Modifier.height(24.dp))

                ScoreBoard(
                    humanWins = uiState.humanWins,
                    computerWins = uiState.computerWins,
                    ties = uiState.ties
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Dificultad: ${viewModel.getDifficultyDisplayName(uiState.currentDifficulty)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }

    ShowDialogs(viewModel, uiState)
}

@Composable
private fun ShowDialogs(
    viewModel: TicTacToeViewModel,
    uiState: com.svape.masterunalapp.ui.viewmodel.TicTacToeUiState
) {
    if (uiState.showDifficultyDialog) {
        DifficultyDialog(
            currentDifficulty = uiState.currentDifficulty,
            onDifficultySelected = { difficulty ->
                viewModel.onEvent(TicTacToeEvent.SetDifficulty(difficulty))
            },
            onDismiss = { viewModel.onEvent(TicTacToeEvent.DismissDialogs) },
            getDifficultyName = { viewModel.getDifficultyDisplayName(it) }
        )
    }

    if (uiState.showAboutDialog) {
        AboutDialog(
            onDismiss = { viewModel.onEvent(TicTacToeEvent.DismissDialogs) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicTacToeTopBar(
    onNewGame: () -> Unit,
    onDifficultyClick: () -> Unit,
    onResetScores: () -> Unit,
    onAbout: () -> Unit,
    soundEnabled: Boolean,
    onSoundToggle: () -> Unit
) {
    TopAppBar(
        title = { Text("Triqui") },
        actions = {
            IconButton(onClick = onNewGame) {
                Text("🔄", fontSize = 20.sp)
            }
            IconButton(onClick = onDifficultyClick) {
                Text("⚙️", fontSize = 20.sp)
            }
            IconButton(onClick = onResetScores) {
                Text("🔃", fontSize = 20.sp)
            }
            IconButton(onClick = onSoundToggle) {
                Text(if (soundEnabled) "🔊" else "🔇", fontSize = 20.sp)
            }
            IconButton(onClick = onAbout) {
                Text("ℹ️", fontSize = 20.sp)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF003366),
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
fun ScoreBoard(
    humanWins: Int,
    computerWins: Int,
    ties: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScoreItem("Humano", humanWins, Color(0xFF2E7D32))
            ScoreItem("Empates", ties, Color(0xFFFF9800))
            ScoreItem("Android", computerWins, Color(0xFFD32F2F))
        }
    }
}

@Composable
fun ScoreItem(label: String, score: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = score.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun DifficultyDialog(
    currentDifficulty: TicTacToeGame.DifficultyLevel,
    onDifficultySelected: (TicTacToeGame.DifficultyLevel) -> Unit,
    onDismiss: () -> Unit,
    getDifficultyName: (TicTacToeGame.DifficultyLevel) -> String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Dificultad") },
        text = {
            Column {
                TicTacToeGame.DifficultyLevel.values().forEach { difficulty ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentDifficulty == difficulty,
                            onClick = { onDifficultySelected(difficulty) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(getDifficultyName(difficulty))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Acerca de") },
        text = {
            Column {
                Text("Juego de Triqui")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Desarrollado con Jetpack Compose")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Sergio Vargas Pedraza")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Universidad Nacional de Colombia")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}