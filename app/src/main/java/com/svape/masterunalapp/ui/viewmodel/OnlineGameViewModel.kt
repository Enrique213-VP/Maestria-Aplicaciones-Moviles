package com.svape.masterunalapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svape.masterunalapp.data.model.GameStatus
import com.svape.masterunalapp.data.model.OnlineGame
import com.svape.masterunalapp.data.repository.FirebaseGameRepository
import com.svape.masterunalapp.ui.utils.SoundManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class OnlineGameUiState(
    val currentGame: OnlineGame? = null,
    val isMyTurn: Boolean = false,
    val mySymbol: String = "",
    val opponentName: String = "",
    val gameMessage: String = "Esperando...",
    val isGameOver: Boolean = false,
    val showWaitingDialog: Boolean = false
)

class OnlineGameViewModel : ViewModel() {

    private val repository = FirebaseGameRepository()
    private val _playerId = UUID.randomUUID().toString()
    private var soundManager: SoundManager? = null

    private val _availableGames = MutableStateFlow<List<OnlineGame>>(emptyList())
    val availableGames: StateFlow<List<OnlineGame>> = _availableGames.asStateFlow()

    private val _currentGameId = MutableStateFlow<String?>(null)
    val currentGameId: StateFlow<String?> = _currentGameId.asStateFlow()

    private val _uiState = MutableStateFlow(OnlineGameUiState())
    val uiState: StateFlow<OnlineGameUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadAvailableGames()
    }

    fun setSoundManager(manager: SoundManager) {
        soundManager = manager
    }

    private fun loadAvailableGames() {
        viewModelScope.launch {
            repository.getAvailableGames().collect { games ->
                _availableGames.value = games
            }
        }
    }

    fun createGame(playerName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.createGame(playerName, _playerId)

            result.fold(
                onSuccess = { game ->
                    _currentGameId.value = game.gameId
                    observeGame(game.gameId)
                    _uiState.value = _uiState.value.copy(
                        showWaitingDialog = true,
                        gameMessage = "Esperando oponente..."
                    )
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al crear juego"
                }
            )

            _isLoading.value = false
        }
    }

    fun joinGame(gameId: String, playerName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.joinGame(gameId, playerName, _playerId)

            result.fold(
                onSuccess = { game ->
                    _currentGameId.value = game.gameId
                    observeGame(game.gameId)
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al unirse al juego"
                }
            )

            _isLoading.value = false
        }
    }

    private fun observeGame(gameId: String) {
        viewModelScope.launch {
            var previousBoard: List<String>? = null

            repository.observeGame(gameId).collect { game ->
                if (game != null) {
                    if (previousBoard != null && previousBoard != game.board) {
                        val isMyMove = game.currentTurn != _playerId
                        if (isMyMove) {
                            soundManager?.playHumanMoveSound()
                        } else {
                            soundManager?.playComputerMoveSound()
                        }
                    }
                    previousBoard = game.board
                    updateUiState(game)
                }
            }
        }
    }

    private fun updateUiState(game: OnlineGame) {
        val isPlayer1 = _playerId == game.player1?.playerId
        val mySymbol = if (isPlayer1) "X" else "O"
        val isMyTurn = game.currentTurn == _playerId
        val opponentName = if (isPlayer1) {
            game.player2?.playerName ?: "Esperando..."
        } else {
            game.player1?.playerName ?: "Jugador 1"
        }

        val gameMessage = when {
            game.status == GameStatus.WAITING -> "Esperando oponente..."
            game.status == GameStatus.FINISHED -> {
                when (game.winner) {
                    mySymbol -> "¡Ganaste! 🎉"
                    null -> "¡Empate!"
                    else -> "Perdiste 😢"
                }
            }
            isMyTurn -> "¡Tu turno!"
            else -> "Turno de $opponentName"
        }

        _uiState.value = OnlineGameUiState(
            currentGame = game,
            isMyTurn = isMyTurn,
            mySymbol = mySymbol,
            opponentName = opponentName,
            gameMessage = gameMessage,
            isGameOver = game.status == GameStatus.FINISHED,
            showWaitingDialog = game.status == GameStatus.WAITING
        )
    }

    fun makeMove(position: Int) {
        val gameId = _currentGameId.value ?: return

        android.util.Log.d("OnlineGame", "makeMove llamado en posición: $position")

        //soundManager?.playHumanMoveSound()

        viewModelScope.launch {
            _errorMessage.value = null

            val result = repository.makeMove(gameId, position, _playerId)

            result.onSuccess {
                android.util.Log.d("OnlineGame", "Movimiento exitoso")
            }

            result.onFailure { exception ->
                android.util.Log.e("OnlineGame", "Error en movimiento: ${exception.message}")
                _errorMessage.value = exception.message ?: "Error al hacer movimiento"
            }
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        soundManager?.setSoundEnabled(enabled)
    }

    fun leaveGame() {
        val gameId = _currentGameId.value ?: return

        viewModelScope.launch {
            repository.leaveGame(gameId)
            _currentGameId.value = null
            _uiState.value = OnlineGameUiState()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun getPlayerId(): String = _playerId
}