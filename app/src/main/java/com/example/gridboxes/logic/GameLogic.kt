package com.example.gridboxes.logic

import com.example.gridboxes.model.BorderKey
import com.example.gridboxes.model.BorderOrientation
import com.example.gridboxes.model.CellPosition
import com.example.gridboxes.model.GridBoxesState
import com.example.gridboxes.model.Player

object GameLogic {
    const val GRID_SIZE = 5
    const val CORNERS_COUNT = 6
    const val TOTAL_CELLS = GRID_SIZE * GRID_SIZE // 25
    const val TOTAL_HORIZONTAL_BORDERS = (GRID_SIZE + 1) * GRID_SIZE // 6 * 5 = 30
    const val TOTAL_VERTICAL_BORDERS = GRID_SIZE * (GRID_SIZE + 1) // 5 * 6 = 30
    const val TOTAL_BORDERS = TOTAL_HORIZONTAL_BORDERS + TOTAL_VERTICAL_BORDERS // 60

    fun isValidBorder(border: BorderKey): Boolean {
        return when (border.orientation) {
            BorderOrientation.HORIZONTAL -> {
                border.row in 0..GRID_SIZE && border.col in 0 until GRID_SIZE
            }
            BorderOrientation.VERTICAL -> {
                border.row in 0 until GRID_SIZE && border.col in 0..GRID_SIZE
            }
        }
    }

    fun getCellBorders(cell: CellPosition): Set<BorderKey> {
        require(cell.row in 0 until GRID_SIZE && cell.col in 0 until GRID_SIZE) {
            "Cell position out of bounds: (${cell.row}, ${cell.col})"
        }
        return setOf(
            BorderKey(cell.row, cell.col, BorderOrientation.HORIZONTAL),
            BorderKey(cell.row + 1, cell.col, BorderOrientation.HORIZONTAL),
            BorderKey(cell.row, cell.col, BorderOrientation.VERTICAL),
            BorderKey(cell.row, cell.col + 1, BorderOrientation.VERTICAL)
        )
    }

    fun getAdjacentCells(border: BorderKey): List<CellPosition> {
        val adjacent = mutableListOf<CellPosition>()
        when (border.orientation) {
            BorderOrientation.HORIZONTAL -> {
                // Border touches cell above if row > 0
                if (border.row > 0) {
                    adjacent.add(CellPosition(border.row - 1, border.col))
                }
                // Border touches cell below if row < GRID_SIZE
                if (border.row < GRID_SIZE) {
                    adjacent.add(CellPosition(border.row, border.col))
                }
            }
            BorderOrientation.VERTICAL -> {
                // Border touches cell to the left if col > 0
                if (border.col > 0) {
                    adjacent.add(CellPosition(border.row, border.col - 1))
                }
                // Border touches cell to the right if col < GRID_SIZE
                if (border.col < GRID_SIZE) {
                    adjacent.add(CellPosition(border.row, border.col))
                }
            }
        }
        return adjacent
    }

    fun getNewlyCompletedCells(
        drawnBorders: Set<BorderKey>,
        claimedCells: Map<CellPosition, Player>,
        border: BorderKey
    ): List<CellPosition> {
        val adjacent = getAdjacentCells(border)
        val combinedBorders = drawnBorders + border
        val completed = mutableListOf<CellPosition>()

        for (cell in adjacent) {
            if (cell !in claimedCells) {
                val requiredBorders = getCellBorders(cell)
                if (combinedBorders.containsAll(requiredBorders)) {
                    completed.add(cell)
                }
            }
        }
        return completed
    }

    sealed class MoveExecutionResult {
        object Rejected : MoveExecutionResult()
        data class Success(
            val newState: GridBoxesState,
            val completedCells: List<CellPosition>,
            val grantedExtraTurn: Boolean,
            val isGameOver: Boolean
        ) : MoveExecutionResult()
    }

    fun applyMove(
        currentState: GridBoxesState,
        border: BorderKey,
        currentTimeMs: Long = System.currentTimeMillis()
    ): MoveExecutionResult {
        if (currentState.isGameOver || currentState.isAnimating) {
            return MoveExecutionResult.Rejected
        }
        if (!isValidBorder(border)) {
            return MoveExecutionResult.Rejected
        }
        if (border in currentState.drawnBorders) {
            return MoveExecutionResult.Rejected
        }

        val newlyCompleted = getNewlyCompletedCells(
            drawnBorders = currentState.drawnBorders,
            claimedCells = currentState.claimedCells,
            border = border
        )

        val newDrawnBorders = currentState.drawnBorders + border
        val newBorderOwners = currentState.borderOwners + (border to currentState.currentPlayer)
        val newClaimedCells = currentState.claimedCells.toMutableMap()
        for (cell in newlyCompleted) {
            newClaimedCells[cell] = currentState.currentPlayer
        }

        val scoreAddition = newlyCompleted.size
        var p1Score = currentState.player1Score
        var p2Score = currentState.player2Score

        if (currentState.currentPlayer == Player.PLAYER_ONE) {
            p1Score += scoreAddition
        } else {
            p2Score += scoreAddition
        }

        val doubleBoxCount = if (newlyCompleted.size == 2) {
            currentState.doubleBoxMoves + 1
        } else {
            currentState.doubleBoxMoves
        }

        val isGameOver = (newClaimedCells.size == TOTAL_CELLS) || (newDrawnBorders.size == TOTAL_BORDERS)

        val nextPlayer: Player
        val bannerMessage: String?
        val grantedExtraTurn: Boolean

        if (isGameOver) {
            nextPlayer = currentState.currentPlayer
            bannerMessage = null
            grantedExtraTurn = false
        } else if (newlyCompleted.isNotEmpty()) {
            // Completing at least one cell grants another turn to the same player
            nextPlayer = currentState.currentPlayer
            grantedExtraTurn = true
            bannerMessage = if (newlyCompleted.size == 2) "DOUBLE BOX! EXTRA TURN" else "EXTRA TURN"
        } else {
            // No cell completed: switch player
            nextPlayer = currentState.currentPlayer.other()
            grantedExtraTurn = false
            bannerMessage = null
        }

        val winner: Player?
        val isDraw: Boolean
        val durationSeconds: Long

        if (isGameOver) {
            when {
                p1Score > p2Score -> {
                    winner = Player.PLAYER_ONE
                    isDraw = false
                }
                p2Score > p1Score -> {
                    winner = Player.PLAYER_TWO
                    isDraw = false
                }
                else -> {
                    winner = null
                    isDraw = true
                }
            }
            durationSeconds = ((currentTimeMs - currentState.matchStartTimeMs) / 1000).coerceAtLeast(0L)
        } else {
            winner = null
            isDraw = false
            durationSeconds = ((currentTimeMs - currentState.matchStartTimeMs) / 1000).coerceAtLeast(0L)
        }

        val newState = currentState.copy(
            drawnBorders = newDrawnBorders,
            borderOwners = newBorderOwners,
            claimedCells = newClaimedCells,
            currentPlayer = nextPlayer,
            player1Score = p1Score,
            player2Score = p2Score,
            lastBorder = border,
            winner = winner,
            isDraw = isDraw,
            moveCount = currentState.moveCount + 1,
            doubleBoxMoves = doubleBoxCount,
            isGameOver = isGameOver,
            isAnimating = false,
            bannerMessage = bannerMessage,
            newlyClaimedInLastMove = newlyCompleted,
            matchDurationSeconds = durationSeconds
        )

        return MoveExecutionResult.Success(
            newState = newState,
            completedCells = newlyCompleted,
            grantedExtraTurn = grantedExtraTurn,
            isGameOver = isGameOver
        )
    }

    /**
     * Generates all 60 valid border keys on a 5x5 board.
     */
    fun getAllBorders(): List<BorderKey> {
        val list = mutableListOf<BorderKey>()
        for (r in 0..GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                list.add(BorderKey(r, c, BorderOrientation.HORIZONTAL))
            }
        }
        for (r in 0 until GRID_SIZE) {
            for (c in 0..GRID_SIZE) {
                list.add(BorderKey(r, c, BorderOrientation.VERTICAL))
            }
        }
        return list
    }

    /**
     * Generates all 25 valid cells on a 5x5 board.
     */
    fun getAllCells(): List<CellPosition> {
        val list = mutableListOf<CellPosition>()
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                list.add(CellPosition(r, c))
            }
        }
        return list
    }
}
