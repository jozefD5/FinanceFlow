package com.jd.financeflow.features.finance.presentation.financeDetails.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

import com.jd.financeflow.ui.theme.FinanceFlowTheme

/**
 * A composable that displays a date picker field.
 *
 * @param modifier Modifier to be applied to the composable.
 * @param onValueChange Callback function to be invoked when the selected date changes. Allows to
 * retrieve the selected date.
 * @param label Label to be displayed above the date picker field.
 * @param placeholder Placeholder to be displayed when the date picker field is empty.
 * @param shape Shape of the date picker field.
 * @param colors Colors to be used for the date picker field.
 * @param datePickerState State object that holds the state of the date picker.
 */
@Composable
fun DataPickerField(
    modifier: Modifier = Modifier,
    onValueChange: (Long?) -> Unit = {},
    label: String? = null,
    placeholder: String? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    datePickerState: DatePickerState = rememberDatePickerState()
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {
                onValueChange(datePickerState.selectedDateMillis)
            },
            label = { if (label != null) Text(label) },
            placeholder = { if (placeholder != null) Text(placeholder) },
            readOnly = true,
            shape = shape,
            colors = colors,
            trailingIcon = {
                IconButton(
                    onClick = { showDatePicker = !showDatePicker }
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable { showDatePicker = true },
        ) { }

        if (showDatePicker) {
            Dialog(
                onDismissRequest = { showDatePicker = false },
            ) {
                FinanceFlowTheme {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        DatePicker(
                            state = datePickerState,
                            showModeToggle = true,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DataPickerFieldPreview() {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    MaterialTheme {
        DataPickerField(
            label = "Date",
            placeholder = "Select date",
            datePickerState = datePickerState
        )
    }
}