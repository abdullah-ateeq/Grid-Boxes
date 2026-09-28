package com.example.gridboxes.model

data class GridBoxesState(
    val drawnBorders: Set<BorderKey> = emptySet(),
    val borderOwners: Map<BorderKey, Player> = emptyMap(),
    val claimedCells: Map<CellPosition, Player> = emptyMap(),
    val currentPlayer: Player = Player.PLAYER_ONE,
    val player1Score: Int = 0,
    val player2Score: Int = 0,
    val lastBorder: BorderKey? = null,
    val winner: Player? = null,
    val isDraw: Boolean = false,
    val moveCount: Int = 0,
    val doubleBoxMoves: Int = 0,
    val isGameOver: Boolean = false,
    val isAnimating: Boolean = false,
    val bannerMessage: String? = null,
    val newlyClaimedInLastMove: List<CellPosition> = emptyList(),
    val matchStartTimeMs: Long = System.currentTimeMillis(),
    val matchDurationSeconds: Long = 0L
)
