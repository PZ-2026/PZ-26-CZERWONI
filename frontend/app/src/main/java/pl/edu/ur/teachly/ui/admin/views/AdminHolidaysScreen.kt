package pl.edu.ur.teachly.ui.admin.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.data.model.HolidayResponse
import pl.edu.ur.teachly.ui.admin.viewmodels.AdminHolidaysViewModel
import pl.edu.ur.teachly.ui.components.admin.AdminMessageSnackbars
import pl.edu.ur.teachly.ui.components.admin.AdminScreenHeader
import pl.edu.ur.teachly.ui.components.other.cards.HolidayCard
import pl.edu.ur.teachly.ui.components.other.dialog.HolidayDialog

@Composable
fun AdminHolidaysScreen(
    viewModel: AdminHolidaysViewModel = koinViewModel(),
    showHeader: Boolean = true,
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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            if (showHeader) {
                AdminScreenHeader(title = "Święta i dni wolne")
            } else {
                Surface(color = colorScheme.surface, shadowElevation = 2.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End,
                    ) { }
                }
            }

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
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary,
            shape = CircleShape,
        ) {
            Icon(Icons.Default.Add, contentDescription = "Dodaj")
        }
        AdminMessageSnackbars(
            successMessage = state.successMessage,
            errorMessage = state.error,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
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
