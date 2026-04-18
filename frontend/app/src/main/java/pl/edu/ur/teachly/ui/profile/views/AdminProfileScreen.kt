package pl.edu.ur.teachly.ui.profile.views

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.ui.profile.viewmodels.ProfileViewModel

@Composable
fun AdminProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    
}