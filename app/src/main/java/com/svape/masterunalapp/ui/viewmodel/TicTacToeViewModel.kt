package com.svape.masterunalapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.svape.masterunalapp.data.TicTacToeGame
import com.svape.masterunalapp.ui.utils.SoundManager

data class TicTacToeUiState(
    val board: List<Char> = List(9) { ' ' },
    val gameMessage: String = "¡Tu turno! Toca un cuadro para jugar",
    val isGameOver: Boolean = false,
    val isComputerTurn: Boolean = false,
    val currentDifficulty: TicTacToeGame.DifficultyLevel = TicTacToeGame.DifficultyLevel.Expert,
    val showDifficultyDialog: Boolean = false,
    val showQuitDialog: Boolean = false,
    val showAboutDialog: Boolean = false,
    val soundEnabled: Boolean = true
)

sealed class TicTacToeEvent {
    data class CellClicked(val position: Int) : TicTacToeEvent()
    object NewGameRequested : TicTacToeEvent()
    object ComputerMoveCompleted : TicTacToeEvent()
    object ShowDifficultyDialog : TicTacToeEvent()
    object ShowQuitDialog : TicTacToeEvent()
    object ShowAboutDialog : TicTacToeEvent()
    object DismissDialogs : TicTacToeEvent()
    data class SetDifficulty(val difficulty: TicTacToeGame.DifficultyLevel) : TicTacToeEvent()
    object QuitGame : TicTacToeEvent()
    data class SetSoundEnabled(val enabled: Boolean) : TicTacToeEvent()
}

class TicTacToeViewModel : ViewModel() {

    private val game = TicTacToeGame()
    private var soundManager: SoundManager? = null

    private val _uiState = MutableStateFlow(TicTacToeUiState())
    val uiState: StateFlow<TicTacToeUiState> = _uiState.asStateFlow()

    init {
        startNewGame()
    }

    fun setSoundManager(manager: SoundManager) {
        soundManager = manager
    }

    fun onEvent(event: TicTacToeEvent) {
        when (event) {
            is TicTacToeEvent.CellClicked -> handleCellClick(event.position)
            is TicTacToeEvent.NewGameRequested -> startNewGame()
            is TicTacToeEvent.ComputerMoveCompleted -> handleComputerMove()
            is TicTacToeEvent.ShowDifficultyDialog -> {
                _uiState.value = _uiState.value.copy(showDifficultyDialog = true)
            }
            is TicTacToeEvent.ShowQuitDialog -> {
                _uiState.value = _uiState.value.copy(showQuitDialog = true)
            }
            is TicTacToeEvent.ShowAboutDialog -> {
                _uiState.value = _uiState.value.copy(showAboutDialog = true)
            }
            is TicTacToeEvent.DismissDialogs -> {
                _uiState.value = _uiState.value.copy(
                    showDifficultyDialog = false,
                    showQuitDialog = false,
                    showAboutDialog = false
                )
            }
            is TicTacToeEvent.SetDifficulty -> {
                game.setDifficultyLevel(event.difficulty)
                _uiState.value = _uiState.value.copy(
                    currentDifficulty = event.difficulty,
                    showDifficultyDialog = false
                )
            }
            is TicTacToeEvent.QuitGame -> {
                // Este evento será manejado en la Activity
            }
            is TicTacToeEvent.SetSoundEnabled -> {
                soundManager?.setSoundEnabled(event.enabled)
                _uiState.value = _uiState.value.copy(soundEnabled = event.enabled)
            }
        }
    }

    private fun handleCellClick(position: Int) {
        val currentState = _uiState.value

        if (currentState.isGameOver || currentState.isComputerTurn ||
            currentState.board[position] != ' ') return

        // Movimiento del usuario
        if (game.setMove(TicTacToeGame.HUMAN_PLAYER, position)) {
            // Reproducir sonido del jugador humano
            soundManager?.playHumanMoveSound()

            updateUIAfterMove()

            // Si el juego no ha terminado, programar el movimiento de la computadora
            if (!_uiState.value.isGameOver) {
                scheduleComputerMove()
            }
        }
    }

    private fun scheduleComputerMove() {
        _uiState.value = _uiState.value.copy(
            isComputerTurn = true,
            gameMessage = "Turno de Android..."
        )

        // Programar el movimiento de la computadora con delay
        viewModelScope.launch {
            delay(1000) // Esperar 1 segundo
            handleComputerMove()
        }
    }

    private fun handleComputerMove() {
        val computerMove = game.getComputerMove()
        game.setMove(TicTacToeGame.COMPUTER_PLAYER, computerMove)

        // Reproducir sonido del movimiento de la computadora
        soundManager?.playComputerMoveSound()

        updateUIAfterMove()
        _uiState.value = _uiState.value.copy(isComputerTurn = false)
    }

    private fun updateUIAfterMove() {
        val board = List(9) { index -> game.getBoardOccupant(index) }
        val winner = game.checkForWinner()

        val newMessage = when (winner) {
            0 -> {
                if (_uiState.value.isComputerTurn) {
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
        val currentDifficulty = game.getDifficultyLevel()
        _uiState.value = TicTacToeUiState(
            currentDifficulty = currentDifficulty,
            soundEnabled = _uiState.value.soundEnabled
        )
    }

    fun getDifficultyDisplayName(difficulty: TicTacToeGame.DifficultyLevel): String {
        return when (difficulty) {
            TicTacToeGame.DifficultyLevel.Easy -> "Fácil"
            TicTacToeGame.DifficultyLevel.Harder -> "Difícil"
            TicTacToeGame.DifficultyLevel.Expert -> "Experto"
        }
    }
}