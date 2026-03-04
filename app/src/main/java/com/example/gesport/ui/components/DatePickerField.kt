package com.example.gesport.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: String,
    onDateSelected: (String) -> Unit,
    placeholder: String,
    leadingIconRes: Int,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    val formatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }

    // Parse value "yyyy-MM-dd" -> millis (si no, null)
    val initialMillis = remember(value) {
        runCatching {
            val date = LocalDate.parse(value, formatter)
            date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }.getOrNull()
    }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier.fillMaxWidth()) {

        OutlinedTextField(
            value = value,
            onValueChange = { /* readOnly */ },
            readOnly = true,
            enabled = false,
            placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.65f)) },
            leadingIcon = {
                Image(
                    painter = painterResource(leadingIconRes),
                    contentDescription = null,
                    modifier = Modifier.heightIn(max = 19.dp)
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp),
            shape = RoundedCornerShape(4.dp),
            colors = TextFieldDefaults.colors(
                disabledContainerColor = Color.White.copy(alpha = 0.28f),
                disabledIndicatorColor = Color.Transparent,
                disabledTextColor = Color.White,
                disabledLeadingIconColor = Color.Unspecified,
                disabledPlaceholderColor = Color.White.copy(alpha = 0.65f),
                selectionColors = TextSelectionColors(
                    handleColor = Color(0xFF2DAAE1).copy(alpha = 0.55f),
                    backgroundColor = Color(0xFF2DAAE1).copy(alpha = 0.4f)
                )
            )
        )

        // Overlay clickable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { showDialog = true }
        )
    }

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val picked = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .format(formatter)
                            onDateSelected(picked)
                        }
                        showDialog = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}