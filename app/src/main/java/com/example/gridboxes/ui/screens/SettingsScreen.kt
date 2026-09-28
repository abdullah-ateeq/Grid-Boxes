package com.example.gridboxes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gridboxes.data.GameSettings
import com.example.gridboxes.ui.components.GridConfirmDialog
import com.example.gridboxes.ui.components.GridSecondaryButton
import com.example.gridboxes.ui.components.GridTopAppBar
import com.example.gridboxes.ui.theme.LocalGridBoxesPalette

@Composable
fun SettingsScreen(
    settings: GameSettings,
    onThemeModeChange: (String) -> Unit,
    onBoardThemeChange: (String) -> Unit,
    onSoundChange: (Boolean) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onShowLastBorderChange: (Boolean) -> Unit,
    onShowBorderGuidesChange: (Boolean) -> Unit,
    onResetStats: () -> Unit,
    onBackClick: () -> Unit
) {
    val palette = LocalGridBoxesPalette.current
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        GridConfirmDialog(
            title = "Reset Statistics",
            message = "This will erase all recorded games, win counts, and high scores. This cannot be undone.",
            confirmText = "Reset",
            onConfirm = { onResetStats() },
            onDismiss = { showResetDialog = false }
        )
    }

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
                title = "Settings",
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section: Appearance
                SettingsSection(title = "APPEARANCE") {
                    // App Theme Segmented Control
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Theme Mode",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeOptionButton(
                                label = "System",
                                selected = settings.themeMode == "system",
                                onClick = { onThemeModeChange("system") },
                                modifier = Modifier.weight(1f)
                            )
                            ThemeOptionButton(
                                label = "Dark",
                                selected = settings.themeMode == "dark",
                                onClick = { onThemeModeChange("dark") },
                                modifier = Modifier.weight(1f)
                            )
                            ThemeOptionButton(
                                label = "Light",
                                selected = settings.themeMode == "light",
                                onClick = { onThemeModeChange("light") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(palette.surfaceBorder)
                    )

                    // Board Theme Segmented Control
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Board Visual Theme",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeOptionButton(
                                label = "Classic Bronze",
                                selected = settings.boardTheme == "classic",
                                onClick = { onBoardThemeChange("classic") },
                                modifier = Modifier.weight(1f)
                            )
                            ThemeOptionButton(
                                label = "Midnight Obsidian",
                                selected = settings.boardTheme == "midnight",
                                onClick = { onBoardThemeChange("midnight") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Section: Gameplay & Feedback
                SettingsSection(title = "GAMEPLAY & FEEDBACK") {
                    SettingsSwitchRow(
                        title = "Sound Effects",
                        description = "Procedural audio for borders, boxes, and victories",
                        checked = settings.soundEnabled,
                        onCheckedChange = onSoundChange
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(palette.surfaceBorder)
                    )

                    SettingsSwitchRow(
                        title = "Haptic Feedback",
                        description = "Tactile vibration when placing borders and completing cells",
                        checked = settings.hapticsEnabled,
                        onCheckedChange = onHapticsChange
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(palette.surfaceBorder)
                    )

                    SettingsSwitchRow(
                        title = "Highlight Last Border",
                        description = "Glow and pulse the most recently drawn border segment",
                        checked = settings.showLastBorder,
                        onCheckedChange = onShowLastBorderChange
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(palette.surfaceBorder)
                    )

                    SettingsSwitchRow(
                        title = "Show Border Guides",
                        description = "Display faint lines for unused grid segments",
                        checked = settings.showBorderGuides,
                        onCheckedChange = onShowBorderGuidesChange
                    )
                }

                // Section: Data & Reset
                SettingsSection(title = "DATA") {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Clear match statistics and records",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        GridSecondaryButton(
                            text = "Reset Statistics",
                            onClick = { showResetDialog = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    val palette = LocalGridBoxesPalette.current

    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = palette.textSecondary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(palette.surface.copy(alpha = 0.6f))
                .border(1.dp, palette.surfaceBorder, RoundedCornerShape(18.dp))
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val palette = LocalGridBoxesPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary,
                lineHeight = 16.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = palette.playerOnePrimary,
                uncheckedThumbColor = palette.textMuted,
                uncheckedTrackColor = palette.boardSurface
            )
        )
    }
}

@Composable
private fun ThemeOptionButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGridBoxesPalette.current

    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) palette.playerOnePrimary else palette.boardSurface)
            .border(
                1.dp,
                if (selected) palette.playerOnePrimary else palette.surfaceBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) Color.White else palette.textSecondary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            letterSpacing = 0.5.sp
        )
    }
}
