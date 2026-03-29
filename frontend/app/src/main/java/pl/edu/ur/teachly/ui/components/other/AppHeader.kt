package pl.edu.ur.teachly.ui.components.other

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R

// Gradients
sealed interface HeaderBackground {
    data class Vertical(
        val colors: List<Color>,
    ) : HeaderBackground

    data class Diagonal(
        val colors: List<Color>,
    ) : HeaderBackground
}

@Composable
fun AppHeader(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    background: HeaderBackground,
    topPadding: Dp = 20.dp,
    bottomPadding: Dp = 16.dp,
    decorativeCircle: Boolean = false,
    extra: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val brush = when (background) {
        is HeaderBackground.Vertical -> Brush.verticalGradient(background.colors)
        is HeaderBackground.Diagonal -> Brush.linearGradient(
            colors = background.colors,
            start = Offset.Zero,
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
        )
    }

    val isLight = background is HeaderBackground.Vertical
    val backBg = if (isLight) MaterialTheme.colorScheme.surface
    else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.12f)
    val backTint = if (isLight) MaterialTheme.colorScheme.onSurface
    else MaterialTheme.colorScheme.onPrimary
    val titleColor = if (isLight) MaterialTheme.colorScheme.onBackground
    else MaterialTheme.colorScheme.onPrimary
    val subtitleColor = if (isLight) MaterialTheme.colorScheme.onSurfaceVariant
    else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush)
            .padding(horizontal = 24.dp)
            .padding(top = topPadding, bottom = bottomPadding)
    ) {
        if (decorativeCircle) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .offset(x = 60.dp, y = (-40).dp)
                    .align(Alignment.TopEnd)
                    .background(
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.05f),
                        CircleShape,
                    )
            )
        }

        Column {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .background(backBg, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = backTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.height(32.dp))
            }

            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = titleColor,
            )

            if (subtitle != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtitleColor,
                )
            }

            extra?.invoke(this)
        }
    }
}