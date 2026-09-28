package com.example.gridboxes.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gridboxes.data.GamePreferences
import com.example.gridboxes.data.GameSettings
import com.example.gridboxes.data.GameStats
import com.example.gridboxes.logic.GameLogic
import com.example.gridboxes.model.BorderKey
import com.example.gridboxes.model.GridBoxesState
import com.example.gridboxes.util.HapticHelper
import com.example.gridboxes.util.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = GamePreferences(application)

    val settings: StateFlow<GameSettings> = preferences.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GameSettings()
    )

    val stats: StateFlow<GameStats> = preferences.statsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GameStats()
    )

    private val soundManager = SoundManager { settings.value.soundEnabled }
    private val hapticHelper = HapticHelper(application) { settings.value.hapticsEnabled }

    private val _uiState = MutableStateFlow(GridBoxesState())
    val uiState: StateFlow<GridBoxesState> = _uiState.asStateFlow()

    private val undoStack = ArrayDeque<GridBoxesState>()
    private var bannerJob: Job? = null

    val canUndo: Boolean
        get() = undoStack.isNotEmpty() && !_uiState.value.isGameOver && !_uiState.value.isAnimating

    fun onBorderTapped(border: BorderKey) {
        val currentState = _uiState.value
        if (currentState.isGameOver || currentState.isAnimating) return
        if (border in currentState.drawnBorders) return
        if (!GameLogic.isValidBorder(border)) return

        // Save undo snapshot
        undoStack.addLast(currentState)

        // Lock briefly during placement animation
        _uiState.value = currentState.copy(isAnimating = true)

        viewModelScope.launch {
            // Short delay for tactile feel and border stroke draw animation
            delay(120)

            val moveResult = GameLogic.applyMove(currentState, border)
            if (moveResult is GameLogic.MoveExecutionResult.Success) {
                _uiState.value = moveResult.newState

                // Sound & Haptics
                when {
                    moveResult.isGameOver -> {
                        soundManager.playSound(SoundManager.SoundType.GAME_END)
                        hapticHelper.performHaptic(HapticHelper.HapticType.WIN)
                        // Record stats
                        preferences.recordGameResult(
                            player1Score = moveResult.newState.player1Score,
                            player2Score = moveResult.newState.player2Score,
                            winner = moveResult.newState.winner,
                            isDraw = moveResult.newState.isDraw
                        )
                    }
                    moveResult.completedCells.size == 2 -> {
                        soundManager.playSound(SoundManager.SoundType.DOUBLE_BOX)
                        hapticHelper.performHaptic(HapticHelper.HapticType.DOUBLE_COMPLETION)
                    }
                    moveResult.completedCells.size == 1 -> {
                        soundManager.playSound(SoundManager.SoundType.CELL_COMPLETED)
                        hapticHelper.performHaptic(HapticHelper.HapticType.CELL_COMPLETION)
                    }
                    else -> {
                        soundManager.playSound(SoundManager.SoundType.BORDER_DRAW)
                        hapticHelper.performHaptic(HapticHelper.HapticType.BORDER_TAP)
                    }
                }

                // Banner auto-clear if showing extra turn/double box
                if (moveResult.newState.bannerMessage != null) {
                    bannerJob?.cancel()
                    bannerJob = viewModelScope.launch {
                        delay(1400)
                        _uiState.value = _uiState.value.copy(bannerMessage = null)
                    }
                }
            } else {
                // If rejected, restore state and remove snapshot
                if (undoStack.isNotEmpty()) undoStack.removeLast()
                _uiState.value = currentState.copy(isAnimating = false)
            }
        }
    }

    fun undo() {
        if (!canUndo) return
        bannerJob?.cancel()
        val previousState = undoStack.removeLast()
        _uiState.value = previousState.copy(isAnimating = false, bannerMessage = null)
        hapticHelper.performHaptic(HapticHelper.HapticType.BORDER_TAP)
    }

    fun restartMatch() {
        bannerJob?.cancel()
        undoStack.clear()
        _uiState.value = GridBoxesState(matchStartTimeMs = System.currentTimeMillis())
        hapticHelper.performHaptic(HapticHelper.HapticType.BORDER_TAP)
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch { preferences.setThemeMode(mode) }
    }

    fun setBoardTheme(theme: String) {
        viewModelScope.launch { preferences.setBoardTheme(theme) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { preferences.setSoundEnabled(enabled) }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch { preferences.setHapticsEnabled(enabled) }
    }

    fun setShowLastBorder(enabled: Boolean) {
        viewModelScope.launch { preferences.setShowLastBorder(enabled) }
    }

    fun setShowBorderGuides(enabled: Boolean) {
        viewModelScope.launch { preferences.setShowBorderGuides(enabled) }
    }

    fun resetStatistics() {
        viewModelScope.launch { preferences.resetStatistics() }
    }
}
