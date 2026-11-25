package com.atabekdev.coinly.screen.budget.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import coinly.shared.generated.resources.Res
import coinly.shared.generated.resources.ic_add
import coinly.shared.generated.resources.ic_pie_chart
import com.atabekdev.coinly.core.common.util.formatBalance
import com.atabekdev.coinly.data.budget.model.BudgetProgress
import com.atabekdev.coinly.screen.budget.edit.BudgetEditScreen
import com.atabekdev.coinly.ui.components.EmojiTile
import com.atabekdev.coinly.ui.components.ListCard
import com.atabekdev.coinly.ui.components.ListCardDivider
import com.atabekdev.coinly.ui.components.RoundIconButton
import com.atabekdev.coinly.ui.components.ScreenHeader
import com.atabekdev.coinly.ui.localization.BudgetStrings
import com.atabekdev.coinly.ui.localization.LocalStrings
import com.atabekdev.coinly.ui.theme.ExpenseAccentColor
import com.atabekdev.coinly.ui.theme.ExpenseTintColor
import com.atabekdev.coinly.ui.theme.HeroSecondaryColor
import com.atabekdev.coinly.ui.theme.HeroTrackColor
import com.atabekdev.coinly.ui.theme.WarningBarColor
import com.atabekdev.coinly.ui.theme.categoryTint
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs

object BudgetListScreen : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = LocalStrings.current.budget.title
            val icon = painterResource(Res.drawable.ic_pie_chart)
            return remember(title, icon) { TabOptions(index = 2u, title = title, icon = icon) }
        }

    @Composable
    override fun Content() {
        // Tabs live in a nested navigator; the edit screen opens over the tabs in the root one.
        val rootNavigator = checkNotNull(LocalNavigator.currentOrThrow.parent)
        val strings = LocalStrings.current
        val model = koinScreenModel<BudgetListScreenModel>()
        val state by model.state.collectAsState()

        Scaffold(
            topBar = {
                ScreenHeader(
                    title = strings.budget.title,
                    modifier = Modifier.statusBarsPadding(),
                ) {
                    RoundIconButton(
                        icon = painterResource(Res.drawable.ic_add),
                        contentDescription = strings.budget.add,
                        onClick = { rootNavigator.push(BudgetEditScreen()) },
                    )
                }
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
            ) {
                if (state.budgets.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = strings.budget.empty,
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                text = strings.budget.emptyHint,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                } else {
                    item {
                        MonthBudgetCard(
                            state = state,
                            strings = strings.budget,
                            monthName = strings.common.monthsNominative[state.currentMonth.month.ordinal],
                            currency = strings.common.currency,
                        )
                    }
                    item {
                        Text(
                            text = strings.budget.categories,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 20.dp, bottom = 12.dp),
                        )
                    }
                    item {
                        ListCard {
                            state.budgets.forEachIndexed { index, budget ->
                                if (index > 0) ListCardDivider()
                                BudgetRow(
                                    budget = budget,
                                    strings = strings.budget,
                                    currency = strings.common.currency,
                                    onClick = { rootNavigator.push(BudgetEditScreen(budget.id)) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MonthBudgetCard(
    state: BudgetListState,
    strings: BudgetStrings,
    monthName: String,
    currency: String,
) {
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val left = state.totalLimit - state.totalSpent
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primary,
        contentColor = onPrimary,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "${strings.monthBudget} ${monthName.lowercase()}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = HeroSecondaryColor,
                )
                // The "left of" part wraps under the amount when both do not fit in one line.
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${signed(left)} $currency",
                        style = MaterialTheme.typography.headlineLarge,
                        maxLines = 1,
                        modifier = Modifier.alignByBaseline(),
                    )
                    Text(
                        text = "${strings.leftOf} ${formatBalance(state.totalLimit)} $currency",
                        style = MaterialTheme.typography.labelLarge,
                        color = HeroSecondaryColor,
                        maxLines = 1,
                        modifier = Modifier.alignByBaseline(),
                    )
                }
            }
            ProgressBar(
                fraction = fractionOf(state.totalSpent, state.totalLimit),
                color = onPrimary,
                trackColor = HeroTrackColor,
                height = 10,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${percentOf(state.totalSpent, state.totalLimit)}$PERCENT_SIGN ${strings.spent}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "${strings.daysLeft} ${state.daysLeft}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun BudgetRow(
    budget: BudgetProgress,
    strings: BudgetStrings,
    currency: String,
    onClick: () -> Unit,
) {
    val left = budget.limit - budget.spent
    // Warning from 80% of the limit, danger from 100%.
    val level = when {
        budget.spent >= budget.limit -> BudgetLevel.DANGER
        budget.spent * 100 >= budget.limit * WARNING_PERCENT -> BudgetLevel.WARNING
        else -> BudgetLevel.NORMAL
    }
    val barColor: Color
    val trackColor: Color
    val textColor: Color
    when (level) {
        BudgetLevel.NORMAL -> {
            barColor = MaterialTheme.colorScheme.primary
            trackColor = MaterialTheme.colorScheme.primaryContainer
            textColor = MaterialTheme.colorScheme.onSurfaceVariant
        }
        BudgetLevel.WARNING -> {
            barColor = WarningBarColor
            trackColor = ExpenseTintColor
            textColor = ExpenseAccentColor
        }
        BudgetLevel.DANGER -> {
            barColor = MaterialTheme.colorScheme.error
            trackColor = MaterialTheme.colorScheme.errorContainer
            textColor = MaterialTheme.colorScheme.error
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .heightIn(min = 66.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EmojiTile(
            emoji = budget.emoji,
            containerColor = categoryTint(budget.sortOrder),
            size = 36.dp,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = budget.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).alignByBaseline(),
                )
                Text(
                    text = formatBalance(budget.spent),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    modifier = Modifier.alignByBaseline(),
                )
                Text(
                    text = "${strings.of} ${formatBalance(budget.limit)} $currency",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.alignByBaseline(),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ProgressBar(
                        fraction = fractionOf(budget.spent, budget.limit),
                        color = barColor,
                        trackColor = trackColor,
                        height = 6,
                    )
                }
                Text(
                    text = if (left < 0) "${strings.overBy} ${formatBalance(abs(left))}"
                    else "${strings.left} ${formatBalance(left)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (level == BudgetLevel.NORMAL) FontWeight.Medium else FontWeight.SemiBold,
                    color = textColor,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun ProgressBar(
    fraction: Float,
    color: Color,
    trackColor: Color,
    height: Int,
) {
    Box(modifier = Modifier.fillMaxWidth().height(height.dp).background(trackColor, CircleShape)) {
        if (fraction > 0f) {
            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(fraction).background(color, CircleShape))
        }
    }
}

private enum class BudgetLevel { NORMAL, WARNING, DANGER }

private const val WARNING_PERCENT = 80
private const val PERCENT_SIGN = "%"
private const val MINUS_SIGN = "−"

// Spent share of the limit for a bar: 0..1, an overspent limit is a full bar.
private fun fractionOf(spent: Long, limit: Long): Float =
    if (limit <= 0L) 0f else (spent.toDouble() / limit).toFloat().coerceIn(0f, 1f)

// Whole percent, rounded down; can exceed 100 when overspent.
private fun percentOf(spent: Long, limit: Long): Long = if (limit <= 0L) 0L else spent * 100 / limit

private fun signed(tiyin: Long): String = "${if (tiyin < 0) MINUS_SIGN else ""}${formatBalance(abs(tiyin))}"
