package com.example.gridboxes.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.gridboxes.logic.GameLogic
import com.example.gridboxes.model.BorderKey
import com.example.gridboxes.model.BorderOrientation
import com.example.gridboxes.model.CellPosition
import com.example.gridboxes.model.GridBoxesState
import com.example.gridboxes.model.Player
import com.example.gridboxes.ui.theme.LocalGridBoxesPalette
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun GameBoard(
    state: GridBoxesState,
    showLastBorder: Boolean,
    showBorderGuides: Boolean,
    onBorderTapped: (BorderKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGridBoxesPalette.current

    // Infinite animation for subtle pulsing glow on the last placed border
    val infiniteTransition = rememberInfiniteTransition(label = "lastBorderPulse")
    val pulseAlpha = infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lastBorderAlpha"
    )

    // Animation progress for newly claimed cells
    val cellClaimScale = remember { Animatable(1f) }
    LaunchedEffect(state.newlyClaimedInLastMove) {
        if (state.newlyClaimedInLastMove.isNotEmpty()) {
            cellClaimScale.snapTo(0.3f)
            cellClaimScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = palette.background,
                spotColor = Color.Black
            )
            .clip(RoundedCornerShape(20.dp))
            .background(palette.boardSurface)
            .border(1.dp, palette.surfaceBorder, RoundedCornerShape(20.dp))
            .padding(12.dp)
            .semantics {
                contentDescription = "Grid Boxes Board, 5 by 5 grid, ${state.drawnBorders.size} of 60 borders drawn, Player 1 has ${state.player1Score} boxes, Player 2 has ${state.player2Score} boxes"
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(state.isGameOver, state.isAnimating) {
                    if (state.isGameOver || state.isAnimating) return@pointerInput

                    detectTapGestures { tapOffset ->
                        val boardSide = min(size.width, size.height)
                        val inset = boardSide * 0.08f
                        val playable = boardSide - 2 * inset
                        val step = playable / GameLogic.GRID_SIZE

                        // Detect closest unused border within generous hit area
                        val unusedBorders = GameLogic.getAllBorders().filter { it !in state.drawnBorders }
                        var bestBorder: BorderKey? = null
                        var minDistanceSq = Float.MAX_VALUE
                        val hitThreshold = step * 0.48f

                        for (border in unusedBorders) {
                            val dist = distanceToBorder(border, tapOffset, inset, step)
                            if (dist < hitThreshold && dist < minDistanceSq) {
                                minDistanceSq = dist
                                bestBorder = border
                            }
                        }

                        bestBorder?.let { onBorderTapped(it) }
                    }
                }
        ) {
            val boardSide = min(size.width, size.height)
            val inset = boardSide * 0.08f
            val playable = boardSide - 2 * inset
            val step = playable / GameLogic.GRID_SIZE

            // Stroke widths responsive to board size
            val borderStrokeWidth = step * 0.11f
            val guideStrokeWidth = step * 0.035f
            val lastBorderGlowWidth = step * 0.18f
            val dotRadius = step * 0.09f
            val dotCenterRadius = step * 0.05f

            // 1. RENDER CLAIMED CELLS & SYMBOLS
            for (row in 0 until GameLogic.GRID_SIZE) {
                for (col in 0 until GameLogic.GRID_SIZE) {
                    val cell = CellPosition(row, col)
                    val owner = state.claimedCells[cell]
                    if (owner != null) {
                        val cellLeft = inset + col * step
                        val cellTop = inset + row * step
                        val cellPadding = step * 0.07f
                        val cellWidth = step - (cellPadding * 2)
                        val cellHeight = step - (cellPadding * 2)

                        val isNewlyClaimed = state.newlyClaimedInLastMove.contains(cell)
                        val currentScale = if (isNewlyClaimed) cellClaimScale.value else 1f

                        val fillColor = if (owner == Player.PLAYER_ONE) {
                            palette.playerOneFill
                        } else {
                            palette.playerTwoFill
                        }

                        val symbolColor = if (owner == Player.PLAYER_ONE) {
                            palette.playerOnePrimary
                        } else {
                            palette.playerTwoPrimary
                        }

                        // Soft rounded cell background
                        drawRoundRect(
                            color = fillColor,
                            topLeft = Offset(cellLeft + cellPadding, cellTop + cellPadding),
                            size = Size(cellWidth, cellHeight),
                            cornerRadius = CornerRadius(step * 0.12f, step * 0.12f)
                        )

                        // Center ownership symbol: Diamond for P1, Circle for P2
                        val centerX = cellLeft + step / 2
                        val centerY = cellTop + step / 2
                        val symbolSize = (step * 0.22f) * currentScale

                        if (owner == Player.PLAYER_ONE) {
                            // Geometric Diamond
                            val diamondPath = Path().apply {
                                moveTo(centerX, centerY - symbolSize)
                                lineTo(centerX + symbolSize, centerY)
                                lineTo(centerX, centerY + symbolSize)
                                lineTo(centerX - symbolSize, centerY)
                                close()
                            }
                            drawPath(
                                path = diamondPath,
                                color = symbolColor.copy(alpha = 0.85f),
                                style = Fill
                            )
                            drawPath(
                                path = diamondPath,
                                color = Color.White.copy(alpha = 0.5f),
                                style = Stroke(width = step * 0.025f)
                            )
                        } else {
                            // Geometric Circle / Ring
                            drawCircle(
                                color = symbolColor.copy(alpha = 0.85f),
                                radius = symbolSize * 0.9f,
                                center = Offset(centerX, centerY),
                                style = Fill
                            )
                            drawCircle(
                                color = Color.White.copy(alpha = 0.5f),
                                radius = symbolSize * 0.9f,
                                center = Offset(centerX, centerY),
                                style = Stroke(width = step * 0.025f)
                            )
                        }
                    }
                }
            }

            // 2. RENDER SUBTLE BORDER GUIDES (if enabled)
            if (showBorderGuides) {
                val allBorders = GameLogic.getAllBorders()
                for (border in allBorders) {
                    if (border !in state.drawnBorders) {
                        val (start, end) = getBorderCoordinates(border, inset, step)
                        drawLine(
                            color = palette.borderUnused,
                            start = start,
                            end = end,
                            strokeWidth = guideStrokeWidth,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            // 3. RENDER EXISTING DRAWN BORDERS
            for (border in state.drawnBorders) {
                val (start, end) = getBorderCoordinates(border, inset, step)
                val owner = state.borderOwners[border]
                val lineColor = if (owner == Player.PLAYER_ONE) {
                    palette.playerOnePrimary.copy(alpha = 0.95f)
                } else if (owner == Player.PLAYER_TWO) {
                    palette.playerTwoPrimary.copy(alpha = 0.95f)
                } else {
                    palette.borderUsed
                }

                drawLine(
                    color = lineColor,
                    start = start,
                    end = end,
                    strokeWidth = borderStrokeWidth,
                    cap = StrokeCap.Round
                )
            }

            // 4. RENDER LAST-MOVE INDICATION (if enabled)
            if (showLastBorder && state.lastBorder != null) {
                val last = state.lastBorder
                val (start, end) = getBorderCoordinates(last, inset, step)

                // Outer soft halo
                drawLine(
                    color = palette.lastMoveHighlight.copy(alpha = pulseAlpha.value * 0.65f),
                    start = start,
                    end = end,
                    strokeWidth = lastBorderGlowWidth,
                    cap = StrokeCap.Round
                )

                // Inner bright stroke
                drawLine(
                    color = Color.White.copy(alpha = pulseAlpha.value * 0.9f),
                    start = start,
                    end = end,
                    strokeWidth = borderStrokeWidth * 0.7f,
                    cap = StrokeCap.Round
                )
            }

            // 5. RENDER GRID ANCHOR POINTS / CORNERS (6x6)
            for (r in 0..GameLogic.GRID_SIZE) {
                for (c in 0..GameLogic.GRID_SIZE) {
                    val cx = inset + c * step
                    val cy = inset + r * step

                    // Outer anchor ring
                    drawCircle(
                        color = palette.gridAnchor,
                        radius = dotRadius,
                        center = Offset(cx, cy)
                    )

                    // Inner anchor core for depth
                    drawCircle(
                        color = palette.boardSurface,
                        radius = dotCenterRadius,
                        center = Offset(cx, cy)
                    )
                }
            }
        }
    }
}

/**
 * Calculates start and end point offsets for a border in canvas space.
 */
private fun getBorderCoordinates(
    border: BorderKey,
    inset: Float,
    step: Float
): Pair<Offset, Offset> {
    return when (border.orientation) {
        BorderOrientation.HORIZONTAL -> {
            val startX = inset + border.col * step
            val startY = inset + border.row * step
            val endX = inset + (border.col + 1) * step
            val endY = startY
            Offset(startX, startY) to Offset(endX, endY)
        }
        BorderOrientation.VERTICAL -> {
            val startX = inset + border.col * step
            val startY = inset + border.row * step
            val endX = startX
            val endY = inset + (border.row + 1) * step
            Offset(startX, startY) to Offset(endX, endY)
        }
    }
}

/**
 * Computes shortest distance from tap point to the line segment of a border.
 */
private fun distanceToBorder(
    border: BorderKey,
    tap: Offset,
    inset: Float,
    step: Float
): Float {
    val (start, end) = getBorderCoordinates(border, inset, step)

    return when (border.orientation) {
        BorderOrientation.HORIZONTAL -> {
            // Horizontal segment along start.y from start.x to end.x
            val minX = start.x
            val maxX = end.x
            val clampedX = tap.x.coerceIn(minX, maxX)
            val dx = tap.x - clampedX
            val dy = tap.y - start.y
            kotlin.math.sqrt(dx * dx + dy * dy)
        }
        BorderOrientation.VERTICAL -> {
            // Vertical segment along start.x from start.y to end.y
            val minY = start.y
            val maxY = end.y
            val clampedY = tap.y.coerceIn(minY, maxY)
            val dx = tap.x - start.x
            val dy = tap.y - clampedY
            kotlin.math.sqrt(dx * dx + dy * dy)
        }
    }
}
