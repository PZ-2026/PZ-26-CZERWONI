package pl.edu.ur.teachly.ui.admin.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.data.model.HolidayResponse
import pl.edu.ur.teachly.ui.admin.viewmodels.AdminHolidaysViewModel
import pl.edu.ur.teachly.ui.components.admin.AdminMessageSnackbars

@Composable
fun AdminHolidaysScreen(
    viewModel: AdminHolidaysViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<HolidayResponse?>(null) }
    var showDeleteDialog by remember { mutableStateOf<HolidayResponse?>(null) }

    LaunchedEffect(state.successMessage, state.error) {
        if (state.successMessage != null || state.error != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Surface(color = colorScheme.primary) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Święta i dni wolne",
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onPrimary
                )
                FilledIconButton(
                    onClick = { showAddDialog = true },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = colorScheme.onPrimary)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Dodaj",
                        tint = colorScheme.primary
                    )
                }
            }
        }

        AdminMessageSnackbars(successMessage = state.successMessage, errorMessage = state.error)

        when {
            state.isLoading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.holidays) { holiday ->
                    HolidayCard(
                        holiday = holiday,
                        onEdit = { showEditDialog = holiday },
                        onDelete = { showDeleteDialog = holiday }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        HolidayDialog(
            title = "Dodaj święto",
            initialDate = "",
            initialDescription = "",
            onDismiss = { showAddDialog = false },
            onSave = { date, desc ->
                viewModel.addHoliday(date, desc)
                showAddDialog = false
            }
        )
    }

    showEditDialog?.let { holiday ->
        HolidayDialog(
            title = "Edytuj święto",
            initialDate = holiday.holidayDate,
            initialDescription = holiday.description ?: "",
            onDismiss = { showEditDialog = null },
            onSave = { date, desc ->
                viewModel.updateHoliday(holiday.id, date, desc)
                showEditDialog = null
            }
        )
    }

    showDeleteDialog?.let { holiday ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Usuń święto") },
            text = { Text("Czy na pewno chcesz usunąć: ${holiday.holidayDate}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteHoliday(holiday.id)
                    showDeleteDialog = null
                }) { Text("Usuń", color = colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = null }) { Text("Anuluj") } }
        )
    }
}

@Composable
private fun HolidayCard(holiday: HolidayResponse, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Event,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    holiday.holidayDate,
                    style = typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                holiday.description?.let {
                    Text(
                        it,
                        style = typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edytuj",
                    tint = colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Usuń",
                    tint = colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun HolidayDialog(
    title: String,
    initialDate: String,
    initialDescription: String,
    onDismiss: () -> Unit,
    onSave: (String, String?) -> Unit
) {
    var date by remember { mutableStateOf(initialDate) }
    var description by remember { mutableStateOf(initialDescription) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Data (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { if (it.length <= 100) description = it },
                    label = { Text("Opis (opcjonalnie)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(date.trim(), description.trim().ifBlank { null }) },
                enabled = date.isNotBlank()
            ) { Text("Zapisz") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } }
    )
}
