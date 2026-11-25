package com.atabekdev.coinly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atabekdev.coinly.data.category.model.Category
import com.atabekdev.coinly.ui.theme.categoryTint

// Category picker: tiles in four columns, the selected one outlined.
@Composable
fun CategoryGrid(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        categories.chunked(CATEGORY_COLUMNS).forEach { rowCategories ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowCategories.forEach { category ->
                    CategoryTile(
                        category = category,
                        selected = category.id == selectedCategoryId,
                        onClick = { onCategoryClick(category.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(CATEGORY_COLUMNS - rowCategories.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryTile(
    category: Category,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier.height(72.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
        ) {
            EmojiTile(
                emoji = category.emoji,
                containerColor = categoryTint(category.sortOrder),
                size = 36.dp,
            )
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelSmall,
                lineHeight = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private const val CATEGORY_COLUMNS = 4
