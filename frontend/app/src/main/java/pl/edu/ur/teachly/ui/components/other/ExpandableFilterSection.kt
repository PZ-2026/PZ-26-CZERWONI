package pl.edu.ur.teachly.ui.components.other

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableFilterSection(
    activeFilterCount: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(200),
        label = "chevron",
    )
    val headerBackground by animateColorAsState(
        targetValue = if (expanded) colorScheme.primaryContainer else colorScheme.surface,
        animationSpec = tween(200),
        label = "headerBg",
    )
    val headerContentColor by animateColorAsState(
        targetValue = if (expanded) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "headerFg",
    )

    Surface(
        color = colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = modifier,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(0.dp))
                    .clickable(role = Role.Button) { expanded = !expanded }
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BadgedBox(
                    badge = {
                        if (activeFilterCount > 0) {
                            Badge(
                                containerColor = colorScheme.primary,
                                contentColor = colorScheme.onPrimary,
                            ) {
                                Text(activeFilterCount.toString(), style = typography.labelSmall)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = headerContentColor,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Filtry",
                    style = typography.labelLarge,
                    color = headerContentColor,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Zwiń" else "Rozwiń",
                    tint = headerContentColor,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(chevronRotation),
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(animationSpec = tween(200)) + fadeIn(
                    animationSpec = tween(
                        200
                    )
                ),
                exit = shrinkVertically(animationSpec = tween(200)) + fadeOut(
                    animationSpec = tween(
                        150
                    )
                ),
            ) {
                Column {
                    HorizontalDivider(color = colorScheme.outlineVariant)
                    content()
                }
            }
        }
    }
}
