package pl.edu.ur.teachly.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.edu.ur.teachly.ui.components.HomeHeader
import pl.edu.ur.teachly.ui.components.StatsRow
import pl.edu.ur.teachly.ui.components.SubjectChips
import pl.edu.ur.teachly.ui.components.Tutor
import pl.edu.ur.teachly.ui.components.TutorList

@Composable
fun HomeScreen(
    onTutorClick: (Tutor) -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        HomeHeader(
            query = uiState.query,
            onQueryChange = viewModel::onQueryChange,
            onClear = viewModel::clearQuery,
            onSearch = { focusManager.clearFocus() })
        SubjectChips(activeSubject = uiState.activeSubject, onSelect = viewModel::onSubjectSelect)
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        StatsRow()
        TutorList(tutors = uiState.tutors, onTutorClick = onTutorClick)
    }
}
