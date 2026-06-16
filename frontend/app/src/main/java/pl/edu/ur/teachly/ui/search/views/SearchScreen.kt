package pl.edu.ur.teachly.ui.search.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.ui.components.other.FilterChips
import pl.edu.ur.teachly.ui.components.other.FullScreenError
import pl.edu.ur.teachly.ui.components.other.LoadingBox
import pl.edu.ur.teachly.ui.components.search.SearchHeader
import pl.edu.ur.teachly.ui.components.search.TutorList
import pl.edu.ur.teachly.ui.models.Tutor
import pl.edu.ur.teachly.ui.search.viewmodels.SearchViewModel

@Composable
fun SearchScreen(onTutorClick: (Tutor) -> Unit = {}, viewModel: SearchViewModel = koinViewModel()) {
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
            onSearch = { focusManager.clearFocus() }
        )
        FilterChips(
            items = uiState.subjects,
            activeItem = uiState.activeSubject,
            onSelect = viewModel::onSubjectSelect
        )
        OutlinedTextField(
            value = uiState.city,
            onValueChange = viewModel::onCityChange,
            label = { Text("Miasto (zajęcia stacjonarne)") },
            placeholder = { Text("np. Kraków") },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )

        when {
            uiState.isLoading -> LoadingBox()

            uiState.error != null -> FullScreenError(message = uiState.error!!)

            else -> {
                TutorList(tutors = uiState.tutors, onTutorClick = onTutorClick)
            }
        }
    }
}
