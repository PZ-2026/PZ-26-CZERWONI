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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import pl.edu.ur.teachly.data.model.AdminUserUpdateRequest
import pl.edu.ur.teachly.data.model.UserResponse
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.ui.admin.viewmodels.AdminUsersViewModel
import pl.edu.ur.teachly.ui.components.admin.AdminMessageSnackbars
import pl.edu.ur.teachly.ui.components.admin.AdminSearchBar

@Composable
fun AdminUsersScreen(
    viewModel: AdminUsersViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showEditDialog by remember { mutableStateOf<UserResponse?>(null) }
    var showBanDialog by remember { mutableStateOf<UserResponse?>(null) }

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
        // Header
        Surface(color = colorScheme.primary) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    "Użytkownicy",
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onPrimary
                )
                Spacer(Modifier.height(8.dp))
                AdminSearchBar(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchChange(it) },
                    placeholder = "Szukaj po imieniu, nazwisku, email...",
                )
                Spacer(Modifier.height(8.dp))
                // Role filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    listOf(null, UserRole.STUDENT, UserRole.TUTOR, UserRole.ADMIN).forEach { role ->
                        FilterChip(
                            selected = state.selectedRole == role,
                            onClick = { viewModel.onRoleFilterChange(role) },
                            label = { Text(role?.name ?: "Wszyscy") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colorScheme.secondary,
                                selectedLabelColor = colorScheme.onSecondary,
                                containerColor = colorScheme.surfaceVariant,
                                labelColor = colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }
        }

        AdminMessageSnackbars(successMessage = state.successMessage, errorMessage = state.error)

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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

@Composable
private fun UserAdminCard(
    user: UserResponse,
    onEdit: () -> Unit,
    onBanToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.isActive) colorScheme.surface else colorScheme.errorContainer.copy(
                alpha = 0.3f
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "${user.firstName} ${user.lastName}",
                        style = typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    RoleBadge(user.role)
                    if (!user.isActive) {
                        Surface(color = colorScheme.error, shape = RoundedCornerShape(4.dp)) {
                            Text(
                                "ZABLOKOWANY",
                                style = typography.labelSmall,
                                color = colorScheme.onError,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(user.email, style = typography.bodySmall, color = colorScheme.onSurfaceVariant)
                user.phoneNumber?.let {
                    Text(
                        it,
                        style = typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edytuj",
                        tint = colorScheme.primary
                    )
                }
                IconButton(onClick = onBanToggle) {
                    Icon(
                        if (user.isActive) Icons.Default.Block else Icons.Default.LockOpen,
                        contentDescription = if (user.isActive) "Zablokuj" else "Odblokuj",
                        tint = if (user.isActive) colorScheme.error else colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleBadge(role: UserRole?) {
    val (label, color) = when (role) {
        UserRole.ADMIN -> "ADMIN" to colorScheme.error
        UserRole.TUTOR -> "TUTOR" to colorScheme.tertiary
        else -> "STUDENT" to colorScheme.primary
    }
    Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
        Text(
            label,
            style = typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun AdminUserEditDialog(
    user: UserResponse,
    onDismiss: () -> Unit,
    onSave: (AdminUserUpdateRequest) -> Unit
) {
    var firstName by remember { mutableStateOf(user.firstName) }
    var lastName by remember { mutableStateOf(user.lastName) }
    var email by remember { mutableStateOf(user.email) }
    var phone by remember { mutableStateOf(user.phoneNumber ?: "") }
    var role by remember { mutableStateOf(user.role ?: UserRole.STUDENT) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edytuj użytkownika") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Imię") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Nazwisko") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { if (it.length <= 10) phone = it },
                    label = { Text("Telefon") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Text("Rola", style = typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    UserRole.entries.forEach { r ->
                        FilterChip(
                            selected = role == r,
                            onClick = { role = r },
                            label = { Text(r.name) })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        AdminUserUpdateRequest(
                            firstName.trim(),
                            lastName.trim(),
                            email.trim(),
                            phone.trim(),
                            role,
                            null
                        )
                    )
                },
                enabled = firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && phone.length == 10
            ) { Text("Zapisz") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } }
    )
}
