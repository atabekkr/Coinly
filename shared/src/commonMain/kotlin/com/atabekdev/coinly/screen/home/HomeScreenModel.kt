package com.atabekdev.coinly.screen.home

import cafe.adriel.voyager.core.model.StateScreenModel
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent

class HomeScreenModel : StateScreenModel<HomeState>(HomeState()), KoinComponent {

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Idle -> setState()
        }
    }

    private fun setState(loading: Boolean = false) {
        mutableState.update { it.copy(isLoading = loading) }
    }
}
