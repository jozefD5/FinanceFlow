package com.jd.financeflow.features.financeDetails.presentation.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

import com.jd.financeflow.R


/**
 * A read-only text field displaying a formatted date that opens a [DatePickerDialog] when pressed
 * or when its trailing calendar icon is clicked.
 *
 * @param value Value (date in epoch milliseconds) to display, or null for no date
 * @param onValueChange Callback invoked with the selected date in epoch milliseconds when the
 * dialog is confirmed; null if no date was selected.
 * @param modifier Modifier to be applied to the composable.
 * @param label Label to be displayed above the date picker field.
 * @param placeholder Placeholder to be displayed when the date picker field is empty.
 * @param shape Shape of the date picker field.
 * @param colors Colors to be used for the date picker field.
 */
@Composable
fun DataPickerField(
    value: Long?,
    onValueChange: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    var showDatePicker by remember { mutableStateOf(false) }

    // Internal state synchronized with the external value
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = value)

    // Format the timestamp for display
    val formattedDate = remember(value) {
        value?.let { convertMillisToDate(it) } ?: ""
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = formattedDate,
            onValueChange = {},
            label = { label?.let { Text(it) } },
            placeholder = { placeholder?.let { Text(it) } },
            readOnly = true,
            shape = shape,
            colors = colors,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select date")
                }
            },
            // Use interactionSource to capture clicks cleanly without an overlay hack
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect { interaction ->
                            if (interaction is PressInteraction.Release) {
                                showDatePicker = true
                            }
                        }
                    }
                },
            modifier = Modifier.fillMaxWidth()
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        onValueChange(datePickerState.selectedDateMillis)
                        showDatePicker = false
                    }) {
                        Text(stringResource(R.string.save_title))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(stringResource(R.string.cancel_title))
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = true
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DataPickerFieldPreview() {
    MaterialTheme {
        DataPickerField(
            value = System.currentTimeMillis(),
            label = "Date",
            onValueChange = {},
            placeholder = "Select date",
        )
    }
}