package pl.edu.ur.teachly.ui.admin.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import pl.edu.ur.teachly.data.model.TutorRequest
import pl.edu.ur.teachly.data.model.TutorResponse
import pl.edu.ur.teachly.ui.admin.viewmodels.AdminTutorsViewModel
import pl.edu.ur.teachly.ui.components.admin.AdminMessageSnackbars
import pl.edu.ur.teachly.ui.components.admin.AdminScreenHeader
import pl.edu.ur.teachly.ui.components.admin.AdminSearchBar
import pl.edu.ur.teachly.ui.components.other.cards.CardInfoRow
import pl.edu.ur.teachly.ui.components.other.dialog.DialogSectionLabel
import pl.edu.ur.teachly.ui.components.other.dialog.DialogSwitchRow

@Composable
fun AdminTutorsScreen(
    viewModel: AdminTutorsViewModel = koinViewModel(),
    showHeader: Boolean = true,
) {
    val state by viewModel.state.collectAsState()
    var showEditDialog by remember { mutableStateOf<TutorResponse?>(null) }

    LaunchedEffect(state.successMessage, state.error) {
        if (state.successMessage != null || state.error != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (showHeader) {
                AdminScreenHeader(title = "Korepetytorzy") {
                    AdminSearchBar(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onSearchChange(it) },
                        placeholder = "Szukaj po imieniu, nazwisku, email...",
                    )
                }
            } else {
                Surface(color = colorScheme.surface, shadowElevation = 2.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        AdminSearchBar(
                            value = state.searchQuery,
                            onValueChange = { viewModel.onSearchChange(it) },
                            placeholder = "Szukaj po imieniu, nazwisku, email...",
                        )
                    }
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
                    items(state.filteredTutors) { tutor ->
                        TutorAdminCard(tutor = tutor, onEdit = { showEditDialog = tutor })
                    }
                }
            }
        }
        AdminMessageSnackbars(
            successMessage = state.successMessage,
            errorMessage = state.error,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    showEditDialog?.let { tutor ->
        TutorEditDialog(
            tutor = tutor,
            onDismiss = { showEditDialog = null },
            onSave = { request ->
                viewModel.updateTutor(tutor.id, request)
                showEditDialog = null
            }
        )
    }
}

@Composable
private fun TutorAdminCard(tutor: TutorResponse, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${tutor.firstName} ${tutor.lastName}",
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                )
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edytuj",
                        tint = colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.Email,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = tutor.email,
            )

            Spacer(Modifier.height(6.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.Payments,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = "Stawka: ${tutor.hourlyRate} PLN/h",
            )

            tutor.bio?.let {
                Spacer(Modifier.height(6.dp))
                CardInfoRow(
                    icon = {
                        Icon(
                            Icons.Default.Info,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = colorScheme.primary
                        )
                    },
                    text = it.take(80) + if (it.length > 80) "..." else "",
                )
            }

            if (tutor.offersOnline || tutor.offersInPerson) {
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (tutor.offersOnline) Surface(
                        color = colorScheme.primaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "Online",
                            style = typography.labelSmall,
                            color = colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (tutor.offersInPerson) Surface(
                        color = colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "Stacjonarnie",
                            style = typography.labelSmall,
                            color = colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TutorEditDialog(
    tutor: TutorResponse,
    onDismiss: () -> Unit,
    onSave: (TutorRequest) -> Unit,
) {
    var bio by remember { mutableStateOf(tutor.bio ?: "") }
    var hourlyRate by remember { mutableStateOf(tutor.hourlyRate.toString()) }
    var offersOnline by remember { mutableStateOf(tutor.offersOnline) }
    var offersInPerson by remember { mutableStateOf(tutor.offersInPerson) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edytuj korepetytora") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = hourlyRate,
                    onValueChange = { hourlyRate = it },
                    label = { Text("Stawka godzinowa (PLN)") },
                    leadingIcon = { Icon(Icons.Default.Payments, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    leadingIcon = { Icon(Icons.Default.Info, null) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )
                DialogSectionLabel("Forma zajęć")
                DialogSwitchRow("Zajęcia online", offersOnline) { offersOnline = it }
                DialogSwitchRow("Zajęcia stacjonarne", offersInPerson) { offersInPerson = it }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        TutorRequest(
                            bio = bio.ifBlank { null },
                            hourlyRate = hourlyRate.toDoubleOrNull() ?: tutor.hourlyRate,
                            offersOnline = offersOnline,
                            offersInPerson = offersInPerson,
                        )
                    )
                },
                enabled = hourlyRate.toDoubleOrNull() != null,
            ) { Text("Zapisz") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } }
    )
}
