package io.github.roony11_1.myapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ChipPremium(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icono: ImageVector? = null,
    iconoFinal: ImageVector? = null)
{
    val colors = MaterialTheme.colorScheme
    val iconoInicial = icono
    val iconoTrailing = iconoFinal

    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = {
            Text(
                text = texto,
                style = MaterialTheme.typography.labelLarge)
        },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = when
        {
            seleccionado ->
            {
                {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp))
                }
            }

            iconoInicial != null ->
            {
                {
                    Icon(
                        imageVector = iconoInicial,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp))
                }
            }

            else -> null
        },
        trailingIcon = if (iconoTrailing != null)
        {
            {
                Icon(
                    imageVector = iconoTrailing,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp))
            }
        }
        else
        {
            null
        },
        shape = MaterialTheme.shapes.small,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = colors.surfaceContainer,
            labelColor = colors.onSurfaceVariant,
            iconColor = colors.onSurfaceVariant,
            selectedContainerColor = colors.primaryContainer,
            selectedLabelColor = colors.onPrimaryContainer,
            selectedLeadingIconColor = colors.onPrimaryContainer),
        border = BorderStroke(
            width = 1.dp,
            color = if (seleccionado)
                colors.primary.copy(alpha = 0.55f)
            else
                colors.outlineVariant.copy(alpha = 0.8f)))
}
