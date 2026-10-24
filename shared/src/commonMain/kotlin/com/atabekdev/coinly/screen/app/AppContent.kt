package com.atabekdev.coinly.screen.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition

@Composable
fun AppContent() {
    MaterialTheme {
        Navigator(MainScreen()) { navigator ->
            FadeTransition(navigator)
        }
    }
}
