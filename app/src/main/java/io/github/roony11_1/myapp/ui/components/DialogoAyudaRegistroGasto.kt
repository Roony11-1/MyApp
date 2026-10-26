package io.github.roony11_1.myapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun DialogoAyudaRegistroGasto(
    onCerrar: () -> Unit,
    modifier: Modifier = Modifier)
{
    AlertDialog(
        onDismissRequest = onCerrar,
        modifier = modifier,
        title = {
            Text(
                text = "Cómo registrar un gasto",
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
                    text = "Completa estos pasos y guarda tu movimiento.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)

                FilaAyuda(
                    icono = Icons.AutoMirrored.Rounded.Notes,
                    titulo = "Descripción",
                    texto = "Escribe en qué gastaste. Si hay gastos parecidos, " +
                            "las sugerencias completan cuenta, categorías, " +
                            "comercio y etiquetas.")

                FilaAyuda(
                    icono = Icons.Rounded.Payments,
                    titulo = "Monto y categorías",
                    texto = "Indica cuánto fue y elige una o más categorías.")

                FilaAyuda(
                    icono = Icons.Rounded.AccountBalanceWallet,
                    titulo = "Cuenta",
                    texto = "Selecciona de dónde salió el dinero.")

                FilaAyuda(
                    icono = Icons.Rounded.Store,
                    titulo = "Datos opcionales",
                    texto = "Puedes agregar comercio, etiquetas y notas.")

                FilaAyuda(
                    icono = Icons.Rounded.CalendarMonth,
                    titulo = "Fecha y guardado",
                    texto = "Ajusta la fecha si hace falta y confirma el " +
                            "resumen antes de guardar.")

                Text(
                    text = "Las sugerencias no modifican el monto ni la fecha.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary)
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar)
            {
                Text(text = "Entendido")
            }
        })
}

@Composable
private fun FilaAyuda(
    icono: ImageVector,
    titulo: String,
    texto: String)
{
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top)
    {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center)
        {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp))
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp))
        {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface)

            Text(
                text = texto,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
