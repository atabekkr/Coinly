package com.atabekdev.coinly.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.atabekdev.coinly.core.common.util.formatBalance
import com.atabekdev.coinly.data.transaction.model.Transaction
import com.atabekdev.coinly.data.transaction.model.TransactionType
import com.atabekdev.coinly.ui.theme.IncomeColor
import com.atabekdev.coinly.ui.theme.categoryTint

// Row of a list card: category tile, name over the note (if any), signed amount.
@Composable
fun TransactionRow(
    transaction: Transaction,
    currency: String,
    onClick: () -> Unit,
) {
    val isIncome = transaction.type == TransactionType.INCOME
    // Zero gets no sign.
    val sign = if (transaction.amount == 0L) "" else if (isIncome) INCOME_SIGN else EXPENSE_SIGN
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .heightIn(min = 64.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EmojiTile(
            emoji = transaction.category.emoji,
            containerColor = categoryTint(transaction.category.sortOrder),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = transaction.category.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (transaction.note.isNotBlank()) {
                Text(
                    text = transaction.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Text(
            text = "$sign${formatBalance(transaction.amount)} $currency",
            style = MaterialTheme.typography.titleMedium,
            color = if (isIncome) IncomeColor else MaterialTheme.colorScheme.onSurface,
        )
    }
}

private const val INCOME_SIGN = "+"
private const val EXPENSE_SIGN = "−"
