package com.svape.masterunalapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.svape.masterunalapp.data.TicTacToeGame

data class TicTacToeUiState(
    val board: List<Char> = List(9) { ' ' },
    val gameMessage: String = "¡Tu turno! Toca un cuadro para jugar",
    val isGameOver: Boolean = false,
    val isComputerTurn: Boolean = false
)

sealed class TicTacToeEvent {
    data class CellClicked(val position: Int) : TicTacToeEvent()
    object NewGameRequested : TicTacToeEvent()
    object ComputerMoveCompleted : TicTacToeEvent()
}

class TicTacToeViewModel : ViewModel() {

    private val game = TicTacToeGame()

    private val _uiState = MutableStateFlow(TicTacToeUiState())
    val uiState: StateFlow<TicTacToeUiState> = _uiState.asStateFlow()

    init {
        startNewGame()
    }

    fun onEvent(event: TicTacToeEvent) {
        when (event) {
            is TicTacToeEvent.CellClicked -> handleCellClick(event.position)
            is TicTacToeEvent.NewGameRequested -> startNewGame()
            is TicTacToeEvent.ComputerMoveCompleted -> handleComputerMove()
        }
    }

    private fun handleCellClick(position: Int) {
        val currentState = _uiState.value

        if (currentState.isGameOver || currentState.isComputerTurn ||
            currentState.board[position] != ' ') return

        // Movimiento del usuario
        if (game.setMove(TicTacToeGame.HUMAN_PLAYER, position)) {
            updateUIAfterMove()
        }
    }

    private fun handleComputerMove() {
        val computerMove = game.getComputerMove()
        game.setMove(TicTacToeGame.COMPUTER_PLAYER, computerMove)
        updateUIAfterMove()

        _uiState.value = _uiState.value.copy(isComputerTurn = false)
    }

    private fun updateUIAfterMove() {
        val board = List(9) { index -> game.getBoardOccupant(index) }
        val winner = game.checkForWinner()

        val newMessage = when (winner) {
            0 -> {
                if (!_uiState.value.isComputerTurn) {
                    _uiState.value = _uiState.value.copy(isComputerTurn = true)
                    "Turno de Android..."
                } else {
                    "¡Tu turno!"
                }
            }
            1 -> "¡Es un empate!"
            2 -> "¡Ganaste!"
            3 -> "¡Android ganó!"
            else -> "Error en el juego"
        }

        _uiState.value = _uiState.value.copy(
            board = board,
            gameMessage = newMessage,
            isGameOver = winner != 0
        )
    }

    private fun startNewGame() {
        game.clearBoard()
        _uiState.value = TicTacToeUiState()
    }
}