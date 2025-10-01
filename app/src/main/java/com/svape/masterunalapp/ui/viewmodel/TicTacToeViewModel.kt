package com.svape.masterunalapp.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
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
    val soundEnabled: Boolean = true,
    val humanWins: Int = 0,
    val computerWins: Int = 0,
    val ties: Int = 0
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
    object ResetScores : TicTacToeEvent()
}

class TicTacToeViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_BOARD = "board"
        private const val KEY_GAME_OVER = "gameOver"
        private const val KEY_GAME_MESSAGE = "gameMessage"
        private const val KEY_IS_COMPUTER_TURN = "isComputerTurn"
        private const val KEY_GO_FIRST = "goFirst"

        private const val PREFS_NAME = "ttt_prefs"
        private const val KEY_HUMAN_WINS = "humanWins"
        private const val KEY_COMPUTER_WINS = "computerWins"
        private const val KEY_TIES = "ties"
        private const val KEY_DIFFICULTY = "difficulty"
        private const val KEY_SOUND_ENABLED = "soundEnabled"
    }

    private val game = TicTacToeGame()
    private var soundManager: SoundManager? = null
    private var sharedPrefs: SharedPreferences? = null
    private var goFirst: Char = TicTacToeGame.HUMAN_PLAYER

    private val _uiState = MutableStateFlow(TicTacToeUiState())
    val uiState: StateFlow<TicTacToeUiState> = _uiState.asStateFlow()

    init {
        restoreInstanceState()
    }

    fun initializeWithContext(context: Context) {
        sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadPreferences()
    }

    fun setSoundManager(manager: SoundManager) {
        soundManager = manager
    }

    private fun restoreInstanceState() {
        val savedBoard = savedStateHandle.get<CharArray>(KEY_BOARD)

        if (savedBoard != null) {
            // Hay un estado guardado, restaurarlo
            game.setBoardState(savedBoard)
            val isComputerTurn = savedStateHandle.get<Boolean>(KEY_IS_COMPUTER_TURN) ?: false
            goFirst = savedStateHandle.get<Char>(KEY_GO_FIRST) ?: TicTacToeGame.HUMAN_PLAYER

            _uiState.value = _uiState.value.copy(
                board = savedBoard.toList(),
                gameMessage = savedStateHandle.get<String>(KEY_GAME_MESSAGE)
                    ?: "¡Tu turno! Toca un cuadro para jugar",
                isGameOver = savedStateHandle.get<Boolean>(KEY_GAME_OVER) ?: false,
                isComputerTurn = isComputerTurn
            )

            // Si es el turno de la computadora después de restaurar, programar su movimiento
            if (isComputerTurn && !_uiState.value.isGameOver) {
                scheduleComputerMove()
            }
        } else {
            // No hay estado guardado, iniciar nuevo juego
            startNewGame()
        }
    }

    private fun loadPreferences() {
        sharedPrefs?.let { prefs ->
            val humanWins = prefs.getInt(KEY_HUMAN_WINS, 0)
            val computerWins = prefs.getInt(KEY_COMPUTER_WINS, 0)
            val ties = prefs.getInt(KEY_TIES, 0)
            val difficultyOrdinal = prefs.getInt(KEY_DIFFICULTY, TicTacToeGame.DifficultyLevel.Expert.ordinal)
            val soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true)

            val difficulty = TicTacToeGame.DifficultyLevel.values()[difficultyOrdinal]
            game.setDifficultyLevel(difficulty)

            _uiState.value = _uiState.value.copy(
                humanWins = humanWins,
                computerWins = computerWins,
                ties = ties,
                currentDifficulty = difficulty,
                soundEnabled = soundEnabled
            )

            soundManager?.setSoundEnabled(soundEnabled)
        }
    }

    fun savePreferences() {
        sharedPrefs?.edit()?.apply {
            putInt(KEY_HUMAN_WINS, _uiState.value.humanWins)
            putInt(KEY_COMPUTER_WINS, _uiState.value.computerWins)
            putInt(KEY_TIES, _uiState.value.ties)
            putInt(KEY_DIFFICULTY, _uiState.value.currentDifficulty.ordinal)
            putBoolean(KEY_SOUND_ENABLED, _uiState.value.soundEnabled)
            apply()
        }
    }

    fun saveInstanceState() {
        savedStateHandle[KEY_BOARD] = game.getBoardState()
        savedStateHandle[KEY_GAME_OVER] = _uiState.value.isGameOver
        savedStateHandle[KEY_GAME_MESSAGE] = _uiState.value.gameMessage
        savedStateHandle[KEY_IS_COMPUTER_TURN] = _uiState.value.isComputerTurn
        savedStateHandle[KEY_GO_FIRST] = goFirst
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
                savePreferences()
            }
            is TicTacToeEvent.QuitGame -> {
                // Este evento será manejado en la Activity
            }
            is TicTacToeEvent.SetSoundEnabled -> {
                soundManager?.setSoundEnabled(event.enabled)
                _uiState.value = _uiState.value.copy(soundEnabled = event.enabled)
                savePreferences()
            }
            is TicTacToeEvent.ResetScores -> {
                _uiState.value = _uiState.value.copy(
                    humanWins = 0,
                    computerWins = 0,
                    ties = 0
                )
                savePreferences()
            }
        }
    }

    private fun handleCellClick(position: Int) {
        val currentState = _uiState.value

        if (currentState.isGameOver || currentState.isComputerTurn ||
            currentState.board[position] != ' ') return

        // Movimiento del usuario
        if (game.setMove(TicTacToeGame.HUMAN_PLAYER, position)) {
            soundManager?.playHumanMoveSound()
            updateUIAfterMove()

            // Guardar el estado después del movimiento del jugador
            saveInstanceState()

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

        saveInstanceState()

        viewModelScope.launch {
            delay(1000)
            handleComputerMove()
        }
    }

    private fun handleComputerMove() {
        val computerMove = game.getComputerMove()
        game.setMove(TicTacToeGame.COMPUTER_PLAYER, computerMove)

        soundManager?.playComputerMoveSound()
        updateUIAfterMove()

        _uiState.value = _uiState.value.copy(isComputerTurn = false)

        saveInstanceState()
    }

    private fun updateUIAfterMove() {
        val board = List(9) { index -> game.getBoardOccupant(index) }
        val winner = game.checkForWinner()

        val (newMessage, isGameOver) = when (winner) {
            0 -> {
                if (_uiState.value.isComputerTurn) {
                    "Turno de Android..." to false
                } else {
                    "¡Tu turno!" to false
                }
            }
            1 -> "¡Es un empate!" to true
            2 -> "¡Ganaste!" to true
            3 -> "¡Android ganó!" to true
            else -> "Error en el juego" to true
        }

        var humanWins = _uiState.value.humanWins
        var computerWins = _uiState.value.computerWins
        var ties = _uiState.value.ties

        if (isGameOver) {
            when (winner) {
                1 -> ties++
                2 -> humanWins++
                3 -> computerWins++
            }
            savePreferences()
        }

        _uiState.value = _uiState.value.copy(
            board = board,
            gameMessage = newMessage,
            isGameOver = isGameOver,
            humanWins = humanWins,
            computerWins = computerWins,
            ties = ties
        )
    }

    private fun startNewGame() {
        game.clearBoard()

        goFirst = if (goFirst == TicTacToeGame.HUMAN_PLAYER) {
            TicTacToeGame.COMPUTER_PLAYER
        } else {
            TicTacToeGame.HUMAN_PLAYER
        }

        val startMessage = if (goFirst == TicTacToeGame.COMPUTER_PLAYER) {
            "Android comienza..."
        } else {
            "¡Tu turno! Toca un cuadro para jugar"
        }

        _uiState.value = _uiState.value.copy(
            board = List(9) { ' ' },
            gameMessage = startMessage,
            isGameOver = false,
            isComputerTurn = goFirst == TicTacToeGame.COMPUTER_PLAYER
        )

        saveInstanceState()

        if (goFirst == TicTacToeGame.COMPUTER_PLAYER) {
            scheduleComputerMove()
        }
    }

    fun getDifficultyDisplayName(difficulty: TicTacToeGame.DifficultyLevel): String {
        return when (difficulty) {
            TicTacToeGame.DifficultyLevel.Easy -> "Fácil"
            TicTacToeGame.DifficultyLevel.Harder -> "Difícil"
            TicTacToeGame.DifficultyLevel.Expert -> "Experto"
        }
    }
}