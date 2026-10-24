package com.atabekdev.coinly.screen.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.atabekdev.coinly.screen.home.HomeScreen
import com.atabekdev.coinly.screen.transaction.list.TransactionListScreen

// Bottom tabs. Lives in the root navigator, so detail screens pushed there cover the tab bar.
class MainScreen : Screen {

    @Composable
    override fun Content() {
        TabNavigator(HomeScreen) {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        TabItem(HomeScreen)
                        TabItem(TransactionListScreen)
                    }
                },
            ) { innerPadding ->
                // Consumed insets keep the tabs' own Scaffolds from adding system bar padding twice.
                Box(modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)) {
                    CurrentTab()
                }
            }
        }
    }
}

@Composable
private fun RowScope.TabItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    val options = tab.options
    NavigationBarItem(
        selected = tabNavigator.current.key == tab.key,
        onClick = { tabNavigator.current = tab },
        icon = {
            options.icon?.let { Icon(painter = it, contentDescription = null) }
        },
        label = { Text(options.title) },
    )
}
