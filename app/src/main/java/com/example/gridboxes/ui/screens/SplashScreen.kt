package com.example.gridboxes.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gridboxes.ui.theme.LocalGridBoxesPalette
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val palette = LocalGridBoxesPalette.current

    // Animation progress states
    val gridFadeAnim = remember { Animatable(0f) }
    val border1Anim = remember { Animatable(0f) }
    val border2Anim = remember { Animatable(0f) }
    val border3Anim = remember { Animatable(0f) }
    val border4Anim = remember { Animatable(0f) }
    val cellFillAnim = remember { Animatable(0f) }
    val titleFadeAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Step 1: Grid anchors fade in (0 - 300ms)
        gridFadeAnim.animateTo(1f, animationSpec = tween(300, easing = LinearEasing))

        // Step 2: Borders draw sequentially (300 - 900ms)
        border1Anim.animateTo(1f, animationSpec = tween(150, easing = FastOutSlowInEasing))
        border2Anim.animateTo(1f, animationSpec = tween(150, easing = FastOutSlowInEasing))
        border3Anim.animateTo(1f, animationSpec = tween(150, easing = FastOutSlowInEasing))
        border4Anim.animateTo(1f, animationSpec = tween(150, easing = FastOutSlowInEasing))

        // Step 3: Cell completes and fills with color & symbol (900 - 1150ms)
        cellFillAnim.animateTo(1f, animationSpec = tween(250, easing = FastOutSlowInEasing))

        // Step 4: Title fades in (1150 - 1450ms)
        titleFadeAnim.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))

        delay(150)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Mini-Grid Emblem
            Canvas(
                modifier = Modifier.size(140.dp)
            ) {
                val sizePx = size.minDimension
                val step = sizePx / 3f
                val strokeW = step * 0.12f
                val dotRadius = step * 0.11f

                // Draw cell fill when 4th border closes
                if (cellFillAnim.value > 0f) {
                    val fillAlpha = cellFillAnim.value * 0.75f
                    drawRoundRect(
                        color = palette.playerOneFill.copy(alpha = fillAlpha),
                        topLeft = Offset(step + step * 0.1f, step + step * 0.1f),
                        size = Size(step * 0.8f, step * 0.8f),
                        cornerRadius = CornerRadius(step * 0.15f, step * 0.15f)
                    )

                    // Diamond symbol in center
                    val centerX = step * 1.5f
                    val centerY = step * 1.5f
                    val diamondSize = (step * 0.22f) * cellFillAnim.value
                    val diamondPath = Path().apply {
                        moveTo(centerX, centerY - diamondSize)
                        lineTo(centerX + diamondSize, centerY)
                        lineTo(centerX, centerY + diamondSize)
                        lineTo(centerX - diamondSize, centerY)
                        close()
                    }
                    drawPath(
                        path = diamondPath,
                        color = palette.playerOnePrimary.copy(alpha = cellFillAnim.value),
                        style = Fill
                    )
                }

                // Border 1: Top (progress from left to right)
                if (border1Anim.value > 0f) {
                    val start = Offset(step, step)
                    val end = Offset(step + step * border1Anim.value, step)
                    drawLine(
                        color = palette.borderUsed,
                        start = start,
                        end = end,
                        strokeWidth = strokeW,
                        cap = StrokeCap.Round
                    )
                }

                // Border 2: Right (progress from top to bottom)
                if (border2Anim.value > 0f) {
                    val start = Offset(step * 2f, step)
                    val end = Offset(step * 2f, step + step * border2Anim.value)
                    drawLine(
                        color = palette.borderUsed,
                        start = start,
                        end = end,
                        strokeWidth = strokeW,
                        cap = StrokeCap.Round
                    )
                }

                // Border 3: Bottom (progress from right to left)
                if (border3Anim.value > 0f) {
                    val start = Offset(step * 2f, step * 2f)
                    val end = Offset(step * 2f - step * border3Anim.value, step * 2f)
                    drawLine(
                        color = palette.borderUsed,
                        start = start,
                        end = end,
                        strokeWidth = strokeW,
                        cap = StrokeCap.Round
                    )
                }

                // Border 4: Left (closing border, progress from bottom to top)
                if (border4Anim.value > 0f) {
                    val start = Offset(step, step * 2f)
                    val end = Offset(step, step * 2f - step * border4Anim.value)
                    drawLine(
                        color = palette.playerOnePrimary,
                        start = start,
                        end = end,
                        strokeWidth = strokeW,
                        cap = StrokeCap.Round
                    )
                }

                // Grid anchor dots (4 corners of center cell + adjacent corners)
                if (gridFadeAnim.value > 0f) {
                    val anchorColor = palette.gridAnchor.copy(alpha = gridFadeAnim.value)
                    for (r in 1..2) {
                        for (c in 1..2) {
                            val cx = c * step
                            val cy = r * step
                            drawCircle(
                                color = anchorColor,
                                radius = dotRadius,
                                center = Offset(cx, cy)
                            )
                            drawCircle(
                                color = palette.background,
                                radius = dotRadius * 0.45f,
                                center = Offset(cx, cy)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Title "GRID BOXES"
            Text(
                text = "GRID BOXES",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary.copy(alpha = titleFadeAnim.value),
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "LOCAL STRATEGY",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = palette.playerOnePrimary.copy(alpha = titleFadeAnim.value),
                letterSpacing = 3.sp
            )
        }
    }
}
