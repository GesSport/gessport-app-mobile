package com.example.gesport.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectField(
    modifier: Modifier = Modifier,
    value: String,
    onSelected: (String) -> Unit,
    placeholder: String,
    options: List<String>,
    optionLabel: (String) -> String = { it },


    leadingIconRes: Int? = null,
    leadingIconVector: ImageVector? = null
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            readOnly = true,
            placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.65f)) },
            leadingIcon = {
                when {
                    leadingIconVector != null -> {
                        Icon(
                            imageVector = leadingIconVector,
                            contentDescription = null,
                            modifier = Modifier.heightIn(max = 19.dp),
                            tint = Color.White.copy(alpha = 0.90f)
                        )
                    }
                    leadingIconRes != null -> {
                        Image(
                            painter = painterResource(leadingIconRes),
                            contentDescription = null,
                            modifier = Modifier.heightIn(max = 19.dp)
                        )
                    }
                }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            singleLine = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .heightIn(min = 50.dp),
            shape = RoundedCornerShape(4.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.28f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.28f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                selectionColors = TextSelectionColors(
                    handleColor = Color(0xFF2DAAE1).copy(alpha = 0.55f),
                    backgroundColor = Color(0xFF2DAAE1).copy(alpha = 0.4f)
                )
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(optionLabel(opt)) },
                    onClick = {
                        onSelected(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}