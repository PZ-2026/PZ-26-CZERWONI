package pl.edu.ur.teachly.ui.schedule.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import pl.edu.ur.teachly.ui.components.MOCK_SCHEDULE
import pl.edu.ur.teachly.ui.components.ScheduledClass

class ScheduleViewModel : ViewModel() {

    private val allClasses = MOCK_SCHEDULE.values.flatten()

    // TODO: adapt for DB
    val upcomingClasses: List<ScheduledClass> = allClasses.filter { it.status != "Zakończone" }
    val finishedClasses: List<ScheduledClass> = allClasses.filter { it.status == "Zakończone" }

    var expanded by mutableStateOf(false)
        private set

    fun toggleExpanded() {
        expanded = !expanded
    }
}