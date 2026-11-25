package com.atabekdev.coinly.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

// White 44 dp circle with an icon (close, back, delete).
@Composable
fun RoundIconButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(44.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
