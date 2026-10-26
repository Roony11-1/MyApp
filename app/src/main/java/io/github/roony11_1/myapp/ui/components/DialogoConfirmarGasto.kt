package io.github.roony11_1.myapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.roony11_1.myapp.core.util.formatearFechaHora
import io.github.roony11_1.myapp.core.util.formatearMonto

data class ResumenGasto(
    val monto: Double,
    val descripcion: String,
    val cuenta: String,
    val categorias: List<String>,
    val fechaHora: Long,
    val comercio: String? = null,
    val etiquetas: List<String> = emptyList(),
    val notas: String? = null)

@Composable
fun DialogoConfirmarGasto(
    resumen: ResumenGasto,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier,
    guardando: Boolean = false)
{
    val puedeConfirmar = !guardando &&
            resumen.descripcion.isNotBlank() &&
            resumen.categorias.isNotEmpty()

    AlertDialog(
        onDismissRequest = {
            if (!guardando)
                onCancelar()
        },
        modifier = modifier,
        title = {
            Text(
                text = "Confirmar gasto",
                style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp))
            {
                Text(
                    text = "Revisa los datos antes de guardar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp))
                {
                    Text(
                        text = "Monto",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Text(
                        text = formatearMonto(resumen.monto),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold)
                }

                ResumenFila(
                    etiqueta = "Descripción",
                    valor = resumen.descripcion)

                ResumenFila(
                    etiqueta = "Cuenta",
                    valor = resumen.cuenta)

                ResumenFila(
                    etiqueta = "Categorías",
                    valor = if (resumen.categorias.isEmpty())
                        "Sin categoría"
                    else
                        resumen.categorias.joinToString(", "))

                ResumenFila(
                    etiqueta = "Fecha y hora",
                    valor = formatearFechaHora(resumen.fechaHora))

                resumen.comercio?.let { comercio ->
                    ResumenFila(
                        etiqueta = "Comercio",
                        valor = comercio)
                }

                if (resumen.etiquetas.isNotEmpty())
                {
                    ResumenFila(
                        etiqueta = "Etiquetas",
                        valor = resumen.etiquetas.joinToString(", "))
                }

                resumen.notas?.let { notas ->
                    ResumenFila(
                        etiqueta = "Notas",
                        valor = notas)
                }

                if (resumen.descripcion.isBlank())
                {
                    Text(
                        text = "La descripción es obligatoria.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error)
                }

                if (resumen.categorias.isEmpty())
                {
                    Text(
                        text = "Selecciona al menos una categoría.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmar,
                enabled = puedeConfirmar)
            {
                Text(if (guardando) "Guardando..." else "Confirmar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancelar,
                enabled = !guardando)
            {
                Text("Cancelar")
            }
        })
}

@Composable
private fun ResumenFila(
    etiqueta: String,
    valor: String)
{
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp))
    {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface)
    }
}
