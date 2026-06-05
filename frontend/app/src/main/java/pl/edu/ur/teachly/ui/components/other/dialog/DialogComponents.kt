package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.other.authTextFieldColors

val DialogFieldSpacing = 12.dp
val DialogFieldShape = RoundedCornerShape(14.dp)

@Composable
fun AppFormDialog(
    title: String,
    subtitle: String? = null,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmText: String = "Zapisz",
    confirmEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .widthIn(max = 520.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.45f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text(
                    text = title,
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                if (subtitle != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 460.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(DialogFieldSpacing)
                ) {
                    content()
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.35f))
                Spacer(Modifier.height(14.dp))

                PrimaryButton(
                    text = confirmText,
                    onClick = onConfirm,
                    enabled = confirmEnabled
                )
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Anuluj", color = colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun DialogSectionCard(
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (title != null) {
            DialogSectionLabel(title)
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colorScheme.surfaceVariant.copy(alpha = 0.35f),
            border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.25f))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun DialogSectionLabel(text: String) {
    Text(
        text = text,
        style = typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = colorScheme.primary
    )
}

@Composable
fun DialogErrorText(message: String) {
    Text(
        text = message,
        style = typography.bodySmall,
        color = colorScheme.error
    )
}

@Composable
fun <T> DialogChipRow(entries: List<T>, selected: T, onSelect: (T) -> Unit, label: (T) -> String) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        entries.forEach { entry ->
            FilterChip(
                selected = selected == entry,
                onClick = { onSelect(entry) },
                label = { Text(label(entry), style = typography.labelMedium) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colorScheme.primaryContainer,
                    selectedLabelColor = colorScheme.onPrimaryContainer,
                    containerColor = colorScheme.surface,
                    labelColor = colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected == entry,
                    borderColor = colorScheme.outline.copy(alpha = 0.5f),
                    selectedBorderColor = colorScheme.primary.copy(alpha = 0.4f)
                )
            )
        }
    }
}

@Composable
fun DialogPickerField(
    value: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: ImageVector? = null,
    trailingIconDescription: String? = null,
    isError: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null
) {
    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon?.let { icon ->
                { Icon(icon, contentDescription = trailingIconDescription) }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = DialogFieldShape,
            colors = authTextFieldColors(),
            isError = isError,
            supportingText = supportingText
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
        )
    }
}

@Composable
fun AppConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    destructive: Boolean = false
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .widthIn(max = 420.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.45f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text(
                    text = title,
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = message,
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(18.dp))
                HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.35f))
                Spacer(Modifier.height(14.dp))
                if (destructive) {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.error,
                            contentColor = colorScheme.onError
                        )
                    ) {
                        Text(confirmText)
                    }
                } else {
                    PrimaryButton(text = confirmText, onClick = onConfirm)
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Anuluj", color = colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun AppContentDialog(
    title: String,
    subtitle: String? = null,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmText: String = "OK",
    confirmEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .widthIn(max = 520.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.45f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text(
                    text = title,
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                if (subtitle != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(16.dp))
                content()
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.35f))
                Spacer(Modifier.height(14.dp))
                PrimaryButton(text = confirmText, onClick = onConfirm, enabled = confirmEnabled)
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Anuluj", color = colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun DialogSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = typography.bodyMedium, color = colorScheme.onSurface)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorScheme.onPrimary,
                checkedTrackColor = colorScheme.primary,
                uncheckedThumbColor = colorScheme.outline,
                uncheckedTrackColor = colorScheme.surfaceVariant
            )
        )
    }
}
