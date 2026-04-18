package pl.edu.ur.teachly.ui.components.other

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R

sealed interface HeaderBackground {
    data class Vertical(val colors: List<Color>) : HeaderBackground
    data class Diagonal(val colors: List<Color>) : HeaderBackground
}

@Composable
fun AppHeader(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    background: HeaderBackground,
    topPadding: Dp = 24.dp,
    bottomPadding: Dp = 20.dp,
    decorativeCircle: Boolean = false,
) {
    val brush = when (background) {
        is HeaderBackground.Vertical -> Brush.verticalGradient(background.colors)
        is HeaderBackground.Diagonal -> Brush.linearGradient(
            colors = background.colors,
            start = Offset.Zero,
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush)
    ) {
        if (decorativeCircle) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .offset(x = 80.dp, y = (-60).dp)
                    .align(Alignment.TopEnd)
                    .background(colorScheme.onPrimary.copy(alpha = 0.06f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .offset(x = 40.dp, y = 40.dp)
                    .align(Alignment.TopEnd)
                    .background(colorScheme.onPrimary.copy(alpha = 0.04f), CircleShape)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = topPadding, bottom = bottomPadding)
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            colorScheme.onPrimary.copy(alpha = 0.15f),
                            RoundedCornerShape(10.dp),
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.height(32.dp))
            } else Spacer(Modifier.height(12.dp))

            Text(
                text = title,
                style = typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onPrimary,
            )

            if (subtitle != null) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    style = typography.bodyLarge,
                    color = colorScheme.onPrimary.copy(alpha = 0.75f),
                )
            }
            if (onBack == null) {
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
