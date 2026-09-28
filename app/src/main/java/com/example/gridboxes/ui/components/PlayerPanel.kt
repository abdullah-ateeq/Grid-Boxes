package com.example.gridboxes.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gridboxes.model.Player
import com.example.gridboxes.ui.theme.LocalGridBoxesPalette

@Composable
fun PlayerPanel(
    player: Player,
    score: Int,
    isCurrentTurn: Boolean,
    modifier: Modifier = Modifier
) {
    val palette = LocalGridBoxesPalette.current

    val primaryColor = if (player == Player.PLAYER_ONE) palette.playerOnePrimary else palette.playerTwoPrimary
    val containerBg = if (player == Player.PLAYER_ONE) palette.playerOneContainer else palette.playerTwoContainer

    val borderColor by animateColorAsState(
        targetValue = if (isCurrentTurn) primaryColor else palette.surfaceBorder,
        animationSpec = tween(300),
        label = "borderColor"
    )

    val surfaceColor by animateColorAsState(
        targetValue = if (isCurrentTurn) containerBg.copy(alpha = 0.55f) else palette.surface.copy(alpha = 0.7f),
        animationSpec = tween(300),
        label = "surfaceColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceColor)
            .border(if (isCurrentTurn) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .semantics {
                contentDescription = "${player.displayName}, ${if (isCurrentTurn) "Current Turn, " else ""}$score boxes"
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Player identity: Symbol + Name + Turn indicator
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Geometric Symbol Box
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(primaryColor.copy(alpha = 0.2f))
                        .border(1.dp, primaryColor, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (player == Player.PLAYER_ONE) {
                        // Diamond
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .rotate(45f)
                                .background(primaryColor)
                        )
                    } else {
                        // Circle
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(primaryColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = player.displayName.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isCurrentTurn) primaryColor else palette.textPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    if (isCurrentTurn) {
                        Text(
                            text = "TURN ACTIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = primaryColor,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    } else {
                        Text(
                            text = "WAITING",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textMuted,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Score Display
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "BOXES",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary,
                    modifier = Modifier.padding(bottom = 4.dp, end = 6.dp),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isCurrentTurn) primaryColor else palette.textPrimary
                )
            }
        }
    }
}
