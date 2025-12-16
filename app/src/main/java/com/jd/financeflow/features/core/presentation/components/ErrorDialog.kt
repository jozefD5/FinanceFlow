package com.jd.financeflow.features.core.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

import com.jd.financeflow.R

/**
 * Displays an error dialog with the given content.
 *
 * @param content the content of the error dialog
 * @param modifier the modifier to apply to the error dialog
 * @param onConfirmation the action to perform when the user confirms the error dialog
 * @param onDismiss the action to perform when the user dismisses the error dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorDialog(
    content: String,
    modifier: Modifier = Modifier,
    onConfirmation: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null
            )
        },
        title = { Text(stringResource(R.string.error_title)) },
        text = { Text(content) },
        onDismissRequest = onDismiss,
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.dismiss_title))
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(R.string.confirm_title))
            }
        },
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun ErrorDialogPreview() {
    MaterialTheme {
        ErrorDialog(
            content = "This is an error",
            onConfirmation = {},
            onDismiss = {}
        )
    }
}