package com.svape.masterunalapp.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.svape.masterunalapp.ui.theme.MasterUnalAppTheme
import com.svape.masterunalapp.ui.viewmodel.OnlineGameViewModel
import com.svape.masterunalapp.ui.utils.SoundManager

class OnlineGameActivity : ComponentActivity() {

    private val viewModel: OnlineGameViewModel by viewModels()
    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        soundManager = SoundManager(this, this)
        viewModel.setSoundManager(soundManager)

        setContent {
            MasterUnalAppTheme {
                OnlineGameNavigation(
                    viewModel = viewModel,
                    onExit = { finish() }
                )
            }
        }
    }
}

@Composable
fun OnlineGameNavigation(
    viewModel: OnlineGameViewModel,
    onExit: () -> Unit
) {
    val currentGameId by viewModel.currentGameId.collectAsState()

    if (currentGameId == null) {
        OnlineGameListScreen(
            viewModel = viewModel,
            onNavigateBack = onExit,
            onGameSelected = { }
        )
    } else {
        OnlineGameScreen(
            viewModel = viewModel,
            onNavigateBack = {
                viewModel.leaveGame()
            }
        )
    }
}