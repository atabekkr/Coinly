package com.atabekdev.coinly.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coinly.shared.generated.resources.Res
import coinly.shared.generated.resources.ic_backspace
import org.jetbrains.compose.resources.painterResource

// Digits and backspace only: amounts are whole sums, so there is no decimal key.
@Composable
fun AmountKeypad(
    deleteDescription: String,
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        KEYPAD_ROWS.forEach { digits ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                digits.forEach { digit ->
                    KeypadKey(
                        onClick = { onDigitClick(digit) },
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(text = digit, style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Spacer(modifier = Modifier.weight(1f))
            KeypadKey(
                onClick = { onDigitClick(KEYPAD_ZERO) },
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.weight(1f),
            ) {
                Text(text = KEYPAD_ZERO, style = MaterialTheme.typography.headlineMedium)
            }
            KeypadKey(
                onClick = onDeleteClick,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_backspace),
                    contentDescription = deleteDescription,
                )
            }
        }
    }
}

@Composable
private fun KeypadKey(
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(14.dp),
        color = color,
    ) {
        Box(contentAlignment = Alignment.Center) { content() }
    }
}

private val KEYPAD_ROWS = listOf(
    listOf("1", "2", "3"),
    listOf("4", "5", "6"),
    listOf("7", "8", "9"),
)
private const val KEYPAD_ZERO = "0"
