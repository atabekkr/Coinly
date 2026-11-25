package com.atabekdev.coinly.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import coinly.shared.generated.resources.Res
import coinly.shared.generated.resources.ic_home
import coinly.shared.generated.resources.ic_north_east
import coinly.shared.generated.resources.ic_notifications
import coinly.shared.generated.resources.ic_south_west
import com.atabekdev.coinly.core.common.util.formatBalance
import com.atabekdev.coinly.core.common.util.formatDayMonth
import com.atabekdev.coinly.screen.notification.NotificationScreen
import com.atabekdev.coinly.screen.profile.ProfileScreen
import com.atabekdev.coinly.screen.transaction.edit.TransactionEditScreen
import com.atabekdev.coinly.screen.transaction.list.TransactionListScreen
import com.atabekdev.coinly.ui.components.ListCard
import com.atabekdev.coinly.ui.components.ListCardDivider
import com.atabekdev.coinly.ui.components.MonthSwitcher
import com.atabekdev.coinly.ui.components.SummaryCard
import com.atabekdev.coinly.ui.components.TransactionRow
import com.atabekdev.coinly.ui.localization.HomeStrings
import com.atabekdev.coinly.ui.localization.LocalStrings
import com.atabekdev.coinly.ui.theme.ExpenseAccentColor
import com.atabekdev.coinly.ui.theme.ExpenseTintColor
import com.atabekdev.coinly.ui.theme.HeroOverlayColor
import com.atabekdev.coinly.ui.theme.HeroSecondaryColor
import com.atabekdev.coinly.ui.theme.HeroTertiaryColor
import com.atabekdev.coinly.ui.theme.IncomeColor
import com.atabekdev.coinly.ui.theme.IncomeTintColor
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs
import kotlin.time.Clock

object HomeScreen : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = LocalStrings.current.home.title
            val icon = painterResource(Res.drawable.ic_home)
            return remember(title, icon) { TabOptions(index = 0u, title = title, icon = icon) }
        }

    @Composable
    override fun Content() {
        // Tabs live in a nested navigator; detail screens open over the tabs in the root one.
        val rootNavigator = checkNotNull(LocalNavigator.currentOrThrow.parent)
        val strings = LocalStrings.current
        val model = koinScreenModel<HomeScreenModel>()
        val state by model.state.collectAsState()
        val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

        LazyColumn(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                Hero(
                    state = state,
                    strings = strings.home,
                    currency = strings.common.currency,
                    onPreviousMonthClick = { model.onEvent(HomeEvent.OnPreviousMonthClick) },
                    onNextMonthClick = { model.onEvent(HomeEvent.OnNextMonthClick) },
                    onProfileClick = { rootNavigator.push(ProfileScreen()) },
                    onNotificationsClick = { rootNavigator.push(NotificationScreen()) },
                )
            }

            item {
                // Pulled up over the hero; the layout height shrinks by the overlap so the
                // content below follows the cards.
                Row(
                    modifier = Modifier
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            val overlap = CARDS_OVERLAP.roundToPx()
                            layout(placeable.width, placeable.height - overlap) {
                                placeable.place(0, -overlap)
                            }
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // The cards are labelled, so the amounts carry no sign and no color (as designed).
                    SummaryCard(
                        title = strings.home.income,
                        amount = amountText(state.totals.income, strings.common.currency),
                        amountColor = MaterialTheme.colorScheme.onSurface,
                        icon = painterResource(Res.drawable.ic_south_west),
                        iconColor = IncomeColor,
                        iconTint = IncomeTintColor,
                        shadowElevation = 6.dp,
                        modifier = Modifier.weight(1f),
                    )
                    SummaryCard(
                        title = strings.home.expenses,
                        amount = amountText(state.totals.expense, strings.common.currency),
                        amountColor = MaterialTheme.colorScheme.onSurface,
                        icon = painterResource(Res.drawable.ic_north_east),
                        iconColor = ExpenseAccentColor,
                        iconTint = ExpenseTintColor,
                        shadowElevation = 6.dp,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = strings.home.transactions,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = strings.home.seeAll,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable { rootNavigator.push(TransactionListScreen()) }
                            .padding(horizontal = 8.dp, vertical = 12.dp),
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
                            text = strings.home.transactionsEmpty,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            state.days.forEachIndexed { index, day ->
                item(key = day.date.toString()) {
                    Column(
                        modifier = Modifier.padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = if (index == 0) 0.dp else 16.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = dayTitle(day.date, today, strings.home),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        ListCard {
                            day.transactions.forEachIndexed { rowIndex, transaction ->
                                if (rowIndex > 0) ListCardDivider()
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

@Composable
private fun Hero(
    state: HomeState,
    strings: HomeStrings,
    currency: String,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
) {
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
            )
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 72.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Profile and notifications are hidden until they are built; spacers keep the pill centred.
            Spacer(modifier = Modifier.size(44.dp))
            MonthSwitcher(
                month = state.month,
                nextEnabled = state.month < state.currentMonth,
                previousDescription = strings.previousMonth,
                nextDescription = strings.nextMonth,
                onPreviousClick = onPreviousMonthClick,
                onNextClick = onNextMonthClick,
                containerColor = HeroOverlayColor,
                contentColor = onPrimary,
            )
            Spacer(modifier = Modifier.size(44.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = strings.totalBalance,
                style = MaterialTheme.typography.labelLarge,
                color = HeroSecondaryColor,
            )
            Row {
                val balanceText = signedText(state.balance, positiveSign = "")
                Text(
                    text = balanceText,
                    // A long balance drops to a smaller size so it stays on one line.
                    style = if (balanceText.length <= BIG_BALANCE_MAX_LENGTH) MaterialTheme.typography.displayLarge
                    else MaterialTheme.typography.headlineLarge,
                    color = onPrimary,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.alignByBaseline(),
                )
                Text(
                    text = " $currency",
                    style = MaterialTheme.typography.titleLarge,
                    color = HeroTertiaryColor,
                    maxLines = 1,
                    modifier = Modifier.alignByBaseline(),
                )
            }
            val monthChange = state.totals.income - state.totals.expense
            Text(
                text = "${signedText(monthChange, positiveSign = INCOME_SIGN)} $currency ${strings.thisMonth}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = onPrimary,
                modifier = Modifier
                    .background(HeroOverlayColor, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
            )
        }
    }
}

private val CARDS_OVERLAP = 44.dp
// "12 480 000": up to 8 digits fit in the big size next to the currency.
private const val BIG_BALANCE_MAX_LENGTH = 10
// The avatar shows the first letter of the app name; there are no user profiles.
private const val APP_INITIAL = "C"
private const val INCOME_SIGN = "+"
private const val EXPENSE_SIGN = "−"

private fun amountText(tiyin: Long, currency: String): String = "${formatBalance(tiyin)} $currency"

// Zero gets no sign.
private fun signedText(tiyin: Long, positiveSign: String): String {
    val sign = if (tiyin > 0) positiveSign else if (tiyin < 0) EXPENSE_SIGN else ""
    return "$sign${formatBalance(abs(tiyin))}"
}

// "Сегодня, 15 ноября", "Вчера, 14 ноября", "11 ноября".
private fun dayTitle(date: LocalDate, today: LocalDate, strings: HomeStrings): String =
    when (date) {
        today -> "${strings.today}, ${formatDayMonth(date)}"
        today.minus(1, DateTimeUnit.DAY) -> "${strings.yesterday}, ${formatDayMonth(date)}"
        else -> formatDayMonth(date)
    }
