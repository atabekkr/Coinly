package com.atabekdev.coinly.screen.app

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.atabekdev.coinly.ui.theme.CoinlyTheme

@Composable
fun AppContent() {
    CoinlyTheme {
        Navigator(MainScreen()) { navigator ->
            FadeTransition(navigator)
        }
    }
}
