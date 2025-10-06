package com.svape.masterunalapp.data.repository

import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase
import com.svape.masterunalapp.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseGameRepository {

    private val database = FirebaseDatabase.getInstance()
    private val gamesRef = database.getReference("games")

    suspend fun createGame(playerName: String, playerId: String): Result<OnlineGame> {
        return try {
            val gameId = gamesRef.push().key ?: return Result.failure(Exception("No se pudo generar ID"))

            val player1 = Player(
                playerId = playerId,
                playerName = playerName,
                symbol = "X"
            )

            val game = OnlineGame(
                gameId = gameId,
                createdBy = playerId,
                player1 = player1,
                currentTurn = playerId,
                status = GameStatus.WAITING
            )

            gamesRef.child(gameId).setValue(game).await()
            Result.success(game)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinGame(gameId: String, playerName: String, playerId: String): Result<OnlineGame> {
        return try {
            val snapshot = gamesRef.child(gameId).get().await()
            val game = snapshot.getValue(OnlineGame::class.java)
                ?: return Result.failure(Exception("Juego no encontrado"))

            if (game.status != GameStatus.WAITING) {
                return Result.failure(Exception("El juego ya está en curso o terminado"))
            }

            if (game.player2 != null) {
                return Result.failure(Exception("El juego ya está lleno"))
            }

            val player2 = Player(
                playerId = playerId,
                playerName = playerName,
                symbol = "O"
            )

            val updatedGame = game.copy(
                player2 = player2,
                status = GameStatus.PLAYING
            )

            gamesRef.child(gameId).setValue(updatedGame).await()
            Result.success(updatedGame)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAvailableGames(): Flow<List<OnlineGame>> = callbackFlow {
        val query = gamesRef.orderByChild("status").equalTo(GameStatus.WAITING.name)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val games = mutableListOf<OnlineGame>()
                snapshot.children.forEach { child ->
                    child.getValue(OnlineGame::class.java)?.let { game ->
                        games.add(game)
                    }
                }
                trySend(games.sortedByDescending { it.createdAt })
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    fun observeGame(gameId: String): Flow<OnlineGame?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val game = snapshot.getValue(OnlineGame::class.java)
                trySend(game)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        gamesRef.child(gameId).addValueEventListener(listener)
        awaitClose { gamesRef.child(gameId).removeEventListener(listener) }
    }

    suspend fun makeMove(gameId: String, position: Int, playerId: String): Result<Boolean> {
        return try {
            val snapshot = gamesRef.child(gameId).get().await()
            val game = snapshot.getValue(OnlineGame::class.java)
                ?: return Result.failure(Exception("Juego no encontrado"))

            // Validaciones
            if (game.status != GameStatus.PLAYING) {
                return Result.failure(Exception("El juego no está en curso"))
            }

            if (game.currentTurn != playerId) {
                return Result.failure(Exception("No es tu turno"))
            }

            if (game.board[position] != " ") {
                return Result.failure(Exception("Posición ocupada"))
            }

            val newBoard = game.board.toMutableList()
            val playerSymbol = if (playerId == game.player1?.playerId) "X" else "O"
            newBoard[position] = playerSymbol

            val nextTurn = if (playerId == game.player1?.playerId) {
                game.player2?.playerId ?: ""
            } else {
                game.player1?.playerId ?: ""
            }

            val winner = checkWinner(newBoard)
            val newStatus = if (winner != null || !newBoard.contains(" ")) {
                GameStatus.FINISHED
            } else {
                GameStatus.PLAYING
            }

            val updatedGame = game.copy(
                board = newBoard,
                currentTurn = nextTurn,
                status = newStatus,
                winner = winner
            )

            gamesRef.child(gameId).setValue(updatedGame).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun leaveGame(gameId: String): Result<Boolean> {
        return try {
            gamesRef.child(gameId).removeValue().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun checkWinner(board: List<String>): String? {
        val winPatterns = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Filas
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Columnas
            listOf(0, 4, 8), listOf(2, 4, 6) // Diagonales
        )

        for (pattern in winPatterns) {
            val (a, b, c) = pattern
            if (board[a] != " " && board[a] == board[b] && board[b] == board[c]) {
                return board[a] // Retorna "X" o "O"
            }
        }

        return null
    }
}