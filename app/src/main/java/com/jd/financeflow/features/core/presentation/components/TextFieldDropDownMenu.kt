package com.jd.financeflow.features.core.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

import com.jd.financeflow.ui.theme.FinanceFlowTheme

/**
 * Larg drop down menu for selecting an item from a list of items. Composable is using outlined
 * text field to show selected content, and a dialog to show list of selectable items.
 *
 * @param items List of items to select from.
 * @param onItemSelected Callback to invoke when an item is selected.
 * @param modifier Modifier for the composable.
 * @param label Label for the outlined text field.
 * @param enabled Whether the composable is enabled or not.
 * @param notSetLabel Label for the "not set" option.
 * @param selectedIndex Index of the currently selected item.
 * @param selectedItemToString Function to convert the selected item to a string.
 * @param drawItem Composable to draw each item.
 */
@Composable
fun <T> LargeDropdownMenu(
    items: List<T>,
    onItemSelected: (index: Int, item: T) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    notSetLabel: String? = null,
    selectedIndex: Int = -1,
    selectedItemToString: (T) -> String = { it.toString() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    drawItem: @Composable (T, Boolean, Boolean, () -> Unit) -> Unit = { item, selected, itemEnabled, onClick ->
        LargeDropdownMenuItem(
            text = item.toString(),
            selected = selected,
            enabled = itemEnabled,
            onClick = onClick,
        )
    },
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        OutlinedTextField(
            label = {
                if (label != null) Text(label)
            },
            value = items.getOrNull(selectedIndex)?.let { selectedItemToString(it) } ?: "",
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (expanded) {
                    Icons.Filled.KeyboardArrowUp
                } else {
                    Icons.Filled.KeyboardArrowDown
                }
            },
            onValueChange = { },
            shape = shape,
            readOnly = true,
            colors = colors
        )

        // Transparent clickable surface on top of OutlinedTextField
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable(enabled = enabled) { expanded = true },
            color = Color.Transparent,
        ) {}
    }

    if (expanded) {
        Dialog(
            onDismissRequest = { expanded = false },
        ) {
            FinanceFlowTheme {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                ) {
                    val listState = rememberLazyListState()
                    if (selectedIndex > -1) {
                        LaunchedEffect("ScrollToSelected") {
                            listState.scrollToItem(index = selectedIndex)
                        }
                    }

                    LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
                        if (notSetLabel != null) {
                            item {
                                LargeDropdownMenuItem(
                                    text = notSetLabel,
                                    selected = false,
                                    enabled = false,
                                    onClick = { },
                                )
                            }
                        }
                        itemsIndexed(items) { index, item ->
                            // Add top space for first item
                            if (index == 0) {
                                Spacer(Modifier.padding(8.dp))
                            }

                            val selectedItem = index == selectedIndex
                            drawItem(
                                item,
                                selectedItem,
                                true
                            ) {
                                onItemSelected(index, item)
                                expanded = false
                            }

                            // Add horizonal divider for all items except last
                            if (index == items.lastIndex) {
                                Spacer(Modifier.padding(8.dp))
                            } else {
                                HorizontalDivider(Modifier.padding(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable for a single dropdown menu item.
 *
 * @param text Text to display in the item.
 * @param selected Whether the item is selected.
 * @param enabled Whether the item is enabled.
 * @param onClick Callback to invoke when the item is clicked.
 */
@Composable
private fun LargeDropdownMenuItem(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.0F)
        selected -> MaterialTheme.colorScheme.primary.copy(alpha = 1.0F)
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 1.0F)
    }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(
            modifier = Modifier
                .clickable(enabled) { onClick() }
                .fillMaxWidth()
                .padding(16.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TextFieldDropDownMenuPreview() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    FinanceFlowTheme {
        Column {
            Spacer(modifier = Modifier.padding(34.dp))
            LargeDropdownMenu(
                label = "Label",
                items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5"),
                onItemSelected = { index, _ ->
                    selectedIndex = index
                },
                selectedIndex = selectedIndex,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}