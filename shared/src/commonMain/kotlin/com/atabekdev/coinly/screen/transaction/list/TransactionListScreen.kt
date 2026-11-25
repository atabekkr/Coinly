package com.atabekdev.coinly.screen.transaction.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coinly.shared.generated.resources.Res
import coinly.shared.generated.resources.ic_add
import coinly.shared.generated.resources.ic_arrow_back
import coinly.shared.generated.resources.ic_north_east
import coinly.shared.generated.resources.ic_south_west
import com.atabekdev.coinly.core.common.util.formatBalance
import com.atabekdev.coinly.core.common.util.formatDayMonth
import com.atabekdev.coinly.screen.transaction.edit.TransactionEditScreen
import com.atabekdev.coinly.ui.components.ListCard
import com.atabekdev.coinly.ui.components.ListCardDivider
import com.atabekdev.coinly.ui.components.MonthSwitcher
import com.atabekdev.coinly.ui.components.RoundIconButton
import com.atabekdev.coinly.ui.components.SummaryCard
import com.atabekdev.coinly.ui.components.TransactionRow
import com.atabekdev.coinly.ui.localization.LocalStrings
import com.atabekdev.coinly.ui.localization.TransactionStrings
import com.atabekdev.coinly.ui.theme.ExpenseAccentColor
import com.atabekdev.coinly.ui.theme.ExpenseTintColor
import com.atabekdev.coinly.ui.theme.IncomeColor
import com.atabekdev.coinly.ui.theme.IncomeTintColor
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock

// The full list of a month's transactions; opened over the tabs from Home's "see all".
class TransactionListScreen : Screen {

    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow
        val strings = LocalStrings.current
        val model = koinScreenModel<TransactionListScreenModel>()
        val state by model.state.collectAsState()
        val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

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
                        onClick = { rootNavigator.pop() },
                    )
                    Text(
                        text = strings.transaction.listTitle,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    MonthSwitcher(
                        month = state.month,
                        nextEnabled = state.month < state.currentMonth,
                        previousDescription = strings.transaction.previousMonth,
                        nextDescription = strings.transaction.nextMonth,
                        onPreviousClick = { model.onEvent(TransactionListEvent.OnPreviousMonthClick) },
                        onNextClick = { model.onEvent(TransactionListEvent.OnNextMonthClick) },
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { rootNavigator.push(TransactionEditScreen()) },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_add),
                        contentDescription = strings.transaction.add,
                    )
                }
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                // Bottom padding keeps the last row above the floating action button.
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 88.dp),
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        SummaryCard(
                            title = strings.transaction.totalIncome,
                            amount = signedAmount(INCOME_SIGN, state.totals.income, strings.common.currency),
                            amountColor = IncomeColor,
                            icon = painterResource(Res.drawable.ic_south_west),
                            iconColor = IncomeColor,
                            iconTint = IncomeTintColor,
                            modifier = Modifier.weight(1f),
                        )
                        SummaryCard(
                            title = strings.transaction.totalExpense,
                            amount = signedAmount(EXPENSE_SIGN, state.totals.expense, strings.common.currency),
                            amountColor = MaterialTheme.colorScheme.onSurface,
                            icon = painterResource(Res.drawable.ic_north_east),
                            iconColor = ExpenseAccentColor,
                            iconTint = ExpenseTintColor,
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
                        Column {
                            Text(
                                text = dayTitle(day.date, today, strings.transaction),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                            )
                            ListCard {
                                day.transactions.forEachIndexed { index, transaction ->
                                    if (index > 0) ListCardDivider()
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
        }
    }
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
