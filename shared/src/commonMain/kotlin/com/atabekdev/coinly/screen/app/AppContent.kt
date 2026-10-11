package com.atabekdev.coinly.screen.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.atabekdev.coinly.screen.home.HomeScreen

@Composable
fun AppContent() {
    MaterialTheme {
        Navigator(HomeScreen()) { navigator ->
            FadeTransition(navigator)
        }
    }
}
