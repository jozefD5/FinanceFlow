package com.jd.financeflow.features.finance.presentation.addTransaction.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jd.financeflow.ui.theme.FinanceFlowTheme

@Composable
fun TextFieldDropDownMenu(
    selectedIndex: Int,
    options: List<String>,
    onOptionSelected: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    // Declaring a boolean value to store the expanded state of the Text Field
    var expanded by remember { mutableStateOf(false) }

    // Up Icon when expanded and down icon when collapsed
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(modifier = modifier) {
        OutlinedTextField(
            value = options[selectedIndex],
            onValueChange = {
                onOptionSelected(options.indexOf(it))
            },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = !expanded }

                )
            },
            modifier = Modifier
                .fillMaxWidth()
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.fillMaxWidth()
    ) {
        options.forEach { value ->
            DropdownMenuItem(
                text = { Text(text = value) },
                onClick = {
                    onOptionSelected(options.indexOf(value))
                    expanded = false
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TextFieldDropDownMenuPreview() {
    FinanceFlowTheme{
        Column {
            Spacer(modifier = Modifier.padding(34.dp))
            TextFieldDropDownMenu(
                selectedIndex = 0,
                options = listOf("Option 1", "Option 2", "Option 3"),
                onOptionSelected = {},
                label = "Label"
            )
        }
    }
}