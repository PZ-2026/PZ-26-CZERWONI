package pl.edu.ur.teachly.ui.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pl.edu.ur.teachly.ui.navigation.AppRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen(navController: NavHostController) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Okno Debug",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))

            DebugSection("Auth") {
                DebugButton("Splash") { navController.navigate(AppRoute.Splash) }
                DebugButton("Login") { navController.navigate(AppRoute.Login) }
                DebugButton("Register") { navController.navigate(AppRoute.Register) }
            }

            DebugSection("Home") {
                DebugButton("Home") { navController.navigate(AppRoute.Home) }
                DebugButton("Tutor Detail (id=1)") {
                    navController.navigate(
                        AppRoute.TutorDetail(
                            tutorId = 1
                        )
                    )
                }
                DebugButton("Booking (id=1)") { navController.navigate(AppRoute.Booking(tutorId = "1")) }
                DebugButton("Booking Confirm") {
                    navController.navigate(
                        AppRoute.BookingConfirm(
                            tutorId = "1",
                            bookingId = "debug-booking-id",
                            scheduledAt = "2025-06-01T10:00:00",
                        )
                    )
                }
            }

            DebugSection("Profil ucznia") {
                DebugButton("Student Profile") { navController.navigate(AppRoute.Profile) }
                DebugButton("Profile Edit") { navController.navigate(AppRoute.ProfileEdit) }
            }

            DebugSection("Profil korepetytora") {
                DebugButton("Tutor Own Profile (id=1)") {
                    navController.navigate(AppRoute.TutorProfile(tutorId = 1))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DebugSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        content()
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun DebugButton(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label)
    }
}