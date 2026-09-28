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
import androidx.compose.foundation.rememberScrollState
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
import com.example.gridboxes.data.GameStats
import com.example.gridboxes.ui.components.GridPrimaryButton
import com.example.gridboxes.ui.components.GridSecondaryButton
import com.example.gridboxes.ui.theme.LocalGridBoxesPalette

@Composable
fun HomeScreen(
    stats: GameStats,
    onPlayClick: () -> Unit,
    onHowToPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val palette = LocalGridBoxesPalette.current

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
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 28.dp)
            ) {
                // Geometric Logo Badge
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(45f)
                            .background(palette.playerOnePrimary)
                    )
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(palette.playerTwoPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "GRID BOXES",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Draw the border. Complete the box.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = palette.textSecondary,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )
            }

            // Statistics Summary Card (if games played > 0)
            if (stats.gamesPlayed > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(palette.surface.copy(alpha = 0.6f))
                        .border(1.dp, palette.surfaceBorder, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "STATISTICS",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatItem(label = "PLAYED", value = "${stats.gamesPlayed}")
                            StatItem(label = "P1 WINS", value = "${stats.player1Wins}", color = palette.playerOnePrimary)
                            StatItem(label = "P2 WINS", value = "${stats.player2Wins}", color = palette.playerTwoPrimary)
                            StatItem(label = "DRAWS", value = "${stats.draws}")
                            StatItem(label = "BEST", value = "${stats.highestScore}", color = palette.accentGold)
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Actions Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GridPrimaryButton(
                    text = "PLAY",
                    onClick = onPlayClick
                )

                GridSecondaryButton(
                    text = "How to Play",
                    onClick = onHowToPlayClick
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        GridSecondaryButton(
                            text = "Settings",
                            onClick = onSettingsClick
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        GridSecondaryButton(
                            text = "About",
                            onClick = onAboutClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color? = null
) {
    val palette = LocalGridBoxesPalette.current
    val textColor = color ?: palette.textPrimary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = palette.textMuted,
            fontSize = 9.sp,
            letterSpacing = 0.5.sp
        )
    }
}
