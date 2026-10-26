package io.github.roony11_1.myapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
    enabled: Boolean = true)
{
    var expandido by remember { mutableStateOf(false) }
    var mostrarSelectorFecha by remember { mutableStateOf(false) }
    var mostrarSelectorHora by remember { mutableStateOf(false) }

    val fechaActual = fechaHoraAFechaLocal(fechaHora)
    val horaActual = fechaHoraAHoraLocal(fechaHora)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer)
    {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp))
        {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically)
            {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp))
                {
                    Text(
                        text = "Fecha y hora",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface)

                    Text(
                        text = "${formatearFecha(fechaHora)} · " +
                                formatearHora(fechaHora),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                TextButton(
                    onClick = { expandido = !expandido },
                    enabled = enabled)
                {
                    Text(
                        text = if (expandido) "Ocultar" else "Cambiar",
                        style = MaterialTheme.typography.labelLarge)
                }
            }

            if (expandido)
            {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp))
                {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp))
                    {
                        Text(
                            text = "Fecha",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)

                        OutlinedButton(
                            onClick = { mostrarSelectorFecha = true },
                            enabled = enabled,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth())
                        {
                            Text(
                                text = formatearFecha(fechaHora),
                                style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp))
                    {
                        Text(
                            text = "Hora",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)

                        OutlinedButton(
                            onClick = { mostrarSelectorHora = true },
                            enabled = enabled,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth())
                        {
                            Text(
                                text = formatearHora(fechaHora),
                                style = MaterialTheme.typography.labelLarge)
                        }
                    }
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
