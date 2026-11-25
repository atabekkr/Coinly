package com.atabekdev.coinly.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coinly.shared.generated.resources.Res
import coinly.shared.generated.resources.ic_chevron_left
import coinly.shared.generated.resources.ic_chevron_right
import com.atabekdev.coinly.core.common.util.formatMonth
import kotlinx.datetime.YearMonth
import org.jetbrains.compose.resources.painterResource

// White pill: previous month, month name, next month.
@Composable
fun MonthSwitcher(
    month: YearMonth,
    nextEnabled: Boolean,
    previousDescription: String,
    nextDescription: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPreviousClick, modifier = Modifier.size(44.dp)) {
                Icon(
                    painter = painterResource(Res.drawable.ic_chevron_left),
                    contentDescription = previousDescription,
                )
            }
            Text(
                text = formatMonth(month),
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
            )
            IconButton(onClick = onNextClick, enabled = nextEnabled, modifier = Modifier.size(44.dp)) {
                Icon(
                    painter = painterResource(Res.drawable.ic_chevron_right),
                    contentDescription = nextDescription,
                )
            }
        }
    }
}
