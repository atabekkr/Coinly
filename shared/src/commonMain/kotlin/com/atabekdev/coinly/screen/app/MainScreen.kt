package com.atabekdev.coinly.screen.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import coinly.shared.generated.resources.Res
import coinly.shared.generated.resources.ic_add
import com.atabekdev.coinly.screen.budget.list.BudgetListScreen
import com.atabekdev.coinly.screen.home.HomeScreen
import com.atabekdev.coinly.screen.report.ReportScreen
import com.atabekdev.coinly.screen.settings.SettingsScreen
import com.atabekdev.coinly.screen.transaction.edit.TransactionEditScreen
import com.atabekdev.coinly.ui.localization.LocalStrings
import org.jetbrains.compose.resources.painterResource

// Bottom tabs. Lives in the root navigator, so detail screens pushed there cover the tab bar.
class MainScreen : Screen {

    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow
        val navigationInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        TabNavigator(HomeScreen) {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                // The bar is drawn over the content, which stops at the bar's top edge; the "+"
                // button sticks out above it. The bottom inset is consumed so a tab's own Scaffold
                // does not add it a second time; the top inset is left to the tabs (the Home hero
                // runs under the status bar).
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = BAR_HEIGHT + navigationInset)
                        .consumeWindowInsets(WindowInsets.navigationBars),
                ) {
                    CurrentTab()
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(ADD_BUTTON_OVERHANG + BAR_HEIGHT + navigationInset),
                ) {
                    NotchedBarBackground(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(BAR_HEIGHT + navigationInset),
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = navigationInset)
                            .fillMaxWidth()
                            .height(BAR_HEIGHT)
                            .padding(horizontal = 8.dp)
                            .selectableGroup(),
                    ) {
                        TabItem(HomeScreen)
                        TabItem(ReportScreen)
                        // The "+" button's column.
                        Spacer(modifier = Modifier.weight(1f))
                        TabItem(BudgetListScreen)
                        TabItem(SettingsScreen)
                    }
                    Surface(
                        onClick = { rootNavigator.push(TransactionEditScreen()) },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(ADD_BUTTON_SIZE)
                            .shadow(
                                elevation = 10.dp,
                                shape = CircleShape,
                                ambientColor = MaterialTheme.colorScheme.primary,
                                spotColor = MaterialTheme.colorScheme.primary,
                            ),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_add),
                                contentDescription = LocalStrings.current.transaction.add,
                                modifier = Modifier.size(26.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

// White bar whose top edge dips around the "+" button, with a hairline along that edge only.
@Composable
private fun NotchedBarBackground(modifier: Modifier = Modifier) {
    val fill = MaterialTheme.colorScheme.surface
    val outline = MaterialTheme.colorScheme.surfaceVariant
    Canvas(modifier = modifier) {
        val center = size.width / 2
        // The dip from the design, in dp: 104 wide at the top, 40 deep.
        fun x(dp: Float) = center + dp.dp.toPx()
        fun y(dp: Float) = dp.dp.toPx()
        val edge = Path().apply {
            moveTo(0f, 0f)
            lineTo(x(-52f), 0f)
            cubicTo(x(-40f), 0f, x(-36f), y(8f), x(-33f), y(16f))
            cubicTo(x(-28f), y(30f), x(-15f), y(40f), x(0f), y(40f))
            cubicTo(x(15f), y(40f), x(28f), y(30f), x(33f), y(16f))
            cubicTo(x(36f), y(8f), x(40f), 0f, x(52f), 0f)
            lineTo(size.width, 0f)
        }
        val shape = Path().apply {
            addPath(edge)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path = shape, color = fill)
        drawPath(path = edge, color = outline, style = Stroke(width = 1.dp.toPx()))
    }
}

@Composable
private fun RowScope.TabItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    val options = tab.options
    val selected = tabNavigator.current.key == tab.key
    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.medium)
            .selectable(selected = selected, role = Role.Tab, onClick = { tabNavigator.current = tab }),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
    ) {
        options.icon?.let {
            Icon(painter = it, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        }
        Text(
            text = options.title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = color,
            maxLines = 1,
        )
    }
}

private val BAR_HEIGHT = 64.dp
private val ADD_BUTTON_SIZE = 56.dp
// The "+" button's top is this far above the bar's top edge.
private val ADD_BUTTON_OVERHANG = 24.dp
