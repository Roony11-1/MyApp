package io.github.roony11_1.myapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun TarjetaSeccion(
    titulo: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    descripcion: String? = null,
    acento: Color = MaterialTheme.colorScheme.primary,
    contenido: @Composable ColumnScope.() -> Unit)
{
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)))
    {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp))
        {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp))
            {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = acento.copy(alpha = 0.13f),
                            shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center)
                {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = acento,
                        modifier = Modifier.size(19.dp))
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp))
                {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface)

                    descripcion?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            contenido()
        }
    }
}
