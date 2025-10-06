package com.svape.masterunalapp.data.model

data class OnlineGame(
    val gameId: String = "",
    val createdBy: String = "",
    val player1: Player? = null,
    val player2: Player? = null,
    val board: List<String> = List(9) { " " },
    val currentTurn: String = "",
    val status: GameStatus = GameStatus.WAITING,
    val winner: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class Player(
    val playerId: String = "",
    val playerName: String = "",
    val symbol: String = ""
)

enum class GameStatus {
    WAITING,
    PLAYING,
    FINISHED
}

data class GameUpdate(
    val gameId: String = "",
    val position: Int = -1,
    val playerId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)