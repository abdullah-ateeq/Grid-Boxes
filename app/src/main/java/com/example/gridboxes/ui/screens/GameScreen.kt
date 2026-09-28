package com.example.gridboxes.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gridboxes.model.BorderKey
import com.example.gridboxes.model.GridBoxesState
import com.example.gridboxes.model.Player
import com.example.gridboxes.ui.components.GameBoard
import com.example.gridboxes.ui.components.GridConfirmDialog
import com.example.gridboxes.ui.components.PlayerPanel
import com.example.gridboxes.ui.components.TurnBanner
import com.example.gridboxes.ui.theme.LocalGridBoxesPalette

@Composable
fun GameScreen(
    state: GridBoxesState,
    canUndo: Boolean,
    showLastBorder: Boolean,
    showBorderGuides: Boolean,
    onBorderTapped: (BorderKey) -> Unit,
    onUndoClick: () -> Unit,
    onRestartClick: () -> Unit,
    onHomeClick: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    val palette = LocalGridBoxesPalette.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var showRestartDialog by remember { mutableStateOf(false) }
    var showLeaveDialog by remember { mutableStateOf(false) }

    // When game finishes, wait a brief moment for the last cell animation then go to Result
    LaunchedEffect(state.isGameOver) {
        if (state.isGameOver) {
            kotlinx.coroutines.delay(1000)
            onNavigateToResult()
        }
    }

    if (showRestartDialog) {
        GridConfirmDialog(
            title = "Restart Match",
            message = "Are you sure you want to restart this match? Current progress will be lost.",
            confirmText = "Restart",
            onConfirm = { onRestartClick() },
            onDismiss = { showRestartDialog = false }
        )
    }

    if (showLeaveDialog) {
        GridConfirmDialog(
            title = "Leave Match",
            message = "Return to Home? Current match progress will be reset.",
            confirmText = "Leave",
            onConfirm = { onHomeClick() },
            onDismiss = { showLeaveDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (isLandscape) {
            // Landscape / Tablet Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Board on Left
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    GameBoard(
                        state = state,
                        showLastBorder = showLastBorder,
                        showBorderGuides = showBorderGuides,
                        onBorderTapped = onBorderTapped,
                        modifier = Modifier.fillMaxHeight()
                    )

                    TurnBanner(
                        message = state.bannerMessage,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Controls & Player Panels on Right
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    PlayerPanel(
                        player = Player.PLAYER_TWO,
                        score = state.player2Score,
                        isCurrentTurn = state.currentPlayer == Player.PLAYER_TWO && !state.isGameOver
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PlayerPanel(
                        player = Player.PLAYER_ONE,
                        score = state.player1Score,
                        isCurrentTurn = state.currentPlayer == Player.PLAYER_ONE && !state.isGameOver
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    GameControlButtons(
                        canUndo = canUndo,
                        onUndoClick = onUndoClick,
                        onRestartClick = { showRestartDialog = true },
                        onHomeClick = {
                            if (state.moveCount > 0 && !state.isGameOver) {
                                showLeaveDialog = true
                            } else {
                                onHomeClick()
                            }
                        }
                    )
                }
            }
        } else {
            // Portrait Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header: Navigation & Restart
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (state.moveCount > 0 && !state.isGameOver) {
                                showLeaveDialog = true
                            } else {
                                onHomeClick()
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(palette.surface.copy(alpha = 0.6f))
                            .border(1.dp, palette.surfaceBorder, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = palette.textSecondary
                        )
                    }

                    Text(
                        text = if (state.currentPlayer == Player.PLAYER_ONE) "PLAYER 1'S TURN" else "PLAYER 2'S TURN",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (state.currentPlayer == Player.PLAYER_ONE) palette.playerOnePrimary else palette.playerTwoPrimary,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { showRestartDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(palette.surface.copy(alpha = 0.6f))
                            .border(1.dp, palette.surfaceBorder, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart",
                            tint = palette.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Player 2 Panel (Top)
                PlayerPanel(
                    player = Player.PLAYER_TWO,
                    score = state.player2Score,
                    isCurrentTurn = state.currentPlayer == Player.PLAYER_TWO && !state.isGameOver
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Central Board
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    contentAlignment = Alignment.Center
                ) {
                    GameBoard(
                        state = state,
                        showLastBorder = showLastBorder,
                        showBorderGuides = showBorderGuides,
                        onBorderTapped = onBorderTapped,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TurnBanner(
                        message = state.bannerMessage,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Player 1 Panel (Bottom)
                PlayerPanel(
                    player = Player.PLAYER_ONE,
                    score = state.player1Score,
                    isCurrentTurn = state.currentPlayer == Player.PLAYER_ONE && !state.isGameOver
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Controls: Undo & Match info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MOVE ${state.moveCount} • ${state.drawnBorders.size}/60 BORDERS",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted,
                        letterSpacing = 1.sp
                    )

                    GameActionButton(
                        icon = Icons.AutoMirrored.Filled.Undo,
                        label = "UNDO",
                        enabled = canUndo,
                        onClick = onUndoClick
                    )
                }
            }
        }
    }
}

@Composable
private fun GameControlButtons(
    canUndo: Boolean,
    onUndoClick: () -> Unit,
    onRestartClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            GameActionButton(
                icon = Icons.AutoMirrored.Filled.Undo,
                label = "UNDO",
                enabled = canUndo,
                onClick = onUndoClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            GameActionButton(
                icon = Icons.Default.Refresh,
                label = "RESTART",
                enabled = true,
                onClick = onRestartClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            GameActionButton(
                icon = Icons.Default.Home,
                label = "HOME",
                enabled = true,
                onClick = onHomeClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GameActionButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGridBoxesPalette.current

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) palette.surface.copy(alpha = 0.8f) else palette.surface.copy(alpha = 0.3f))
            .border(1.dp, if (enabled) palette.surfaceBorder else palette.surfaceBorder.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (enabled) palette.textPrimary else palette.textMuted,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (enabled) palette.textPrimary else palette.textMuted,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
