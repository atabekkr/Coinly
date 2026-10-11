package com.atabekdev.coinly.screen.home

sealed interface HomeEvent {
    data object Idle : HomeEvent
}
