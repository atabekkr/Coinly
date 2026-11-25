package com.atabekdev.coinly.screen.budget.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coinly.shared.generated.resources.Res
import coinly.shared.generated.resources.ic_close
import coinly.shared.generated.resources.ic_delete
import com.atabekdev.coinly.core.common.util.convertToTiyin
import com.atabekdev.coinly.core.common.util.formatBalance
import com.atabekdev.coinly.ui.components.AmountKeypad
import com.atabekdev.coinly.ui.components.CategoryGrid
import com.atabekdev.coinly.ui.components.PrimaryButton
import com.atabekdev.coinly.ui.components.RoundIconButton
import com.atabekdev.coinly.ui.localization.LocalStrings
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parametersOf

class BudgetEditScreen(
    private val budgetId: String? = null,
) : Screen {

    // Screens with arguments need a unique key, otherwise Voyager can hand a new screen
    // the cached ScreenModel of a previous instance.
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val strings = LocalStrings.current
        val model = koinScreenModel<BudgetEditScreenModel> { parametersOf(budgetId) }
        val state by model.state.collectAsState()

        LaunchedEffect(state.saveSuccess, state.deleteSuccess) {
            if (state.saveSuccess || state.deleteSuccess) navigator.pop()
        }

        Scaffold { innerPadding ->
            BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        // At least one screen tall, so the amount sits in the free space between
                        // the top and bottom blocks; on a short screen the content scrolls.
                        .heightIn(min = maxHeight)
                        .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RoundIconButton(
                            icon = painterResource(Res.drawable.ic_close),
                            contentDescription = strings.common.close,
                            onClick = { navigator.pop() },
                        )
                        Text(
                            text = if (state.isEditMode) strings.budget.editTitle else strings.budget.newTitle,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            modifier = Modifier.weight(1f),
                        )
                        if (state.isEditMode) {
                            RoundIconButton(
                                icon = painterResource(Res.drawable.ic_delete),
                                contentDescription = strings.budget.delete,
                                onClick = { model.onEvent(BudgetEditEvent.OnDeleteClick) },
                            )
                        } else {
                            // Keeps the title centred.
                            Spacer(modifier = Modifier.size(ROUND_BUTTON_SIZE))
                        }
                    }

                    AmountDisplay(
                        amount = state.amount,
                        currency = strings.common.currency,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (!state.isEditMode && !state.isLoading && state.categories.isEmpty()) {
                            Text(
                                text = strings.budget.allBudgeted,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        CategoryGrid(
                            categories = state.categories,
                            selectedCategoryId = state.selectedCategoryId,
                            onCategoryClick = { model.onEvent(BudgetEditEvent.ChangeCategory(it)) },
                        )
                        AmountKeypad(
                            deleteDescription = strings.budget.deleteDigit,
                            onDigitClick = { digit ->
                                model.onEvent(BudgetEditEvent.ChangeAmount(state.amount + digit))
                            },
                            onDeleteClick = {
                                model.onEvent(BudgetEditEvent.ChangeAmount(state.amount.dropLast(1)))
                            },
                        )
                        PrimaryButton(
                            text = strings.budget.save,
                            onClick = { model.onEvent(BudgetEditEvent.OnSaveClick) },
                            enabled = !state.isLoading,
                        )
                    }
                }
            }
        }

        if (state.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { model.onEvent(BudgetEditEvent.Idle) },
                title = { Text(strings.budget.deleteTitle) },
                text = { Text(strings.budget.deleteMessage) },
                confirmButton = {
                    TextButton(onClick = { model.onEvent(BudgetEditEvent.OnDeleteConfirm) }) {
                        Text(strings.budget.delete)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { model.onEvent(BudgetEditEvent.Idle) }) {
                        Text(strings.common.cancel)
                    }
                },
            )
        }

        state.errorMessage?.let { message ->
            AlertDialog(
                onDismissRequest = { model.onEvent(BudgetEditEvent.Idle) },
                text = { Text(message) },
                confirmButton = {
                    TextButton(onClick = { model.onEvent(BudgetEditEvent.Idle) }) {
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
    currency: String,
    modifier: Modifier = Modifier,
) {
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
            text = formatBalance(convertToTiyin(amount.toLongOrNull() ?: 0L)),
            style = style,
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
