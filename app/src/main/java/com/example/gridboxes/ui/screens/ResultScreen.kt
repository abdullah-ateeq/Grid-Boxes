package com.example.gridboxes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gridboxes.model.GridBoxesState
import com.example.gridboxes.model.Player
import com.example.gridboxes.ui.components.GridPrimaryButton
import com.example.gridboxes.ui.components.GridSecondaryButton
import com.example.gridboxes.ui.theme.LocalGridBoxesPalette
import kotlin.math.abs

@Composable
fun ResultScreen(
    state: GridBoxesState,
    onPlayAgainClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val palette = LocalGridBoxesPalette.current

    val titleText = when {
        state.isDraw -> "MATCH DRAW"
        state.winner == Player.PLAYER_ONE -> "PLAYER 1 WINS"
        state.winner == Player.PLAYER_TWO -> "PLAYER 2 WINS"
        else -> "GAME OVER"
    }

    val winnerColor = when {
        state.isDraw -> palette.accentGold
        state.winner == Player.PLAYER_ONE -> palette.playerOnePrimary
        state.winner == Player.PLAYER_TWO -> palette.playerTwoPrimary
        else -> palette.textPrimary
    }

    val winningMargin = abs(state.player1Score - state.player2Score)
    val minutes = state.matchDurationSeconds / 60
    val seconds = state.matchDurationSeconds % 60
    val formattedDuration = if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Victory Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 28.dp)
            ) {
                // Winner Symbol Emblem
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(winnerColor.copy(alpha = 0.15f))
                        .border(1.5.dp, winnerColor, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.winner == Player.PLAYER_ONE) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(45f)
                                .background(winnerColor)
                        )
                    } else if (state.winner == Player.PLAYER_TWO) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(winnerColor)
                        )
                    } else {
                        // Draw badge
                        Text(
                            text = "=",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = winnerColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = titleText,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = winnerColor,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (state.isDraw) "Evenly matched contest" else "Victory by claiming majority of the grid",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // Scores Comparison Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(palette.surface.copy(alpha = 0.8f))
                    .border(1.dp, palette.surfaceBorder, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Player 1
                        PlayerScoreColumn(
                            player = Player.PLAYER_ONE,
                            score = state.player1Score,
                            isWinner = state.winner == Player.PLAYER_ONE
                        )

                        // VS Divider
                        Text(
                            text = "VS",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textMuted,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )

                        // Player 2
                        PlayerScoreColumn(
                            player = Player.PLAYER_TWO,
                            score = state.player2Score,
                            isWinner = state.winner == Player.PLAYER_TWO
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(palette.surfaceBorder)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Detailed Match Statistics
                    ResultStatRow(label = "Total Moves", value = "${state.moveCount}")
                    ResultStatRow(label = "Double-Box Moves", value = "${state.doubleBoxMoves}")
                    ResultStatRow(label = "Winning Margin", value = if (state.isDraw) "0" else "$winningMargin boxes")
                    ResultStatRow(label = "Match Duration", value = formattedDuration)
                }
            }

            // Bottom Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GridPrimaryButton(
                    text = "PLAY AGAIN",
                    onClick = onPlayAgainClick,
                    accentColor = winnerColor
                )

                GridSecondaryButton(
                    text = "HOME",
                    onClick = onHomeClick
                )
            }
        }
    }
}

@Composable
private fun PlayerScoreColumn(
    player: Player,
    score: Int,
    isWinner: Boolean
) {
    val palette = LocalGridBoxesPalette.current
    val color = if (player == Player.PLAYER_ONE) palette.playerOnePrimary else palette.playerTwoPrimary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (player == Player.PLAYER_ONE) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .rotate(45f)
                        .background(color)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }

            Text(
                text = player.displayName.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$score",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.ExtraBold,
            color = if (isWinner) color else palette.textPrimary
        )

        Text(
            text = if (score == 1) "BOX" else "BOXES",
            style = MaterialTheme.typography.labelSmall,
            color = palette.textSecondary,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun ResultStatRow(
    label: String,
    value: String
) {
    val palette = LocalGridBoxesPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}
