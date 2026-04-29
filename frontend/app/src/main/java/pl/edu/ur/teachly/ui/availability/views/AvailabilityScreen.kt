package pl.edu.ur.teachly.ui.availability.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.ui.availability.viewmodels.AvailabilityViewModel
import pl.edu.ur.teachly.ui.components.availability.OverridesTab
import pl.edu.ur.teachly.ui.components.availability.WeeklyTab
import pl.edu.ur.teachly.ui.components.other.AppHeader
import pl.edu.ur.teachly.ui.components.other.FullScreenError
import pl.edu.ur.teachly.ui.components.other.HeaderBackground
import pl.edu.ur.teachly.ui.components.other.MessageSnackbars
import pl.edu.ur.teachly.ui.components.other.dialog.AvailabilityTimeRangeDialog
import pl.edu.ur.teachly.ui.components.other.dialog.ConfirmDeleteDialog
import pl.edu.ur.teachly.ui.components.other.dialog.OverrideDateDialog
import pl.edu.ur.teachly.ui.models.DAY_NAMES

@Composable
fun AvailabilityScreen(
    tutorId: Int,
    onBack: () -> Unit,
    viewModel: AvailabilityViewModel = koinViewModel(),
) {
    LaunchedEffect(tutorId) { viewModel.load(tutorId) }

    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddSlotFor by remember { mutableStateOf<Int?>(null) }  // dayOfWeek
    var showAddOverride by remember { mutableStateOf(false) }
    var confirmDeleteSlot by remember { mutableStateOf<Int?>(null) }     // slotId
    var confirmDeleteOverride by remember { mutableStateOf<Int?>(null) } // overrideId

    LaunchedEffect(state.successMessage, state.error) {
        if (state.successMessage != null || state.error != null) {
            delay(2000)
            viewModel.clearMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppHeader(
                title = "Harmonogram dostępności",
                subtitle = "Dostosuj swoje godziny pracy",
                background = HeaderBackground.Diagonal(
                    listOf(colorScheme.onPrimaryContainer, colorScheme.primary)
                ),
                onBack = onBack,
            )

            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Tygodniowy") },
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Niedostępność") },
                )
            }

            when {
                state.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                state.error != null && state.recurring.isEmpty() && state.overrides.isEmpty() ->
                    FullScreenError(message = state.error!!)

                selectedTab == 0 -> WeeklyTab(
                    recurring = state.recurring,
                    onAddSlot = { day -> showAddSlotFor = day },
                    onDeleteSlot = { slotId -> confirmDeleteSlot = slotId },
                )

                else -> OverridesTab(
                    overrides = state.overrides,
                    onDeleteOverride = { id -> confirmDeleteOverride = id },
                )
            }
        }

        if (selectedTab == 1 && !state.isLoading) {
            FloatingActionButton(
                onClick = { showAddOverride = true },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp),
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                shape = CircleShape,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj niedostępność")
            }
        }

        MessageSnackbars(
            successMessage = state.successMessage,
            errorMessage = if (state.recurring.isNotEmpty() || state.overrides.isNotEmpty()) state.error else null,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    showAddSlotFor?.let { day ->
        val existingSlots = state.recurring.filter { it.dayOfWeek == day }
        AvailabilityTimeRangeDialog(
            dayName = DAY_NAMES[day] ?: "",
            existingSlots = existingSlots,
            onDismiss = { showAddSlotFor = null },
            onSave = { timeFrom, timeTo ->
                viewModel.addSlot(tutorId, day, timeFrom, timeTo)
                showAddSlotFor = null
            },
        )
    }

    if (showAddOverride) {
        OverrideDateDialog(
            onDismiss = { showAddOverride = false },
            onSave = { date, timeFrom, timeTo ->
                viewModel.addOverride(tutorId, date, timeFrom, timeTo)
                showAddOverride = false
            },
        )
    }

    confirmDeleteSlot?.let { slotId ->
        ConfirmDeleteDialog(
            message = "Czy na pewno chcesz usunąć ten slot dostępności?",
            onDismiss = { confirmDeleteSlot = null },
            onConfirm = {
                viewModel.deleteSlot(tutorId, slotId)
                confirmDeleteSlot = null
            },
        )
    }

    confirmDeleteOverride?.let { overrideId ->
        ConfirmDeleteDialog(
            message = "Czy na pewno chcesz usunąć tę niedostępność?",
            onDismiss = { confirmDeleteOverride = null },
            onConfirm = {
                viewModel.deleteOverride(tutorId, overrideId)
                confirmDeleteOverride = null
            },
        )
    }
}
