package com.atabekdev.coinly.screen.home

sealed interface HomeEvent {
    data object OnPreviousMonthClick : HomeEvent
    data object OnNextMonthClick : HomeEvent
    data object Idle : HomeEvent
}
