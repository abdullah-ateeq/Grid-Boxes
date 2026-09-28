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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gridboxes.ui.components.GridTopAppBar
import com.example.gridboxes.ui.theme.LocalGridBoxesPalette

@Composable
fun AboutScreen(
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
                title = "About",
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Emblem
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(45f)
                            .background(palette.playerOnePrimary)
                    )
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(palette.playerTwoPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "GRID BOXES",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    letterSpacing = 3.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Version 1.0",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.accentGold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Description Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(palette.surface.copy(alpha = 0.6f))
                        .border(1.dp, palette.surfaceBorder, RoundedCornerShape(18.dp))
                        .padding(20.dp)
                ) {
                    Text(
                        text = "A polished local two-player strategy game where players complete cell borders, claim boxes, and compete for the highest score.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = palette.textSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Feature Highlights
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeaturePill(title = "Local 2 Player", subtitle = "Pass and play on a single shared device")
                    FeaturePill(title = "5×5 Cell Grid", subtitle = "25 claimable cells with 60 border segments")
                    FeaturePill(title = "Border Completion", subtitle = "Intuitive geometric canvas touch interaction")
                    FeaturePill(title = "Extra-Turn Mechanic", subtitle = "Maintain momentum when closing boxes")
                    FeaturePill(title = "100% Offline Play", subtitle = "Zero accounts, trackers, or connections required")
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Original Geometric Visual Identity",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FeaturePill(
    title: String,
    subtitle: String
) {
    val palette = LocalGridBoxesPalette.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(palette.surface.copy(alpha = 0.4f))
            .border(1.dp, palette.surfaceBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary
            )
        }
    }
}
