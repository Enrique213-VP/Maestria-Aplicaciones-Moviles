package com.svape.masterunalapp.data

import kotlin.random.Random

class TicTacToeGame {

    companion object {
        const val HUMAN_PLAYER = 'X'
        const val COMPUTER_PLAYER = 'O'
        const val OPEN_SPOT = ' '
        const val BOARD_SIZE = 9
    }

    // Niveles de dificultad de la computadora
    enum class DifficultyLevel { Easy, Harder, Expert }

    // Nivel de dificultad actual
    private var mDifficultyLevel = DifficultyLevel.Expert

    private val mBoard = CharArray(BOARD_SIZE) { OPEN_SPOT }
    private val mRand = Random

    fun clearBoard() {
        for (i in 0 until BOARD_SIZE) {
            mBoard[i] = OPEN_SPOT
        }
    }

    fun setMove(player: Char, location: Int): Boolean {
        return if (location in 0 until BOARD_SIZE && mBoard[location] == OPEN_SPOT) {
            mBoard[location] = player
            true
        } else {
            false
        }
    }

    // Getters y setters para el nivel de dificultad
    fun getDifficultyLevel(): DifficultyLevel {
        return mDifficultyLevel
    }

    fun setDifficultyLevel(difficultyLevel: DifficultyLevel) {
        mDifficultyLevel = difficultyLevel
    }

    fun getComputerMove(): Int {
        return when (mDifficultyLevel) {
            DifficultyLevel.Easy -> getRandomMove()
            DifficultyLevel.Harder -> {
                val move = getWinningMove()
                if (move == -1) getRandomMove() else move
            }
            DifficultyLevel.Expert -> {
                // Intentar ganar, pero si no es posible, bloquear
                // Si eso no es posible, moverse a cualquier lugar
                var move = getWinningMove()
                if (move == -1) move = getBlockingMove()
                if (move == -1) move = getRandomMove()
                move
            }
        }
    }

    private fun getRandomMove(): Int {
        var move: Int
        do {
            move = mRand.nextInt(BOARD_SIZE)
        } while (mBoard[move] != OPEN_SPOT)
        return move
    }

    private fun getWinningMove(): Int {
        for (i in 0 until BOARD_SIZE) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = COMPUTER_PLAYER
                if (checkForWinner() == 3) {
                    mBoard[i] = OPEN_SPOT
                    return i
                }
                mBoard[i] = OPEN_SPOT
            }
        }
        return -1
    }

    private fun getBlockingMove(): Int {
        for (i in 0 until BOARD_SIZE) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = HUMAN_PLAYER
                if (checkForWinner() == 2) {
                    mBoard[i] = OPEN_SPOT
                    return i
                }
                mBoard[i] = OPEN_SPOT
            }
        }
        return -1
    }

    fun checkForWinner(): Int {
        // Verificar horizontales
        for (i in 0..6 step 3) {
            if (mBoard[i] == HUMAN_PLAYER &&
                mBoard[i+1] == HUMAN_PLAYER &&
                mBoard[i+2] == HUMAN_PLAYER)
                return 2
            if (mBoard[i] == COMPUTER_PLAYER &&
                mBoard[i+1] == COMPUTER_PLAYER &&
                mBoard[i+2] == COMPUTER_PLAYER)
                return 3
        }

        // Verificar verticales
        for (i in 0..2) {
            if (mBoard[i] == HUMAN_PLAYER &&
                mBoard[i+3] == HUMAN_PLAYER &&
                mBoard[i+6] == HUMAN_PLAYER)
                return 2
            if (mBoard[i] == COMPUTER_PLAYER &&
                mBoard[i+3] == COMPUTER_PLAYER &&
                mBoard[i+6] == COMPUTER_PLAYER)
                return 3
        }

        // Verificar diagonales
        if ((mBoard[0] == HUMAN_PLAYER &&
                    mBoard[4] == HUMAN_PLAYER &&
                    mBoard[8] == HUMAN_PLAYER) ||
            (mBoard[2] == HUMAN_PLAYER &&
                    mBoard[4] == HUMAN_PLAYER &&
                    mBoard[6] == HUMAN_PLAYER))
            return 2

        if ((mBoard[0] == COMPUTER_PLAYER &&
                    mBoard[4] == COMPUTER_PLAYER &&
                    mBoard[8] == COMPUTER_PLAYER) ||
            (mBoard[2] == COMPUTER_PLAYER &&
                    mBoard[4] == COMPUTER_PLAYER &&
                    mBoard[6] == COMPUTER_PLAYER))
            return 3

        // Verificar empate
        for (i in 0 until BOARD_SIZE) {
            if (mBoard[i] == OPEN_SPOT)
                return 0
        }

        return 1
    }

    fun getBoardOccupant(location: Int): Char {
        return if (location in 0 until BOARD_SIZE) {
            mBoard[location]
        } else {
            OPEN_SPOT
        }
    }
}