package com.atabekdev.coinly.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coinly.shared.generated.resources.Res
import coinly.shared.generated.resources.ic_arrow_back
import com.atabekdev.coinly.ui.components.InDevelopment
import com.atabekdev.coinly.ui.components.RoundIconButton
import com.atabekdev.coinly.ui.localization.LocalStrings
import org.jetbrains.compose.resources.painterResource

// Placeholder: the feature is not built yet. It has no state, so there is no
// ScreenModel / State / Event.
class ProfileScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val strings = LocalStrings.current

        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    RoundIconButton(
                        icon = painterResource(Res.drawable.ic_arrow_back),
                        contentDescription = strings.common.back,
                        onClick = { navigator.pop() },
                    )
                    Text(
                        text = strings.profile.title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                    )
                }
            },
        ) { innerPadding ->
            InDevelopment(modifier = Modifier.padding(innerPadding))
        }
    }
}
