package com.atabekdev.coinly.screen.transaction.edit

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coinly.shared.generated.resources.Res
import coinly.shared.generated.resources.ic_arrow_back
import coinly.shared.generated.resources.ic_calendar_today
import coinly.shared.generated.resources.ic_delete
import com.atabekdev.coinly.core.common.util.formatDate
import com.atabekdev.coinly.data.transaction.model.TransactionType
import com.atabekdev.coinly.ui.components.visualTransformations.AmountVisualTransformation
import com.atabekdev.coinly.ui.localization.LocalStrings
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

        LaunchedEffect(state.saveSuccess, state.deleteSuccess) {
            if (state.saveSuccess || state.deleteSuccess) navigator.pop()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (state.isEditMode) strings.transaction.editTitle
                            else strings.transaction.newTitle
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_arrow_back),
                                contentDescription = strings.common.back,
                            )
                        }
                    },
                    actions = {
                        if (state.isEditMode) {
                            IconButton(onClick = { model.onEvent(TransactionEditEvent.OnDeleteClick) }) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_delete),
                                    contentDescription = strings.transaction.delete,
                                )
                            }
                        }
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val types = listOf(
                    TransactionType.EXPENSE to strings.transaction.expense,
                    TransactionType.INCOME to strings.transaction.income,
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    types.forEachIndexed { index, (type, label) ->
                        SegmentedButton(
                            selected = state.type == type,
                            onClick = { model.onEvent(TransactionEditEvent.ChangeType(type)) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = types.size),
                        ) {
                            Text(label)
                        }
                    }
                }

                OutlinedTextField(
                    value = state.amount,
                    onValueChange = { model.onEvent(TransactionEditEvent.ChangeAmount(it)) },
                    label = { Text(strings.transaction.amount) },
                    suffix = { Text(strings.common.currency) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = remember { AmountVisualTransformation() },
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = strings.transaction.category,
                    style = MaterialTheme.typography.titleSmall,
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.categories.filter { it.type == state.type }.forEach { category ->
                        FilterChip(
                            selected = category.id == state.selectedCategoryId,
                            onClick = { model.onEvent(TransactionEditEvent.ChangeCategory(category.id)) },
                            label = { Text("${category.emoji} ${category.name}") },
                        )
                    }
                }

                // A read-only text field has no onClick, so the picker opens on its press interaction.
                val dateInteractionSource = remember { MutableInteractionSource() }
                LaunchedEffect(dateInteractionSource) {
                    dateInteractionSource.interactions.collect { interaction ->
                        if (interaction is PressInteraction.Release) {
                            model.onEvent(TransactionEditEvent.OnDateClick)
                        }
                    }
                }
                OutlinedTextField(
                    value = formatDate(state.date),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(strings.transaction.date) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_calendar_today),
                            contentDescription = strings.transaction.selectDate,
                        )
                    },
                    interactionSource = dateInteractionSource,
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = state.note,
                    onValueChange = { model.onEvent(TransactionEditEvent.ChangeNote(it)) },
                    label = { Text(strings.transaction.note) },
                    supportingText = { Text("${state.note.length}/$MAX_NOTE_LENGTH") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Button(
                    onClick = { model.onEvent(TransactionEditEvent.OnSaveClick) },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(strings.transaction.save)
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

// Material DatePicker works with UTC midnight milliseconds.
private fun LocalDate.toPickerMillis(): Long = atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

private fun Long.pickerMillisToDate(): LocalDate =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date
