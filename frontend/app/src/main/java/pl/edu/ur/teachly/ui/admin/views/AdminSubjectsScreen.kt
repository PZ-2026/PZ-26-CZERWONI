package pl.edu.ur.teachly.ui.admin.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.data.model.SubjectCategoryResponse
import pl.edu.ur.teachly.data.model.SubjectResponse
import pl.edu.ur.teachly.ui.admin.viewmodels.AdminSubjectsViewModel
import pl.edu.ur.teachly.ui.components.admin.AdminMessageSnackbars
import pl.edu.ur.teachly.ui.components.admin.AdminScreenHeader
import pl.edu.ur.teachly.ui.components.other.cards.CardInfoRow

@Composable
fun AdminSubjectsScreen(
    viewModel: AdminSubjectsViewModel = koinViewModel(),
    showHeader: Boolean = true,
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showEditSubjectDialog by remember { mutableStateOf<SubjectResponse?>(null) }
    var showDeleteSubjectDialog by remember { mutableStateOf<SubjectResponse?>(null) }

    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showEditCategoryDialog by remember { mutableStateOf<SubjectCategoryResponse?>(null) }
    var showDeleteCategoryDialog by remember { mutableStateOf<SubjectCategoryResponse?>(null) }

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
            val innerTabs = @Composable {
                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Przedmioty") })
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Kategorie") })
                }
            }
            if (showHeader) {
                AdminScreenHeader(title = "Dane platformy") { innerTabs() }
            } else {
                Surface(color = colorScheme.surface, shadowElevation = 2.dp) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.End,
                        ) { }
                        innerTabs()
                    }
                }
            }

            when {
                state.isLoading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                selectedTab == 0 -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.subjects) { subject ->
                        SubjectCard(
                            subject = subject,
                            onEdit = { showEditSubjectDialog = subject },
                            onDelete = { showDeleteSubjectDialog = subject }
                        )
                    }
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.categories) { category ->
                        CategoryCard(
                            category = category,
                            subjectCount = state.subjects.count { it.categoryId == category.id },
                            onEdit = { showEditCategoryDialog = category },
                            onDelete = { showDeleteCategoryDialog = category }
                        )
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = {
                if (selectedTab == 0) showAddSubjectDialog = true
                else showAddCategoryDialog = true
            },
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

    // Subject dialogs
    if (showAddSubjectDialog) {
        SubjectDialog(
            title = "Dodaj przedmiot",
            initialName = "",
            initialCategoryId = state.categories.firstOrNull()?.id ?: 0,
            categories = state.categories,
            onDismiss = { showAddSubjectDialog = false },
            onSave = { name, categoryId ->
                viewModel.addSubject(name, categoryId)
                showAddSubjectDialog = false
            }
        )
    }
    showEditSubjectDialog?.let { subject ->
        SubjectDialog(
            title = "Edytuj przedmiot",
            initialName = subject.subjectName,
            initialCategoryId = subject.categoryId,
            categories = state.categories,
            onDismiss = { showEditSubjectDialog = null },
            onSave = { name, categoryId ->
                viewModel.updateSubject(subject.id, name, categoryId)
                showEditSubjectDialog = null
            }
        )
    }
    showDeleteSubjectDialog?.let { subject ->
        AlertDialog(
            onDismissRequest = { showDeleteSubjectDialog = null },
            title = { Text("Usuń przedmiot") },
            text = { Text("Czy na pewno chcesz usunąć: ${subject.subjectName}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSubject(subject.id); showDeleteSubjectDialog = null
                }) {
                    Text("Usuń", color = colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteSubjectDialog = null
                }) { Text("Anuluj") }
            }
        )
    }

    // Category dialogs
    if (showAddCategoryDialog) {
        CategoryDialog(
            title = "Dodaj kategorię",
            initialName = "",
            onDismiss = { showAddCategoryDialog = false },
            onSave = { name -> viewModel.addCategory(name); showAddCategoryDialog = false }
        )
    }
    showEditCategoryDialog?.let { category ->
        CategoryDialog(
            title = "Edytuj kategorię",
            initialName = category.categoryName,
            onDismiss = { showEditCategoryDialog = null },
            onSave = { name ->
                viewModel.updateCategory(category.id, name); showEditCategoryDialog = null
            }
        )
    }
    showDeleteCategoryDialog?.let { category ->
        AlertDialog(
            onDismissRequest = { showDeleteCategoryDialog = null },
            title = { Text("Usuń kategorię") },
            text = { Text("Czy na pewno chcesz usunąć: ${category.categoryName}? Najpierw usuń wszystkie przypisane przedmioty.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCategory(category.id); showDeleteCategoryDialog = null
                }) {
                    Text("Usuń", color = colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteCategoryDialog = null
                }) { Text("Anuluj") }
            }
        )
    }
}

@Composable
private fun SubjectCard(subject: SubjectResponse, onEdit: () -> Unit, onDelete: () -> Unit) {
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
                    text = subject.subjectName,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                )
                Row {
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

            Spacer(Modifier.height(8.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.Category,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = "Kategoria: ${subject.categoryName}",
            )
        }
    }
}

@Composable
private fun CategoryCard(
    category: SubjectCategoryResponse,
    subjectCount: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
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
                    text = category.categoryName,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                )
                Row {
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
                            tint = if (subjectCount == 0) colorScheme.error else colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.School,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = "Przedmioty: $subjectCount",
            )
        }
    }
}

@Composable
private fun SubjectDialog(
    title: String,
    initialName: String,
    initialCategoryId: Int,
    categories: List<SubjectCategoryResponse>,
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var categoryId by remember { mutableIntStateOf(initialCategoryId) }
    var expanded by remember { mutableStateOf(false) }
    val selectedCategory = categories.find { it.id == categoryId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 100) name = it },
                    label = { Text("Nazwa przedmiotu") },
                    leadingIcon = { Icon(Icons.Default.School, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCategory?.categoryName ?: "Wybierz kategorię",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategoria") },
                        leadingIcon = { Icon(Icons.Default.Category, null) },
                        trailingIcon = {
                            Icon(
                                if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.categoryName) },
                                onClick = { categoryId = cat.id; expanded = false },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name.trim(), categoryId) },
                enabled = name.isNotBlank() && categoryId > 0,
            ) { Text("Zapisz") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } }
    )
}

@Composable
private fun CategoryDialog(
    title: String,
    initialName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 100) name = it },
                label = { Text("Nazwa") },
                leadingIcon = { Icon(Icons.Default.Edit, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name.trim()) },
                enabled = name.isNotBlank(),
            ) { Text("Zapisz") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } }
    )
}
