package io.github.roony11_1.myapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun FondoPremium(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit)
{
    val background = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary

    Box(modifier = modifier.fillMaxSize())
    {
        Canvas(modifier = Modifier.fillMaxSize())
        {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(background, surface, background)))

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primary.copy(alpha = 0.22f),
                        Color.Transparent),
                    center = Offset(size.width * 0.88f, 0f),
                    radius = size.width * 0.85f),
                radius = size.width * 0.85f,
                center = Offset(size.width * 0.88f, 0f))

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        secondary.copy(alpha = 0.09f),
                        Color.Transparent),
                    center = Offset(0f, size.height * 0.72f),
                    radius = size.width * 0.9f),
                radius = size.width * 0.9f,
                center = Offset(0f, size.height * 0.72f))
        }

        content()
    }
}
