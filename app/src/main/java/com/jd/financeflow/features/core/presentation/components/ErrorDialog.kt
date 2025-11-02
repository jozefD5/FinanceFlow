package com.jd.financeflow.features.core.presentation.components

//import androidx.compose.runtime.Composable
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.outlined.ErrorOutline
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.stringResource
//
//import com.jd.financeflow.R
//
///**
// * Error dialog composable.
// *
// * @param dialogTitle Title of the error dialog.
// * @param dialogText Text of the error dialog.
// * @param onDismissRequest Callback to be invoked when the dialog is dismissed.
// * @param onConfirmation Callback to be invoked when the confirmation button is clicked.
// * @param modifier Modifier to be applied to the dialog.
// */
//@Composable
//fun ErrorDialog(
//    dialogTitle: String,
//    dialogText: String,
//    onDismissRequest: () -> Unit,
//    onConfirmation: () -> Unit,
//    modifier: Modifier = Modifier,
//    icon: ImageVector = Icons.Outlined.ErrorOutline,
//) {
//    AlertDialog(
//        icon = { Icon(icon, contentDescription = null) },
//        title = { Text(text = dialogTitle) },
//        text = { Text(text = dialogText) },
//        onDismissRequest = onDismissRequest,
//        confirmButton = {
//            Button(
//                onClick = {
//                    onConfirmation()
//                }
//            ) {
//                Text(stringResource(R.string.error_dialog_button_confirm))
//            }
//        },
//        containerColor = MaterialTheme.colorScheme.errorContainer,
//        modifier = modifier,
//    )
//}
//
//@Preview
//@Composable
//fun ErrorDialogPreview() {
//    ErrorDialog(
//        dialogTitle = "Something went wrong",
//        dialogText = "Something went wrong",
//        onDismissRequest = {},
//        onConfirmation = {}
//    )
//}