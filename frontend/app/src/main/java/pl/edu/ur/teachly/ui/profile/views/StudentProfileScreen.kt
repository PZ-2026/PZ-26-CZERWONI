package pl.edu.ur.teachly.ui.profile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.model.ReviewResponse
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.other.cards.StatCard
import pl.edu.ur.teachly.ui.components.other.formatDate
import pl.edu.ur.teachly.ui.components.other.formatPhoneNumber
import pl.edu.ur.teachly.ui.components.profile.ProfileDataCard
import pl.edu.ur.teachly.ui.components.profile.ProfileDataDivider
import pl.edu.ur.teachly.ui.components.profile.ProfileHeader
import pl.edu.ur.teachly.ui.components.profile.ProfileInfoRow
import pl.edu.ur.teachly.ui.profile.viewmodels.ProfileViewModel
import pl.edu.ur.teachly.ui.review.viewmodels.MyReviewsViewModel
import pl.edu.ur.teachly.ui.review.views.AddReviewDialog
import pl.edu.ur.teachly.ui.theme.AvatarColors
import java.time.LocalDate

@Composable
fun StudentProfileScreen(
    onBack: () -> Unit,
    onEditClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
    myReviewsViewModel: MyReviewsViewModel = koinViewModel(),
) {
    val profile by viewModel.profile.collectAsState()
    val reviewsState by myReviewsViewModel.state.collectAsState()

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var editingReview by remember { mutableStateOf<ReviewResponse?>(null) }
    var deletingReview by remember { mutableStateOf<ReviewResponse?>(null) }

    LaunchedEffect(Unit) { myReviewsViewModel.loadMyReviews() }

    LaunchedEffect(reviewsState.editSuccess) {
        if (reviewsState.editSuccess) {
            editingReview = null
            myReviewsViewModel.clearEditSuccess()
        }
    }

    editingReview?.let { review ->
        AddReviewDialog(
            isLoading = reviewsState.isSubmitting,
            error = reviewsState.error,
            initialRating = review.rating,
            initialComment = review.comment ?: "",
            onDismiss = {
                editingReview = null
                myReviewsViewModel.clearError()
            },
            onSubmit = { rating, comment ->
                myReviewsViewModel.updateReview(review.id, review.tutorId, rating, comment)
            },
        )
    }

    deletingReview?.let { review ->
        AlertDialog(
            onDismissRequest = { deletingReview = null },
            title = { Text("Usuń recenzję") },
            text = {
                Text("Czy na pewno chcesz usunąć opinię o ${review.tutorFirstName} ${review.tutorLastName}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        myReviewsViewModel.deleteReview(review.id)
                        deletingReview = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error),
                ) { Text("Usuń") }
            },
            dismissButton = {
                OutlinedButton(onClick = { deletingReview = null }) { Text("Anuluj") }
            },
        )
    }

    when {
        profile.isLoading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator() }

        else -> Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            ProfileHeader(
                profile = profile,
                avatarColor = AvatarColors[0],
                role = profile.role,
                onBack = onBack,
                onEditClick = onEditClick,
            )

            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = colorScheme.surface,
                contentColor = colorScheme.primary,
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Profil") },
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(stringResource(R.string.my_reviews_title)) },
                )
            }

            when (selectedTab) {
                0 -> ProfileTab(
                    profile = profile,
                    onLogout = onLogout,
                )

                1 -> MyReviewsTab(
                    reviews = reviewsState.reviews,
                    isLoading = reviewsState.isLoading,
                    onEditReview = { editingReview = it },
                    onDeleteReview = { deletingReview = it },
                )
            }
        }
    }
}

@Composable
private fun ProfileTab(
    profile: pl.edu.ur.teachly.ui.profile.viewmodels.StudentProfile,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Spacer(Modifier.height(4.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = stringResource(R.string.activity),
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard(
                    value = "${profile.lessonsCount}",
                    label = stringResource(R.string.completed_lessons),
                    modifier = Modifier.weight(1f),
                )
            }
        }

        ProfileDataCard(title = stringResource(R.string.account_data)) {
            if (profile.email.isNotBlank()) {
                ProfileInfoRow(
                    icon = Icons.Default.AlternateEmail,
                    label = stringResource(R.string.email),
                    value = profile.email,
                )
            }
            val phone = formatPhoneNumber(profile.phoneNumber.toString())
            if (phone.isNotBlank()) {
                ProfileDataDivider()
                ProfileInfoRow(
                    icon = Icons.Default.Phone,
                    label = stringResource(R.string.field_phone),
                    value = phone,
                )
            }
            if (profile.createdAt.isNotBlank()) {
                ProfileDataDivider()
                ProfileInfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = stringResource(R.string.account_active_since),
                    value = formatDate(LocalDate.parse(profile.createdAt.take(10))),
                )
            }
            ProfileDataDivider()
            ProfileInfoRow(
                icon = Icons.Default.Person,
                label = stringResource(R.string.role),
                value = stringResource(R.string.student),
            )
        }

        PrimaryButton(
            text = stringResource(R.string.logout),
            onClick = onLogout,
            modifier = Modifier.padding(bottom = 32.dp, top = 8.dp),
        )
    }
}

@Composable
private fun MyReviewsTab(
    reviews: List<ReviewResponse>,
    isLoading: Boolean,
    onEditReview: (ReviewResponse) -> Unit,
    onDeleteReview: (ReviewResponse) -> Unit,
) {
    when {
        isLoading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator() }

        reviews.isEmpty() -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.my_reviews_empty),
                style = typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
            )
        }

        else -> Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            reviews.forEach { review ->
                MyReviewCard(
                    review = review,
                    onEdit = { onEditReview(review) },
                    onDelete = { onDeleteReview(review) },
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MyReviewCard(
    review: ReviewResponse,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = colorScheme.surface,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${review.tutorFirstName} ${review.tutorLastName}",
                        style = typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface,
                    )
                    Text(
                        text = review.createdAt.take(10),
                        style = typography.bodySmall,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "★".repeat(review.rating.toInt().coerceIn(1, 5)),
                        style = typography.labelSmall,
                        color = Color(0xFFD97706),
                    )
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edytuj",
                            modifier = Modifier.size(15.dp),
                            tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Usuń",
                            modifier = Modifier.size(15.dp),
                            tint = colorScheme.error.copy(alpha = 0.5f),
                        )
                    }
                }
            }

            if (!review.comment.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = review.comment,
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
