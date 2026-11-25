package com.atabekdev.coinly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coinly.shared.generated.resources.Res
import coinly.shared.generated.resources.ic_build
import com.atabekdev.coinly.ui.localization.LocalStrings
import org.jetbrains.compose.resources.painterResource

// Body of a screen whose feature is not built yet: an icon with an "in development" text.
@Composable
fun InDevelopment(modifier: Modifier = Modifier) {
    val strings = LocalStrings.current.common
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .size(72.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.extraLarge),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_build),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp),
            )
        }
        Text(
            text = strings.inDevelopment,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = strings.inDevelopmentHint,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
