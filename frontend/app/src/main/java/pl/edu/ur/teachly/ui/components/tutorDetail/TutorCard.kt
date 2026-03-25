package pl.edu.ur.teachly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.home.Tutor

@Composable
fun TutorCard(
    tutor   : Tutor,
    colors  : Pair<Color, Color>,
    onClick : () -> Unit,
) {
    val (avatarBg, avatarFg) = colors

    Surface(
        modifier = Modifier.fillMaxWidth().clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication        = null,
            onClick           = onClick,
        ),
        shape           = RoundedCornerShape(20.dp),
        color           = MaterialTheme.colorScheme.surface,
        border          = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TutorAvatar(initials = tutor.initials, bg = avatarBg, fg = avatarFg, size = 48)
                TutorCardInfo(tutor = tutor)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)

            NearestSlots(slots = tutor.nearestSlots)
        }
    }
}

@Composable
fun TutorAvatar(
    initials : String,
    bg       : Color,
    fg       : Color,
    size     : Int = 48,
) {
    Box(contentAlignment = Alignment.BottomEnd) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(RoundedCornerShape((size * 0.33f).dp))
                .background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text  = initials,
                style = if (size >= 64) MaterialTheme.typography.titleMedium else MaterialTheme.typography.labelMedium,
                color = fg,
            )
        }
    }
}

@Composable
private fun TutorCardInfo(tutor: Tutor) {
    Column {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top,
        ) {
            Column {
                Text(tutor.name,    style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                Text(tutor.subject, style = MaterialTheme.typography.bodySmall,  color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            PriceLabel(price = tutor.pricePerHour)
        }

        Spacer(Modifier.height(8.dp))
        RatingRow(rating = tutor.rating, reviewCount = tutor.reviewCount, isOnline = tutor.isOnline)
        Spacer(Modifier.height(8.dp))
        TagRow(tags = tutor.tags)
    }
}

// Price
@Composable
fun PriceLabel(price: Int, large: Boolean = false) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text  = stringResource(R.string.tutor_price_format, price),
            style = if (large) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text  = stringResource(R.string.tutor_per_hour),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// Rating
@Composable
fun RatingRow(rating: Double, reviewCount: Int, isOnline: Boolean) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text  = stringResource(R.string.tutor_rating_format, rating),
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFFD97706),
        )
        Text(
            text  = stringResource(R.string.tutor_reviews_format, reviewCount),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// Tags
@Composable
fun TagRow(tags: List<String>) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        tags.forEach { tag ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text  = tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

// Closest slots
@Composable
fun NearestSlots(slots: List<String>) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text  = stringResource(R.string.tutor_nearest),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        slots.forEach { slot ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text  = slot,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}