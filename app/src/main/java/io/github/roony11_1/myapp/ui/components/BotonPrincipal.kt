package io.github.roony11_1.myapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cargando: Boolean = false,
    icono: ImageVector? = null)
{
    val shape = RoundedCornerShape(18.dp)
    val colors = MaterialTheme.colorScheme
    val brush = if (enabled)
    {
        Brush.linearGradient(listOf(colors.primary, colors.secondary))
    }
    else
    {
        Brush.linearGradient(
            listOf(colors.surfaceContainerHigh, colors.surfaceContainerHigh))
    }

    Button(
        onClick = onClick,
        enabled = enabled && !cargando,
        shape = shape,
        contentPadding = PaddingValues(horizontal = 22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = colors.onPrimary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = colors.onSurfaceVariant),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape)
            .background(brush = brush, shape = shape))
    {
        if (cargando)
        {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = colors.onPrimary)
        }
        else
        {
            icono?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp))
            }

            if (icono != null)
            {
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = texto,
                style = MaterialTheme.typography.labelLarge)
        }
    }
}
