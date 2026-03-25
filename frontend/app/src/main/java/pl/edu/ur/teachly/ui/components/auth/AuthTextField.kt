package pl.edu.ur.teachly.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp

@Composable
fun AuthTextField(
    value           : String,
    onValueChange   : (String) -> Unit,
    label           : String,
    placeholder     : String,
    leadingIcon     : ImageVector? = null,
    keyboardOptions : KeyboardOptions = KeyboardOptions.Default,
    keyboardActions : KeyboardActions = KeyboardActions.Default,
    isError         : Boolean = false,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text  = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            modifier      = Modifier.fillMaxWidth(),
            placeholder   = {
                Text(
                    text  = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                )
            },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector        = it,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier           = Modifier.size(20.dp),
                    )
                }
            },
            isError         = isError,
            singleLine      = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape           = RoundedCornerShape(14.dp),
            colors          = authTextFieldColors(),
        )
        Spacer(Modifier.height(16.dp))
    }
}