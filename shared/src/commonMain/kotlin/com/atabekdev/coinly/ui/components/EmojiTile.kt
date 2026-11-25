package com.atabekdev.coinly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Rounded square with a tint and a category emoji inside.
@Composable
fun EmojiTile(
    emoji: String,
    containerColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
) {
    Box(
        modifier = modifier.size(size).background(containerColor, MaterialTheme.shapes.small),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = emoji, style = MaterialTheme.typography.titleLarge)
    }
}
