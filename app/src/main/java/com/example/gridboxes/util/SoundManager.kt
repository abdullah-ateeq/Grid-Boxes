package com.example.gridboxes.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundManager(private val isSoundEnabled: () -> Boolean) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val sampleRate = 44100

    enum class SoundType {
        BORDER_DRAW,
        CELL_COMPLETED,
        DOUBLE_BOX,
        EXTRA_TURN,
        GAME_END
    }

    fun playSound(type: SoundType) {
        if (!isSoundEnabled()) return

        scope.launch {
            try {
                val pcm = when (type) {
                    SoundType.BORDER_DRAW -> generateTone(
                        frequencies = doubleArrayOf(740.0),
                        durationMs = 40,
                        decayRate = 30.0
                    )
                    SoundType.CELL_COMPLETED -> generateArpeggio(
                        notes = listOf(523.25 to 60, 659.25 to 100),
                        decayRate = 12.0
                    )
                    SoundType.DOUBLE_BOX -> generateHarmonicChord(
                        frequencies = doubleArrayOf(523.25, 659.25, 783.99),
                        durationMs = 220,
                        decayRate = 10.0
                    )
                    SoundType.EXTRA_TURN -> generateTone(
                        frequencies = doubleArrayOf(880.0, 1108.73),
                        durationMs = 90,
                        decayRate = 18.0
                    )
                    SoundType.GAME_END -> generateArpeggio(
                        notes = listOf(440.0 to 70, 554.37 to 70, 659.25 to 90, 880.0 to 180),
                        decayRate = 8.0
                    )
                }

                playPcm(pcm)
            } catch (_: Exception) {
                // Ignore audio play errors on unsupported devices
            }
        }
    }

    private fun generateTone(
        frequencies: DoubleArray,
        durationMs: Int,
        decayRate: Double
    ): ShortArray {
        val totalSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-decayRate * t)
            var sample = 0.0
            for (freq in frequencies) {
                sample += sin(2.0 * PI * freq * t)
            }
            sample /= frequencies.size
            buffer[i] = (sample * envelope * Short.MAX_VALUE * 0.45).toInt().toShort()
        }
        return buffer
    }

    private fun generateHarmonicChord(
        frequencies: DoubleArray,
        durationMs: Int,
        decayRate: Double
    ): ShortArray {
        val totalSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-decayRate * t)
            var sample = 0.0
            for ((idx, freq) in frequencies.withIndex()) {
                val weight = 1.0 / (idx + 1)
                sample += weight * sin(2.0 * PI * freq * t)
            }
            buffer[i] = (sample * envelope * Short.MAX_VALUE * 0.35).toInt().toShort()
        }
        return buffer
    }

    private fun generateArpeggio(
        notes: List<Pair<Double, Int>>, // frequency to durationMs
        decayRate: Double
    ): ShortArray {
        val totalDurationMs = notes.sumOf { it.second }
        val totalSamples = (sampleRate * totalDurationMs) / 1000
        val buffer = ShortArray(totalSamples)

        var sampleOffset = 0
        for ((freq, durMs) in notes) {
            val noteSamples = (sampleRate * durMs) / 1000
            for (i in 0 until noteSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = exp(-decayRate * t)
                val sample = sin(2.0 * PI * freq * t)
                val targetIndex = sampleOffset + i
                if (targetIndex < totalSamples) {
                    buffer[targetIndex] = (sample * envelope * Short.MAX_VALUE * 0.4).toInt().toShort()
                }
            }
            sampleOffset += noteSamples
        }
        return buffer
    }

    private fun playPcm(pcm: ShortArray) {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(pcm.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(pcm, 0, pcm.size)
        audioTrack.play()

        // Auto release after playback
        audioTrack.setNotificationMarkerPosition(pcm.size)
        audioTrack.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
            override fun onMarkerReached(track: AudioTrack) {
                track.release()
            }
            override fun onPeriodicNotification(track: AudioTrack) {}
        })
    }
}
