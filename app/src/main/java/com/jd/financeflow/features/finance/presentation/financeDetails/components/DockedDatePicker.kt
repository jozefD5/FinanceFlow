package com.jd.financeflow.features.finance.presentation.financeDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.jd.financeflow.R

/**
 * A composable that displays a date picker field.
 *
 * @param modifier Modifier to be applied to the composable.
 * @param onValueChange Callback function to be invoked when the selected date changes. Allows to
 * retrieve the selected date.
 * @param label Label to be displayed above the date picker field.
 * @param placeholder Placeholder to be displayed when the date picker field is empty.
 * @param datePickerState State object that holds the state of the date picker.
 */
@Composable
fun DataPickerField(
    modifier: Modifier = Modifier,
    onValueChange: (Long?) -> Unit = {},
    label: String? = null,
    placeholder: String? = null,
    datePickerState: DatePickerState = rememberDatePickerState()
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    Box(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {
                onValueChange(datePickerState.selectedDateMillis)
            },
            label = { if (label != null) Text(label) },
            placeholder = { if (placeholder != null) Text(placeholder) },
            readOnly = true,
            shape = RoundedCornerShape(dimensionResource(R.dimen.round_corner_size_medium)),
            trailingIcon = {
                IconButton(
                    onClick = { showDatePicker = !showDatePicker }
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        if (showDatePicker) {
            Popup(
                onDismissRequest = { showDatePicker = true },
                alignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 64.dp)
                        .shadow(elevation = 4.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false,
                    )
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