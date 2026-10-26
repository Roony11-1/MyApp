package io.github.roony11_1.myapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.roony11_1.myapp.domain.model.EstadoGasto

@Composable
fun EstadoGastoBadge(
    estado: EstadoGasto,
    modifier: Modifier = Modifier)
{
    val anulado = estado == EstadoGasto.ANULADO

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = if (anulado)
            MaterialTheme.colorScheme.errorContainer
        else
            MaterialTheme.colorScheme.secondaryContainer)
    {
        Text(
            text = if (anulado) "Anulado" else "Vigente",
            style = MaterialTheme.typography.labelSmall,
            color = if (anulado)
                MaterialTheme.colorScheme.onErrorContainer
            else
                MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
    }
}
