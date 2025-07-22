package com.snowi.snuzz.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@Composable
fun MessageCard(
    message: String,
    onDismiss: () -> Unit,
    type: MessageType = MessageType.Error,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (type) {
        MessageType.Error -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        MessageType.Info -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        MessageType.Success -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.medium)
            .background(bgColor, shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = message,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = textColor)
            }
        }
    }
}
