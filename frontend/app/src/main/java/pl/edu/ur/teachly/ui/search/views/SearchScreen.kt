package pl.edu.ur.teachly.ui.search.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.ui.components.search.SearchHeader
import pl.edu.ur.teachly.ui.components.search.SubjectChips
import pl.edu.ur.teachly.ui.components.search.TutorList
import pl.edu.ur.teachly.ui.models.Tutor
import pl.edu.ur.teachly.ui.search.viewmodels.SearchViewModel

@Composable
fun SearchScreen(
    onTutorClick: (Tutor) -> Unit = {},
    viewModel: SearchViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        SearchHeader(
            query = uiState.query,
            onQueryChange = viewModel::onQueryChange,
            onClear = viewModel::clearQuery,
            onSearch = { focusManager.clearFocus() },
        )
        SubjectChips(
            subjects = uiState.subjects,
            activeSubject = uiState.activeSubject,
            onSelect = viewModel::onSubjectSelect,
        )

        when {
            uiState.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            uiState.error != null -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = uiState.error!!,
                    style = typography.bodyMedium,
                    color = colorScheme.error,
                    modifier = Modifier.padding(24.dp),
                )
            }

            else -> {
                TutorList(tutors = uiState.tutors, onTutorClick = onTutorClick)
            }
        }
    }
}