package pl.edu.ur.teachly.ui.admin.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
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
import pl.edu.ur.teachly.data.model.TutorResponse
import pl.edu.ur.teachly.ui.admin.viewmodels.AdminTutorsViewModel
import pl.edu.ur.teachly.ui.components.admin.AdminScreenHeader
import pl.edu.ur.teachly.ui.components.admin.AdminSearchBar
import pl.edu.ur.teachly.ui.components.other.EmptyListState
import pl.edu.ur.teachly.ui.components.other.MessageSnackbars
import pl.edu.ur.teachly.ui.components.other.cards.TutorAdminCard
import pl.edu.ur.teachly.ui.components.other.dialog.TutorEditDialog

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

                state.filteredTutors.isEmpty() -> EmptyListState(message = "Brak korepetytorów")

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
        MessageSnackbars(
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
