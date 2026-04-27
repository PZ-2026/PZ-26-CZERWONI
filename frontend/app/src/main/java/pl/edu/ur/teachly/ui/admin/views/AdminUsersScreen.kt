package pl.edu.ur.teachly.ui.admin.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
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
import pl.edu.ur.teachly.data.model.UserResponse
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.ui.admin.viewmodels.AdminUsersViewModel
import pl.edu.ur.teachly.ui.components.admin.AdminMessageSnackbars
import pl.edu.ur.teachly.ui.components.admin.AdminScreenHeader
import pl.edu.ur.teachly.ui.components.admin.AdminSearchBar
import pl.edu.ur.teachly.ui.components.other.FilterChips
import pl.edu.ur.teachly.ui.components.other.cards.UserAdminCard
import pl.edu.ur.teachly.ui.components.other.dialog.AdminUserEditDialog

@Composable
fun AdminUsersScreen(
    viewModel: AdminUsersViewModel = koinViewModel(),
    initialRoleFilter: String? = null,
) {
    val state by viewModel.state.collectAsState()
    var showEditDialog by remember { mutableStateOf<UserResponse?>(null) }
    var showBanDialog by remember { mutableStateOf<UserResponse?>(null) }

    LaunchedEffect(initialRoleFilter) {
        if (initialRoleFilter != null) {
            val role = UserRole.entries.find { it.name == initialRoleFilter }
            viewModel.onRoleFilterChange(role)
        }
    }

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
            AdminScreenHeader(title = "Użytkownicy") {
                AdminSearchBar(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchChange(it) },
                    placeholder = "Szukaj po imieniu, nazwisku, email...",
                )
                Spacer(Modifier.height(8.dp))
                FilterChips(
                    items = listOf("Wszyscy") + UserRole.entries.map { it.name },
                    activeItem = state.selectedRole?.name ?: "Wszyscy",
                    onSelect = { label ->
                        viewModel.onRoleFilterChange(
                            if (label == "Wszyscy") null else UserRole.valueOf(label)
                        )
                    },
                )
            }

            when {
                state.isLoading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.filteredUsers) { user ->
                        UserAdminCard(
                            user = user,
                            onEdit = { showEditDialog = user },
                            onBanToggle = { showBanDialog = user }
                        )
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

    // Edit dialog
    showEditDialog?.let { user ->
        AdminUserEditDialog(
            user = user,
            onDismiss = { showEditDialog = null },
            onSave = { request ->
                viewModel.updateUser(user.id, request)
                showEditDialog = null
            }
        )
    }

    // Ban/Unban confirmation dialog
    showBanDialog?.let { user ->
        AlertDialog(
            onDismissRequest = { showBanDialog = null },
            title = { Text(if (user.isActive) "Zablokuj konto" else "Odblokuj konto") },
            text = {
                Text(
                    if (user.isActive)
                        "Czy na pewno chcesz zablokować konto użytkownika ${user.firstName} ${user.lastName}?"
                    else
                        "Czy na pewno chcesz odblokować konto użytkownika ${user.firstName} ${user.lastName}?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (user.isActive) viewModel.banUser(user.id) else viewModel.unbanUser(user.id)
                    showBanDialog = null
                }) {
                    Text(
                        if (user.isActive) "Zablokuj" else "Odblokuj",
                        color = if (user.isActive) colorScheme.error else colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showBanDialog = null }) { Text("Anuluj") }
            }
        )
    }
}
