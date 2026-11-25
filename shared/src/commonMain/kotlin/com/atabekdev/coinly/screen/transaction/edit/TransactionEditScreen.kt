package com.atabekdev.coinly.screen.transaction.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coinly.shared.generated.resources.Res
import coinly.shared.generated.resources.ic_calendar_today
import coinly.shared.generated.resources.ic_close
import coinly.shared.generated.resources.ic_delete
import coinly.shared.generated.resources.ic_edit
import com.atabekdev.coinly.core.common.util.convertToTiyin
import com.atabekdev.coinly.core.common.util.formatBalance
import com.atabekdev.coinly.core.common.util.formatDate
import com.atabekdev.coinly.data.transaction.model.TransactionType
import com.atabekdev.coinly.ui.components.AmountKeypad
import com.atabekdev.coinly.ui.components.CategoryGrid
import com.atabekdev.coinly.ui.components.PrimaryButton
import com.atabekdev.coinly.ui.components.RoundIconButton
import com.atabekdev.coinly.ui.components.SegmentedControl
import com.atabekdev.coinly.ui.localization.LocalStrings
import com.atabekdev.coinly.ui.theme.IncomeColor
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parametersOf
import kotlin.time.Clock
import kotlin.time.Instant

class TransactionEditScreen(
    private val transactionId: String? = null,
) : Screen {

    // Screens with arguments need a unique key, otherwise Voyager can hand a new screen
    // the cached ScreenModel of a previous instance.
    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val strings = LocalStrings.current
        val model = koinScreenModel<TransactionEditScreenModel> { parametersOf(transactionId) }
        val state by model.state.collectAsState()
        val focusManager = LocalFocusManager.current

        LaunchedEffect(state.saveSuccess, state.deleteSuccess) {
            if (state.saveSuccess || state.deleteSuccess) navigator.pop()
        }

        Scaffold { innerPadding ->
            BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                // The on-screen keypad enters the amount; the system keyboard is only for the note.
                // A tap outside the note field closes it; tiles and keys clear focus in their handlers.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
                        .verticalScroll(rememberScrollState())
                        // At least one screen tall, so the amount sits in the free space between
                        // the top and bottom blocks; on a short screen the content scrolls.
                        .heightIn(min = maxHeight)
                        .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RoundIconButton(
                                icon = painterResource(Res.drawable.ic_close),
                                contentDescription = strings.common.close,
                                onClick = { navigator.pop() },
                            )
                            Text(
                                text = if (state.isEditMode) strings.transaction.editTitle
                                else strings.transaction.newTitle,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                modifier = Modifier.weight(1f),
                            )
                            if (state.isEditMode) {
                                RoundIconButton(
                                    icon = painterResource(Res.drawable.ic_delete),
                                    contentDescription = strings.transaction.delete,
                                    onClick = { model.onEvent(TransactionEditEvent.OnDeleteClick) },
                                )
                            } else {
                                // Keeps the title centred.
                                Spacer(modifier = Modifier.size(ROUND_BUTTON_SIZE))
                            }
                        }

                        val types = listOf(
                            TransactionType.EXPENSE to strings.transaction.expense,
                            TransactionType.INCOME to strings.transaction.income,
                        )
                        SegmentedControl(
                            options = types.map { it.second },
                            selectedIndex = types.indexOfFirst { it.first == state.type },
                            onSelect = { index ->
                                focusManager.clearFocus()
                                model.onEvent(TransactionEditEvent.ChangeType(types[index].first))
                            },
                        )
                    }

                    AmountDisplay(
                        amount = state.amount,
                        type = state.type,
                        currency = strings.common.currency,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CategoryGrid(
                            categories = state.categories.filter { it.type == state.type },
                            selectedCategoryId = state.selectedCategoryId,
                            onCategoryClick = { categoryId ->
                                focusManager.clearFocus()
                                model.onEvent(TransactionEditEvent.ChangeCategory(categoryId))
                            },
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                onClick = {
                                    focusManager.clearFocus()
                                    model.onEvent(TransactionEditEvent.OnDateClick)
                                },
                                modifier = Modifier.weight(1f).height(FIELD_HEIGHT),
                                shape = FieldShape,
                                color = MaterialTheme.colorScheme.surface,
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_calendar_today),
                                        contentDescription = strings.transaction.selectDate,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Text(
                                        text = formatDate(state.date),
                                        style = MaterialTheme.typography.labelLarge,
                                        maxLines = 1,
                                    )
                                }
                            }

                            BasicTextField(
                                value = state.note,
                                onValueChange = { model.onEvent(TransactionEditEvent.ChangeNote(it)) },
                                textStyle = MaterialTheme.typography.labelLarge
                                    .copy(color = MaterialTheme.colorScheme.onSurface),
                                maxLines = 1,
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f),
                                decorationBox = { innerTextField ->
                                    Row(
                                        modifier = Modifier
                                            .height(FIELD_HEIGHT)
                                            .background(MaterialTheme.colorScheme.surface, FieldShape)
                                            .padding(horizontal = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.ic_edit),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp),
                                        )
                                        Box(modifier = Modifier.weight(1f)) {
                                            if (state.note.isEmpty()) {
                                                Text(
                                                    text = strings.transaction.note,
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                )
                                            }
                                            innerTextField()
                                        }
                                    }
                                },
                            )
                        }

                        AmountKeypad(
                            deleteDescription = strings.transaction.deleteDigit,
                            onDigitClick = { digit ->
                                focusManager.clearFocus()
                                model.onEvent(TransactionEditEvent.ChangeAmount(state.amount + digit))
                            },
                            onDeleteClick = {
                                focusManager.clearFocus()
                                model.onEvent(TransactionEditEvent.ChangeAmount(state.amount.dropLast(1)))
                            },
                        )

                        PrimaryButton(
                            text = if (state.type == TransactionType.EXPENSE) strings.transaction.saveExpense
                            else strings.transaction.saveIncome,
                            onClick = {
                                focusManager.clearFocus()
                                model.onEvent(TransactionEditEvent.OnSaveClick)
                            },
                            enabled = !state.isLoading,
                        )
                    }
                }
            }
        }

        if (state.showDatePicker) {
            val todayMillis = remember {
                Clock.System.todayIn(TimeZone.currentSystemDefault()).toPickerMillis()
            }
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = state.date.toPickerMillis(),
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= todayMillis
                },
            )
            DatePickerDialog(
                onDismissRequest = { model.onEvent(TransactionEditEvent.Idle) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val millis = datePickerState.selectedDateMillis
                            model.onEvent(
                                if (millis != null) TransactionEditEvent.ChangeDate(millis.pickerMillisToDate())
                                else TransactionEditEvent.Idle
                            )
                        },
                    ) {
                        Text(strings.common.ok)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { model.onEvent(TransactionEditEvent.Idle) }) {
                        Text(strings.common.cancel)
                    }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (state.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { model.onEvent(TransactionEditEvent.Idle) },
                title = { Text(strings.transaction.deleteTitle) },
                text = { Text(strings.transaction.deleteMessage) },
                confirmButton = {
                    TextButton(onClick = { model.onEvent(TransactionEditEvent.OnDeleteConfirm) }) {
                        Text(strings.transaction.delete)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { model.onEvent(TransactionEditEvent.Idle) }) {
                        Text(strings.common.cancel)
                    }
                },
            )
        }

        state.errorMessage?.let { message ->
            AlertDialog(
                onDismissRequest = { model.onEvent(TransactionEditEvent.Idle) },
                text = { Text(message) },
                confirmButton = {
                    TextButton(onClick = { model.onEvent(TransactionEditEvent.Idle) }) {
                        Text(strings.common.ok)
                    }
                },
            )
        }
    }
}

// Whole sums with thousand separators; the text shrinks as the amount gets longer.
@Composable
private fun AmountDisplay(
    amount: String,
    type: TransactionType,
    currency: String,
    modifier: Modifier = Modifier,
) {
    val sums = amount.toLongOrNull() ?: 0L
    val isIncome = type == TransactionType.INCOME
    // Zero gets no sign.
    val sign = if (sums == 0L) "" else if (isIncome) INCOME_SIGN else EXPENSE_SIGN
    val style = when {
        amount.length <= 6 -> MaterialTheme.typography.displayMedium
        amount.length <= 9 -> MaterialTheme.typography.displaySmall
        else -> MaterialTheme.typography.headlineLarge
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "$sign${formatBalance(convertToTiyin(sums))}",
            style = style,
            color = if (isIncome) IncomeColor else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.alignByBaseline(),
        )
        Text(
            text = " $currency",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            modifier = Modifier.alignByBaseline(),
        )
    }
}

// Same size as RoundIconButton.
private val ROUND_BUTTON_SIZE = 44.dp
private val FIELD_HEIGHT = 48.dp
private val FieldShape = RoundedCornerShape(14.dp)
private const val INCOME_SIGN = "+"
private const val EXPENSE_SIGN = "−"

// Material DatePicker works with UTC midnight milliseconds.
private fun LocalDate.toPickerMillis(): Long = atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

private fun Long.pickerMillisToDate(): LocalDate =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date
