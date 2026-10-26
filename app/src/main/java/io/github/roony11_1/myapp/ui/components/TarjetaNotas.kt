package io.github.roony11_1.myapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun TarjetaNotas(
    notas: String,
    expandido: Boolean,
    onExpandirToggle: () -> Unit,
    onNotasChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true)
{
    val resumen = notas.trim()
        .lineSequence()
        .firstOrNull()
        .orEmpty()
        .ifBlank { "Opcional" }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)))
    {
        Column(modifier = Modifier.animateContentSize())
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        enabled = enabled,
                        onClick = onExpandirToggle)
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp))
            {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.13f),
                            shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center)
                {
                    Icon(
                        imageVector = Icons.Rounded.Notes,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(19.dp))
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp))
                {
                    Text(
                        text = "Notas",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface)

                    Text(
                        text = resumen,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                }

                Icon(
                    imageVector = if (expandido)
                        Icons.Rounded.ExpandLess
                    else
                        Icons.Rounded.ExpandMore,
                    contentDescription = if (expandido) "Ocultar" else "Agregar notas",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp))
            }

            AnimatedVisibility(visible = expandido)
            {
                CampoTextoPremium(
                    value = notas,
                    onValueChange = onNotasChange,
                    label = "Notas",
                    leadingIcon = Icons.Rounded.Notes,
                    placeholder = "Agrega un detalle opcional",
                    minLines = 3,
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp))
            }
        }
    }
}
