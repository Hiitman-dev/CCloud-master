package com.pira.ccloud.components.dialogs

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.pira.ccloud.ui.theme.GlassAlertDialog

/**
 * Reusable confirmation dialog for destructive actions.
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    GlassAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}

/**
 * Reusable dialog for displaying information.
 */
@Composable
fun InfoDialog(
    title: String,
    message: String,
    buttonText: String = "OK",
    onDismiss: () -> Unit
) {
    GlassAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(buttonText)
            }
        }
    )
}

/**
 * Reusable dialog for displaying error messages with retry option.
 */
@Composable
fun ErrorDialog(
    title: String = "Error",
    message: String,
    retryText: String = "Retry",
    dismissText: String = "Cancel",
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    GlassAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = {
                onRetry()
                onDismiss()
            }) {
                Text(retryText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}
