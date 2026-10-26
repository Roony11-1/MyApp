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
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.roony11_1.myapp.core.util.combinarFechaHora
import io.github.roony11_1.myapp.core.util.fechaHoraAFechaLocal
import io.github.roony11_1.myapp.core.util.fechaHoraAHoraLocal
import io.github.roony11_1.myapp.core.util.fechaLocalAUtcMillis
import io.github.roony11_1.myapp.core.util.formatearFecha
import io.github.roony11_1.myapp.core.util.formatearHora
import io.github.roony11_1.myapp.core.util.utcMillisAFechaLocal
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorFechaHora(
    fechaHora: Long,
    onFechaHoraChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    expandido: Boolean? = null,
    onExpandirToggle: (() -> Unit)? = null)
{
    var expandidoInterno by remember { mutableStateOf(false) }
    val estaExpandido = expandido ?: expandidoInterno
    var mostrarSelectorFecha by remember { mutableStateOf(false) }
    var mostrarSelectorHora by remember { mutableStateOf(false) }

    val fechaActual = fechaHoraAFechaLocal(fechaHora)
    val horaActual = fechaHoraAHoraLocal(fechaHora)

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
                        onClick = {
                            if (expandido == null)
                                expandidoInterno = !expandidoInterno
                            else
                                onExpandirToggle?.invoke()
                        })
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
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(19.dp))
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp))
                {
                    Text(
                        text = "Fecha y hora",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface)

                    Text(
                        text = "${formatearFecha(fechaHora)} · " +
                                formatearHora(fechaHora),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Icon(
                    imageVector = if (estaExpandido)
                        Icons.Rounded.ExpandLess
                    else
                        Icons.Rounded.ExpandMore,
                    contentDescription = if (estaExpandido) "Ocultar" else "Cambiar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp))
            }

            AnimatedVisibility(visible = estaExpandido)
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp))
                {
                    CampoFechaHora(
                        icono = Icons.Rounded.CalendarMonth,
                        etiqueta = "Fecha",
                        valor = formatearFecha(fechaHora),
                        onClick = { mostrarSelectorFecha = true },
                        enabled = enabled,
                        modifier = Modifier.weight(1f))

                    CampoFechaHora(
                        icono = Icons.Rounded.AccessTime,
                        etiqueta = "Hora",
                        valor = formatearHora(fechaHora),
                        onClick = { mostrarSelectorHora = true },
                        enabled = enabled,
                        modifier = Modifier.weight(1f))
                }
            }
        }
    }

    if (mostrarSelectorFecha)
    {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = fechaLocalAUtcMillis(fechaActual))

        DatePickerDialog(
            onDismissRequest = { mostrarSelectorFecha = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { utcMillis ->
                            onFechaHoraChange(
                                combinarFechaHora(
                                    fecha = utcMillisAFechaLocal(utcMillis),
                                    hora = horaActual))
                        }
                        mostrarSelectorFecha = false
                    })
                {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarSelectorFecha = false })
                {
                    Text("Cancelar")
                }
            })
        {
            DatePicker(state = datePickerState)
        }
    }

    if (mostrarSelectorHora)
    {
        val timePickerState = rememberTimePickerState(
            initialHour = horaActual.hour,
            initialMinute = horaActual.minute,
            is24Hour = true)

        TimePickerDialog(
            onDismissRequest = { mostrarSelectorHora = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onFechaHoraChange(
                            combinarFechaHora(
                                fecha = fechaActual,
                                hora = LocalTime.of(
                                    timePickerState.hour,
                                    timePickerState.minute)))
                        mostrarSelectorHora = false
                    })
                {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarSelectorHora = false })
                {
                    Text("Cancelar")
                }
            },
            title = {
                Text("Seleccionar hora")
            })
        {
            TimePicker(state = timePickerState)
        }
    }
}

@Composable
private fun CampoFechaHora(
    icono: ImageVector,
    etiqueta: String,
    valor: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true)
{
    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)))
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp))
        {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp))

            Column(verticalArrangement = Arrangement.spacedBy(1.dp))
            {
                Text(
                    text = etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)

                Text(
                    text = valor,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
