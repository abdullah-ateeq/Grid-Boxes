package com.example.gridboxes.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.gridboxes.model.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.abs
import kotlin.math.max

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "grid_boxes_prefs")

data class GameSettings(
    val themeMode: String = "system", // "system", "dark", "light"
    val boardTheme: String = "classic", // "classic", "midnight"
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val showLastBorder: Boolean = true,
    val showBorderGuides: Boolean = true
)

data class GameStats(
    val gamesPlayed: Int = 0,
    val player1Wins: Int = 0,
    val player2Wins: Int = 0,
    val draws: Int = 0,
    val highestScore: Int = 0,
    val largestWinningMargin: Int = 0
)

class GamePreferences(private val context: Context) {

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val BOARD_THEME_KEY = stringPreferencesKey("board_theme")
        private val SOUND_KEY = booleanPreferencesKey("sound_enabled")
        private val HAPTICS_KEY = booleanPreferencesKey("haptics_enabled")
        private val SHOW_LAST_BORDER_KEY = booleanPreferencesKey("show_last_border")
        private val SHOW_BORDER_GUIDES_KEY = booleanPreferencesKey("show_border_guides")

        private val GAMES_PLAYED_KEY = intPreferencesKey("games_played")
        private val P1_WINS_KEY = intPreferencesKey("p1_wins")
        private val P2_WINS_KEY = intPreferencesKey("p2_wins")
        private val DRAWS_KEY = intPreferencesKey("draws")
        private val HIGHEST_SCORE_KEY = intPreferencesKey("highest_score")
        private val LARGEST_MARGIN_KEY = intPreferencesKey("largest_winning_margin")
    }

    val settingsFlow: Flow<GameSettings> = context.dataStore.data.map { prefs ->
        GameSettings(
            themeMode = prefs[THEME_MODE_KEY] ?: "system",
            boardTheme = prefs[BOARD_THEME_KEY] ?: "classic",
            soundEnabled = prefs[SOUND_KEY] ?: true,
            hapticsEnabled = prefs[HAPTICS_KEY] ?: true,
            showLastBorder = prefs[SHOW_LAST_BORDER_KEY] ?: true,
            showBorderGuides = prefs[SHOW_BORDER_GUIDES_KEY] ?: true
        )
    }

    val statsFlow: Flow<GameStats> = context.dataStore.data.map { prefs ->
        GameStats(
            gamesPlayed = prefs[GAMES_PLAYED_KEY] ?: 0,
            player1Wins = prefs[P1_WINS_KEY] ?: 0,
            player2Wins = prefs[P2_WINS_KEY] ?: 0,
            draws = prefs[DRAWS_KEY] ?: 0,
            highestScore = prefs[HIGHEST_SCORE_KEY] ?: 0,
            largestWinningMargin = prefs[LARGEST_MARGIN_KEY] ?: 0
        )
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = mode
        }
    }

    suspend fun setBoardTheme(theme: String) {
        context.dataStore.edit { prefs ->
            prefs[BOARD_THEME_KEY] = theme
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SOUND_KEY] = enabled
        }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[HAPTICS_KEY] = enabled
        }
    }

    suspend fun setShowLastBorder(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SHOW_LAST_BORDER_KEY] = enabled
        }
    }

    suspend fun setShowBorderGuides(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SHOW_BORDER_GUIDES_KEY] = enabled
        }
    }

    suspend fun recordGameResult(
        player1Score: Int,
        player2Score: Int,
        winner: Player?,
        isDraw: Boolean
    ) {
        context.dataStore.edit { prefs ->
            val games = (prefs[GAMES_PLAYED_KEY] ?: 0) + 1
            prefs[GAMES_PLAYED_KEY] = games

            if (isDraw) {
                prefs[DRAWS_KEY] = (prefs[DRAWS_KEY] ?: 0) + 1
            } else if (winner == Player.PLAYER_ONE) {
                prefs[P1_WINS_KEY] = (prefs[P1_WINS_KEY] ?: 0) + 1
            } else if (winner == Player.PLAYER_TWO) {
                prefs[P2_WINS_KEY] = (prefs[P2_WINS_KEY] ?: 0) + 1
            }

            val gameMaxScore = max(player1Score, player2Score)
            val currentHighest = prefs[HIGHEST_SCORE_KEY] ?: 0
            if (gameMaxScore > currentHighest) {
                prefs[HIGHEST_SCORE_KEY] = gameMaxScore
            }

            val margin = abs(player1Score - player2Score)
            val currentMargin = prefs[LARGEST_MARGIN_KEY] ?: 0
            if (margin > currentMargin) {
                prefs[LARGEST_MARGIN_KEY] = margin
            }
        }
    }

    suspend fun resetStatistics() {
        context.dataStore.edit { prefs ->
            prefs[GAMES_PLAYED_KEY] = 0
            prefs[P1_WINS_KEY] = 0
            prefs[P2_WINS_KEY] = 0
            prefs[DRAWS_KEY] = 0
            prefs[HIGHEST_SCORE_KEY] = 0
            prefs[LARGEST_MARGIN_KEY] = 0
        }
    }
}
