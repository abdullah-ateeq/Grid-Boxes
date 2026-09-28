package com.example.gridboxes.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticHelper(
    private val context: Context,
    private val isHapticsEnabled: () -> Boolean
) {
    enum class HapticType {
        BORDER_TAP,
        CELL_COMPLETION,
        DOUBLE_COMPLETION,
        WIN
    }

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }

    fun performHaptic(type: HapticType) {
        if (!isHapticsEnabled()) return
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                when (type) {
                    HapticType.BORDER_TAP -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
                        } else {
                            vib.vibrate(VibrationEffect.createOneShot(15, 60))
                        }
                    }
                    HapticType.CELL_COMPLETION -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                        } else {
                            vib.vibrate(VibrationEffect.createOneShot(35, 140))
                        }
                    }
                    HapticType.DOUBLE_COMPLETION -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
                        } else {
                            vib.vibrate(VibrationEffect.createOneShot(65, 220))
                        }
                    }
                    HapticType.WIN -> {
                        val timings = longArrayOf(0, 60, 70, 80, 80, 160)
                        val amplitudes = intArrayOf(0, 100, 0, 180, 0, 255)
                        vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                when (type) {
                    HapticType.BORDER_TAP -> vib.vibrate(15)
                    HapticType.CELL_COMPLETION -> vib.vibrate(35)
                    HapticType.DOUBLE_COMPLETION -> vib.vibrate(65)
                    HapticType.WIN -> vib.vibrate(longArrayOf(0, 60, 70, 80, 80, 160), -1)
                }
            }
        } catch (_: Exception) {
            // Gracefully ignore vibration errors
        }
    }
}
