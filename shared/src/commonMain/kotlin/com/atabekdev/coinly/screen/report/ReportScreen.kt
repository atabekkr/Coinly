package com.atabekdev.coinly.screen.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import coinly.shared.generated.resources.Res
import coinly.shared.generated.resources.ic_bar_chart
import com.atabekdev.coinly.core.common.util.formatBalance
import com.atabekdev.coinly.data.transaction.model.CategoryExpense
import com.atabekdev.coinly.data.transaction.model.TransactionType
import com.atabekdev.coinly.ui.components.EmojiTile
import com.atabekdev.coinly.ui.components.ListCard
import com.atabekdev.coinly.ui.components.ListCardDivider
import com.atabekdev.coinly.ui.components.MonthSwitcher
import com.atabekdev.coinly.ui.components.ScreenHeader
import com.atabekdev.coinly.ui.components.SegmentedControl
import com.atabekdev.coinly.ui.localization.LocalStrings
import com.atabekdev.coinly.ui.theme.categoryColor
import com.atabekdev.coinly.ui.theme.categoryTint
import org.jetbrains.compose.resources.painterResource
import kotlin.math.max
import kotlin.math.roundToInt

object ReportScreen : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = LocalStrings.current.report.title
            val icon = painterResource(Res.drawable.ic_bar_chart)
            return remember(title, icon) { TabOptions(index = 1u, title = title, icon = icon) }
        }

    @Composable
    override fun Content() {
        val strings = LocalStrings.current
        val model = koinScreenModel<ReportScreenModel>()
        val state by model.state.collectAsState()
        // One color per row, shared by the ring slice and the row bar.
        val colors = remember(state.categories) {
            state.categories.map { categoryColor(it.sortOrder) }
        }

        Scaffold(
            topBar = {
                ScreenHeader(
                    title = strings.report.title,
                    modifier = Modifier.statusBarsPadding(),
                ) {
                    MonthSwitcher(
                        month = state.month,
                        nextEnabled = state.month < state.currentMonth,
                        previousDescription = strings.report.previousMonth,
                        nextDescription = strings.report.nextMonth,
                        onPreviousClick = { model.onEvent(ReportEvent.OnPreviousMonthClick) },
                        onNextClick = { model.onEvent(ReportEvent.OnNextMonthClick) },
                    )
                }
            },
        ) { innerPadding ->
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
                ) {
                    item {
                        val types = listOf(
                            TransactionType.EXPENSE to strings.report.expenses,
                            TransactionType.INCOME to strings.report.income,
                        )
                        SegmentedControl(
                            options = types.map { it.second },
                            selectedIndex = types.indexOfFirst { it.first == state.type },
                            onSelect = { index -> model.onEvent(ReportEvent.ChangeType(types[index].first)) },
                        )
                    }

                    if (state.categories.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = if (state.type == TransactionType.EXPENSE) strings.report.expensesEmpty
                                    else strings.report.incomeEmpty,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    } else {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                shape = MaterialTheme.shapes.extraLarge,
                                color = MaterialTheme.colorScheme.surface,
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    ExpenseRing(
                                        categories = state.categories,
                                        colors = colors,
                                        total = state.total,
                                        totalLabel = strings.report.total,
                                        currency = strings.common.currency,
                                    )
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                    ) {
                                        Stat(
                                            label = strings.report.dailyAverage,
                                            value = amountText(state.dailyAverage, strings.common.currency),
                                        )
                                        Stat(
                                            label = strings.report.topCategory,
                                            value = state.categories.first().name,
                                        )
                                    }
                                }
                            }
                        }
                        item {
                            Text(
                                text = strings.report.byCategory,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(top = 20.dp, bottom = 12.dp),
                            )
                        }
                        item {
                            // The bar is scaled to the biggest category, not to the total.
                            val biggest = state.categories.maxOf { it.amount }
                            ListCard {
                                state.categories.forEachIndexed { index, expense ->
                                    if (index > 0) ListCardDivider()
                                    ExpenseRow(
                                        expense = expense,
                                        color = colors[index],
                                        share = shareOf(expense.amount, state.total),
                                        barFraction = shareOf(expense.amount, biggest).toFloat(),
                                        currency = strings.common.currency,
                                        lessThanOnePercent = strings.report.lessThanOnePercent,
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

@Composable
private fun Stat(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ExpenseRing(
    categories: List<CategoryExpense>,
    colors: List<Color>,
    total: Long,
    totalLabel: String,
    currency: String,
) {
    Box(
        modifier = Modifier.size(RING_SIZE),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = RING_STROKE.toPx()
            val inset = strokeWidth / 2
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            // A gap between slices only makes sense with more than one; a lone slice is a closed ring.
            val gap = if (categories.size > 1) SLICE_GAP_DEGREES else 0f
            var start = START_ANGLE
            categories.forEachIndexed { index, expense ->
                val sweep = FULL_CIRCLE_DEGREES * shareOf(expense.amount, total).toFloat()
                drawArc(
                    color = colors[index],
                    startAngle = start + gap / 2,
                    sweepAngle = max(sweep - gap, MIN_SWEEP_DEGREES),
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = strokeWidth),
                )
                // Advance by the true share so a widened tiny slice does not shift the others.
                start += sweep
            }
        }
        Column(
            modifier = Modifier.padding(horizontal = RING_STROKE + 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = totalLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = amountText(total, currency),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ExpenseRow(
    expense: CategoryExpense,
    color: Color,
    share: Double,
    barFraction: Float,
    currency: String,
    lessThanOnePercent: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 58.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EmojiTile(
            emoji = expense.emoji,
            containerColor = categoryTint(expense.sortOrder),
            size = 36.dp,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = expense.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = amountText(expense.amount, currency),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant, CircleShape),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(barFraction.coerceIn(MIN_BAR_FRACTION, 1f))
                            .background(color, CircleShape),
                    )
                }
                Text(
                    text = percentText(share, lessThanOnePercent),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(36.dp),
                )
            }
        }
    }
}

private val RING_SIZE = 168.dp
private val RING_STROKE = 20.dp
private const val START_ANGLE = -90f
private const val FULL_CIRCLE_DEGREES = 360f
private const val SLICE_GAP_DEGREES = 2f
private const val MIN_SWEEP_DEGREES = 1f
// Keeps the bar of a tiny category visible.
private const val MIN_BAR_FRACTION = 0.02f
private const val PERCENT_SIGN = "%"

private fun shareOf(amount: Long, total: Long): Double = amount.toDouble() / total

// Breakdown amounts sit under an "expenses" / "income" switch, so they carry no sign.
private fun amountText(tiyin: Long, currency: String): String = "${formatBalance(tiyin)} $currency"

// Any share below 1% reads "<1%"; from 1% up it is rounded to the nearest whole percent.
private fun percentText(share: Double, lessThanOnePercent: String): String =
    if (share < 0.01) lessThanOnePercent else "${(share * 100).roundToInt()}$PERCENT_SIGN"
