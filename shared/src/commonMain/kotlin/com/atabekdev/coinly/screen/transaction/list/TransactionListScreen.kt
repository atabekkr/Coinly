package com.atabekdev.coinly.screen.transaction.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import coinly.shared.generated.resources.Res
import coinly.shared.generated.resources.ic_add
import coinly.shared.generated.resources.ic_chevron_left
import coinly.shared.generated.resources.ic_chevron_right
import coinly.shared.generated.resources.ic_receipt_long
import com.atabekdev.coinly.core.common.util.formatBalance
import com.atabekdev.coinly.core.common.util.formatDayMonth
import com.atabekdev.coinly.core.common.util.formatMonth
import com.atabekdev.coinly.data.transaction.model.Transaction
import com.atabekdev.coinly.data.transaction.model.TransactionType
import com.atabekdev.coinly.screen.transaction.edit.TransactionEditScreen
import com.atabekdev.coinly.ui.localization.LocalStrings
import com.atabekdev.coinly.ui.localization.TransactionStrings
import com.atabekdev.coinly.ui.theme.IncomeColor
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock

object TransactionListScreen : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = LocalStrings.current.transaction.listTitle
            val icon = painterResource(Res.drawable.ic_receipt_long)
            return remember(title, icon) { TabOptions(index = 1u, title = title, icon = icon) }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        // Tabs live in a nested navigator; the edit screen opens over the tabs in the root one.
        val rootNavigator = checkNotNull(LocalNavigator.currentOrThrow.parent)
        val strings = LocalStrings.current
        val model = koinScreenModel<TransactionListScreenModel>()
        val state by model.state.collectAsState()
        val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

        Scaffold(
            topBar = { TopAppBar(title = { Text(strings.transaction.listTitle) }) },
            floatingActionButton = {
                FloatingActionButton(onClick = { rootNavigator.push(TransactionEditScreen()) }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_add),
                        contentDescription = strings.transaction.add,
                    )
                }
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                // Keeps the last row above the floating action button.
                contentPadding = PaddingValues(bottom = 88.dp),
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = { model.onEvent(TransactionListEvent.OnPreviousMonthClick) }) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_chevron_left),
                                contentDescription = strings.transaction.previousMonth,
                            )
                        }
                        Text(
                            text = formatMonth(state.month),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(
                            onClick = { model.onEvent(TransactionListEvent.OnNextMonthClick) },
                            enabled = state.month < state.currentMonth,
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_chevron_right),
                                contentDescription = strings.transaction.nextMonth,
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        TotalCard(
                            title = strings.transaction.totalIncome,
                            amount = signedAmount(INCOME_SIGN, state.totals.income, strings.common.currency),
                            color = IncomeColor,
                            modifier = Modifier.weight(1f),
                        )
                        TotalCard(
                            title = strings.transaction.totalExpense,
                            amount = signedAmount(EXPENSE_SIGN, state.totals.expense, strings.common.currency),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                if (state.days.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = strings.transaction.emptyMonth,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                state.days.forEach { day ->
                    item(key = day.date.toString()) {
                        Text(
                            text = dayTitle(day.date, today, strings.transaction),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
                        )
                    }
                    items(day.transactions, key = { it.id }) { transaction ->
                        TransactionRow(
                            transaction = transaction,
                            currency = strings.common.currency,
                            onClick = { rootNavigator.push(TransactionEditScreen(transaction.id)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalCard(
    title: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium,
                color = color,
            )
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: Transaction,
    currency: String,
    onClick: () -> Unit,
) {
    val isIncome = transaction.type == TransactionType.INCOME
    ListItem(
        leadingContent = {
            Text(
                text = transaction.category.emoji,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        headlineContent = { Text(transaction.category.name) },
        supportingContent = if (transaction.note.isNotBlank()) {
            { Text(text = transaction.note, maxLines = 1) }
        } else {
            null
        },
        trailingContent = {
            Text(
                text = signedAmount(if (isIncome) INCOME_SIGN else EXPENSE_SIGN, transaction.amount, currency),
                style = MaterialTheme.typography.bodyLarge,
                color = if (isIncome) IncomeColor else MaterialTheme.colorScheme.onSurface,
            )
        },
        modifier = Modifier.clickable(onClick = onClick),
    )
}

private const val INCOME_SIGN = "+"
private const val EXPENSE_SIGN = "−"

// Zero gets no sign: "0 сум", not "+0 сум".
private fun signedAmount(sign: String, tiyin: Long, currency: String): String =
    "${if (tiyin > 0) sign else ""}${formatBalance(tiyin)} $currency"

private fun dayTitle(date: LocalDate, today: LocalDate, strings: TransactionStrings): String =
    when (date) {
        today -> strings.today
        today.minus(1, DateTimeUnit.DAY) -> strings.yesterday
        else -> formatDayMonth(date)
    }
