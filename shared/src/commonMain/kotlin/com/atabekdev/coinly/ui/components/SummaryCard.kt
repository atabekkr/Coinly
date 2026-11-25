package com.atabekdev.coinly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Month income / expense card: a small tinted icon tile with a label, the amount below.
@Composable
fun SummaryCard(
    title: String,
    amount: String,
    amountColor: Color,
    icon: Painter,
    iconColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    shadowElevation: Dp = 0.dp,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = shadowElevation,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier.size(28.dp).background(iconTint, MaterialTheme.shapes.extraSmall),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium,
                color = amountColor,
            )
        }
    }
}
