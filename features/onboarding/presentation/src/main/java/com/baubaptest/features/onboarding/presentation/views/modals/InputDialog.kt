package com.baubaptest.features.onboarding.presentation.views.modals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun InputDialog(
    title: String,
    description: String,
    inputLabel: String,
    inputValue: String,
    onValueChange: (String) -> Unit,
    confirmText: String = "Aceptar",
    dismissText: String = "Cancelar",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    inputKeyboard: KeyboardOptions = KeyboardOptions.Default
) {

    val isError = inputValue.length != 6 || inputValue.any { !it.isDigit() }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = inputValue.length == 6
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        },
        title = { Text(title) },
        text = {
            Column {
                Text(description)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = {
                        if (it.length <= 6) {
                            onValueChange(it)
                        }
                    },
                    label = { Text(inputLabel) },
                    keyboardOptions = inputKeyboard,
                    isError = isError,
                    visualTransformation = PasswordVisualTransformation()
                )
                if (isError && inputValue.isNotEmpty()) {
                    Text(
                        text = "El NIP debe contener exactamente 6 dígitos",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    )
}
