package pl.edu.ur.teachly.ui.admin.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.navigation.AppRoute
import pl.edu.ur.teachly.ui.admin.viewmodels.AdminDashboardViewModel
import pl.edu.ur.teachly.ui.components.admin.AdminScreenHeader
import pl.edu.ur.teachly.ui.components.other.cards.DashboardCard

@Composable
fun AdminDashboardScreen(
    viewModel: AdminDashboardViewModel = koinViewModel(),
    onNavigate: (AppRoute) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        // Header
        AdminScreenHeader(
            title = "Panel Administratora",
            icon = Icons.Default.Shield,
            modifier = Modifier.fillMaxWidth(),
        )

        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.error != null -> Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(state.error ?: "", color = colorScheme.error)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { viewModel.loadStats() }) { Text("Spróbuj ponownie") }
            }

            state.stats != null -> {
                val stats = state.stats!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Użytkownicy",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.heightIn(max = 300.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            DashboardCard(
                                "Wszyscy",
                                stats.totalUsers.toString(),
                                Icons.Default.People,
                                colorScheme.primary,
                                colorScheme.onPrimary,
                                onClick = { onNavigate(AppRoute.AdminUsers()) }
                            )
                        }
                        item {
                            DashboardCard(
                                "Uczniowie",
                                stats.totalStudents.toString(),
                                Icons.Default.School,
                                colorScheme.secondary,
                                colorScheme.onSecondary,
                                onClick = { onNavigate(AppRoute.AdminUsers(roleFilter = "STUDENT")) }
                            )
                        }
                        item {
                            DashboardCard(
                                "Korepetytorzy",
                                stats.totalTutors.toString(),
                                Icons.Default.Person,
                                colorScheme.tertiary,
                                colorScheme.onTertiary,
                                onClick = { onNavigate(AppRoute.AdminUsers(roleFilter = "TUTOR")) }
                            )
                        }
                        item {
                            DashboardCard(
                                "Administratorzy",
                                stats.totalAdmins.toString(),
                                Icons.Default.AdminPanelSettings,
                                colorScheme.error,
                                colorScheme.onError,
                                onClick = { onNavigate(AppRoute.AdminUsers(roleFilter = "ADMIN")) }
                            )
                        }
                    }

                    Text("Lekcje", style = typography.titleMedium, fontWeight = FontWeight.Bold)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.heightIn(max = 300.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            DashboardCard(
                                "Wszystkie",
                                stats.totalLessons.toString(),
                                Icons.Default.CalendarMonth,
                                colorScheme.primary,
                                colorScheme.onPrimary,
                                onClick = { onNavigate(AppRoute.AdminLessons()) }
                            )
                        }
                        item {
                            DashboardCard(
                                "Oczekujące",
                                stats.pendingLessons.toString(),
                                Icons.Default.HourglassEmpty,
                                colorScheme.tertiary,
                                colorScheme.onTertiary,
                                onClick = { onNavigate(AppRoute.AdminLessons(statusFilter = "PENDING")) }
                            )
                        }
                        item {
                            DashboardCard(
                                "Potwierdzone",
                                stats.confirmedLessons.toString(),
                                Icons.Default.CheckCircle,
                                colorScheme.secondary,
                                colorScheme.onSecondary,
                                onClick = { onNavigate(AppRoute.AdminLessons(statusFilter = "CONFIRMED")) }
                            )
                        }
                        item {
                            DashboardCard(
                                "Zakończone",
                                stats.completedLessons.toString(),
                                Icons.Default.Done,
                                colorScheme.onSurfaceVariant,
                                colorScheme.surfaceVariant,
                                onClick = { onNavigate(AppRoute.AdminLessons(statusFilter = "COMPLETED")) }
                            )
                        }
                    }

                    Text("Platforma", style = typography.titleMedium, fontWeight = FontWeight.Bold)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.heightIn(max = 240.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            DashboardCard(
                                "Przedmioty",
                                stats.totalSubjects.toString(),
                                Icons.AutoMirrored.Filled.MenuBook,
                                colorScheme.secondary,
                                colorScheme.onSecondary,
                                onClick = {
                                    onNavigate(
                                        AppRoute.AdminData(
                                            initialTab = 0,
                                            initialSubjectTab = 0
                                        )
                                    )
                                }
                            )
                        }
                        item {
                            DashboardCard(
                                "Kategorie",
                                stats.totalCategories.toString(),
                                Icons.Default.Category,
                                colorScheme.tertiary,
                                colorScheme.onTertiary,
                                onClick = {
                                    onNavigate(
                                        AppRoute.AdminData(
                                            initialTab = 0,
                                            initialSubjectTab = 1
                                        )
                                    )
                                }
                            )
                        }
                        item {
                            DashboardCard(
                                "Święta",
                                stats.totalHolidays.toString(),
                                Icons.Default.Event,
                                colorScheme.error,
                                colorScheme.onError,
                                onClick = { onNavigate(AppRoute.AdminData(initialTab = 1)) }
                            )
                        }
                        item {
                            DashboardCard(
                                "Opinie",
                                stats.totalReviews.toString(),
                                Icons.Default.Star,
                                colorScheme.primary,
                                colorScheme.onPrimary,
                                onClick = { onNavigate(AppRoute.AdminData(initialTab = 3)) }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

