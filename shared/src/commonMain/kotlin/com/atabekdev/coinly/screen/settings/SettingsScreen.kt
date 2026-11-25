package com.atabekdev.coinly.screen.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import coinly.shared.generated.resources.Res
import coinly.shared.generated.resources.ic_tune
import com.atabekdev.coinly.ui.components.InDevelopment
import com.atabekdev.coinly.ui.components.ScreenHeader
import com.atabekdev.coinly.ui.localization.LocalStrings
import org.jetbrains.compose.resources.painterResource

// Placeholder tab: the feature is not built yet, the screen says so.
// It has no state, so there is no ScreenModel / State / Event.
object SettingsScreen : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = LocalStrings.current.settings.title
            val icon = painterResource(Res.drawable.ic_tune)
            return remember(title, icon) { TabOptions(index = 3u, title = title, icon = icon) }
        }

    @Composable
    override fun Content() {
        Scaffold(
            topBar = {
                ScreenHeader(
                    title = LocalStrings.current.settings.title,
                    modifier = Modifier.statusBarsPadding(),
                )
            },
        ) { innerPadding ->
            Text(
                text = LocalStrings.current.settings.version,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp, vertical = 8.dp),
            )
        }
    }
}
