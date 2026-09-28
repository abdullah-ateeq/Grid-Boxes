package com.example.gridboxes.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gridboxes.ui.components.GridTopAppBar
import com.example.gridboxes.ui.theme.LocalGridBoxesPalette

@Composable
fun HowToPlayScreen(
    onBackClick: () -> Unit
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
            modifier = Modifier.fillMaxSize()
        ) {
            GridTopAppBar(
                title = "How to Play",
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Interactive / Visual Diagram Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(palette.surface.copy(alpha = 0.7f))
                        .border(1.dp, palette.surfaceBorder, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "COMPLETING A BOX",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.accentGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Visual Diagram of a Cell
                        Canvas(
                            modifier = Modifier.size(120.dp)
                        ) {
                            val pad = 16.dp.toPx()
                            val boxSide = size.width - 2 * pad
                            val strokeW = 6.dp.toPx()
                            val dotR = 6.dp.toPx()

                            // Claimed Cell Fill
                            drawRoundRect(
                                color = palette.playerOneFill,
                                topLeft = Offset(pad, pad),
                                size = Size(boxSide, boxSide),
                                cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                            )

                            // Center Diamond Symbol
                            val cx = size.width / 2
                            val cy = size.height / 2
                            val dSize = 14.dp.toPx()
                            val diamondPath = Path().apply {
                                moveTo(cx, cy - dSize)
                                lineTo(cx + dSize, cy)
                                lineTo(cx, cy + dSize)
                                lineTo(cx - dSize, cy)
                                close()
                            }
                            drawPath(diamondPath, palette.playerOnePrimary, style = Fill)

                            // Top, Right, Bottom drawn earlier
                            drawLine(palette.borderUsed, Offset(pad, pad), Offset(pad + boxSide, pad), strokeW, StrokeCap.Round)
                            drawLine(palette.borderUsed, Offset(pad + boxSide, pad), Offset(pad + boxSide, pad + boxSide), strokeW, StrokeCap.Round)
                            drawLine(palette.borderUsed, Offset(pad + boxSide, pad + boxSide), Offset(pad, pad + boxSide), strokeW, StrokeCap.Round)

                            // 4th Final Closing Border (Highlighted in Player 1 copper)
                            drawLine(palette.playerOnePrimary, Offset(pad, pad + boxSide), Offset(pad, pad), strokeW, StrokeCap.Round)

                            // Corners
                            val corners = listOf(
                                Offset(pad, pad),
                                Offset(pad + boxSide, pad),
                                Offset(pad + boxSide, pad + boxSide),
                                Offset(pad, pad + boxSide)
                            )
                            corners.forEach { pt ->
                                drawCircle(palette.gridAnchor, dotR, pt)
                                drawCircle(palette.boardSurface, dotR * 0.45f, pt)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Drawing the 4th border claims the cell permanently.",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // Rule Items
                RuleCard(
                    number = "1",
                    title = "Take Turns Drawing Borders",
                    description = "Players alternate turns selecting one unused border on the 5×5 grid. Borders may be horizontal or vertical."
                )

                RuleCard(
                    number = "2",
                    title = "Claiming Cells",
                    description = "When a player draws the fourth and final border around a cell, they claim that box and earn +1 point."
                )

                RuleCard(
                    number = "3",
                    title = "Extra Turn Bonus",
                    description = "Whenever you complete a cell, you immediately receive another turn. Chain multiple boxes together to build momentum!"
                )

                RuleCard(
                    number = "4",
                    title = "Double-Box Completion",
                    description = "A single border placed between two nearly-finished cells completes both at once! You claim both (+2 points) and continue your turn."
                )

                RuleCard(
                    number = "5",
                    title = "Permanent Ownership & Victory",
                    description = "Once claimed, cells can never be stolen. The game ends when all 25 cells are claimed. The player with the highest score wins!"
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun RuleCard(
    number: String,
    title: String,
    description: String
) {
    val palette = LocalGridBoxesPalette.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surface.copy(alpha = 0.5f))
            .border(1.dp, palette.surfaceBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            // Number Badge
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(palette.playerOnePrimary.copy(alpha = 0.15f))
                    .border(1.dp, palette.playerOnePrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.playerOnePrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
