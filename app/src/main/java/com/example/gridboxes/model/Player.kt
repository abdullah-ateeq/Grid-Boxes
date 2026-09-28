package com.example.gridboxes.model

enum class Player {
    PLAYER_ONE,
    PLAYER_TWO;

    fun other(): Player = if (this == PLAYER_ONE) PLAYER_TWO else PLAYER_ONE

    val displayName: String
        get() = if (this == PLAYER_ONE) "Player 1" else "Player 2"

    val symbol: String
        get() = if (this == PLAYER_ONE) "◆" else "●"
}
