package com.example.gridboxes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.gridboxes.navigation.GridBoxesNavHost
import com.example.gridboxes.ui.theme.GridBoxesTheme
import com.example.gridboxes.ui.theme.LocalGridBoxesPalette
import com.example.gridboxes.ui.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsState()

            GridBoxesTheme(
                themeMode = settings.themeMode,
                boardTheme = settings.boardTheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = LocalGridBoxesPalette.current.background
                ) {
                    GridBoxesNavHost(viewModel = viewModel)
                }
            }
        }
    }
}