package com.iftikar.outlier.core.designsystem.component.post

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TechStackChips(
    techStack: List<String>,
    modifier: Modifier = Modifier,
    visibleCount: Int = 3
) {
    if (techStack.isEmpty()) return

    val visibleTechnologies = techStack.take(visibleCount)
    val remainingCount = techStack.size - visibleTechnologies.size

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        visibleTechnologies.forEach { technology ->

            TechStackChip(
                text = technology
            )
        }

        if (remainingCount > 0) {
            TechStackChip(
                text = "+$remainingCount"
            )
        }
    }
}

@Composable
private fun TechStackChip(
    text: String
) {
    val chipColor = MaterialTheme.colorScheme.tertiary

    Surface(
        shape = RoundedCornerShape(50),
        color = chipColor.copy(alpha = 0.12f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 8.dp
            ),
            color = chipColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}