package com.svape.masterunalapp.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                    TicTacToeScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun TicTacToeScreen(viewModel: TicTacToeViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Efecto para el movimiento de la computadora
    LaunchedEffect(uiState.isComputerTurn) {
        if (uiState.isComputerTurn && !uiState.isGameOver) {
            delay(1000)
            viewModel.onEvent(TicTacToeEvent.ComputerMoveCompleted)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Triqui",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF003366),
            modifier = Modifier.padding(bottom = 20.dp)
        )

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

@Preview(showBackground = true)
@Composable
fun TicTacToeScreenPreview() {
    MasterUnalAppTheme {
        TicTacToeScreen(viewModel = TicTacToeViewModel())
    }
}