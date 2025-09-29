package com.svape.masterunalapp.ui.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun CustomTicTacToeBoard(
    board: List<Char>,
    onCellClick: (Int) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.size(320.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .pointerInput(enabled) {
                    if (enabled) {
                        detectTapGestures { offset ->
                            val cellWidth = size.width / 3f
                            val cellHeight = size.height / 3f
                            val col = (offset.x / cellWidth).toInt()
                            val row = (offset.y / cellHeight).toInt()
                            val position = row * 3 + col

                            if (position in 0..8) {
                                onCellClick(position)
                            }
                        }
                    }
                }
        ) {
            drawTicTacToeBoard(board = board)
        }
    }
}

private fun DrawScope.drawTicTacToeBoard(
    board: List<Char>
) {
    val gridWidth = 6.dp.toPx()
    val cellWidth = size.width / 3f
    val cellHeight = size.height / 3f

    // Dibujar líneas verticales
    drawLine(
        color = Color.LightGray,
        start = Offset(cellWidth, 0f),
        end = Offset(cellWidth, size.height),
        strokeWidth = gridWidth
    )
    drawLine(
        color = Color.LightGray,
        start = Offset(cellWidth * 2, 0f),
        end = Offset(cellWidth * 2, size.height),
        strokeWidth = gridWidth
    )

    // Dibujar líneas horizontales
    drawLine(
        color = Color.LightGray,
        start = Offset(0f, cellHeight),
        end = Offset(size.width, cellHeight),
        strokeWidth = gridWidth
    )
    drawLine(
        color = Color.LightGray,
        start = Offset(0f, cellHeight * 2),
        end = Offset(size.width, cellHeight * 2),
        strokeWidth = gridWidth
    )

    // Dibujar X's y O's
    for (i in board.indices) {
        val col = i % 3
        val row = i / 3

        val centerX = col * cellWidth + cellWidth / 2
        val centerY = row * cellHeight + cellHeight / 2
        val symbolSize = cellWidth * 0.3f

        when (board[i]) {
            'X' -> {
                drawX(centerX, centerY, symbolSize)
            }
            'O' -> {
                drawO(centerX, centerY, symbolSize)
            }
        }
    }
}

private fun DrawScope.drawX(centerX: Float, centerY: Float, size: Float) {
    val strokeWidth = 8.dp.toPx()

    // Línea diagonal de arriba-izquierda a abajo-derecha
    drawLine(
        color = Color(0xFF2E7D32),
        start = Offset(centerX - size, centerY - size),
        end = Offset(centerX + size, centerY + size),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )

    // Línea diagonal de arriba-derecha a abajo-izquierda
    drawLine(
        color = Color(0xFF2E7D32),
        start = Offset(centerX + size, centerY - size),
        end = Offset(centerX - size, centerY + size),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawO(centerX: Float, centerY: Float, size: Float) {
    val strokeWidth = 8.dp.toPx()

    drawCircle(
        color = Color(0xFFD32F2F),
        radius = size,
        center = Offset(centerX, centerY),
        style = Stroke(width = strokeWidth)
    )
}