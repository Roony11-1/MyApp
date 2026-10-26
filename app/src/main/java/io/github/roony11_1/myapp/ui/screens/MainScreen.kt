package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onVerGastosClick: () -> Unit,
    onRegistrarGastoClick: () -> Unit)
{
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp))
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp))
        {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(primary, secondary)),
                        shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center)
            {
                Icon(
                    imageVector = Icons.Rounded.AccountBalanceWallet,
                    contentDescription = null,
                    tint = onPrimary,
                    modifier = Modifier.size(24.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp))
            {
                Text(
                    text = "MyApp",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground)

                Text(
                    text = "Finanzas con claridad",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = Color.Transparent)
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(listOf(primary, secondary)),
                        shape = MaterialTheme.shapes.extraLarge))
            {
                Canvas(modifier = Modifier.matchParentSize())
                {
                    drawCircle(
                        color = onPrimary.copy(alpha = 0.08f),
                        radius = size.minDimension * 0.55f,
                        center = Offset(size.width * 0.9f, size.height * 0.05f))

                    drawCircle(
                        color = onPrimary.copy(alpha = 0.06f),
                        radius = size.minDimension * 0.4f,
                        center = Offset(size.width * 0.05f, size.height * 0.95f))
                }

                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp))
                {
                    Surface(
                        shape = CircleShape,
                        color = onPrimary.copy(alpha = 0.12f))
                    {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp))
                        {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = onPrimary,
                                modifier = Modifier.size(14.dp))

                            Text(
                                text = "CLARIDAD FINANCIERA",
                                style = MaterialTheme.typography.labelSmall,
                                color = onPrimary)
                        }
                    }

                    Text(
                        text = "Tus gastos,\nbajo control.",
                        style = MaterialTheme.typography.headlineLarge,
                        color = onPrimary)

                    Text(
                        text = "Registra cada movimiento y revisa tu historial en segundos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = onPrimary.copy(alpha = 0.8f))
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp))
        {
            Text(
                text = "Empieza ahora",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground)

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp))
            {
                TarjetaAccion(
                    titulo = "Registrar",
                    descripcion = "Nuevo gasto",
                    icono = Icons.Rounded.AddCircle,
                    acento = primary,
                    onClick = onRegistrarGastoClick,
                    modifier = Modifier.weight(1f))

                TarjetaAccion(
                    titulo = "Ver gastos",
                    descripcion = "Tu historial",
                    icono = Icons.Rounded.ReceiptLong,
                    acento = secondary,
                    onClick = onVerGastosClick,
                    modifier = Modifier.weight(1f))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center)
        {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(primary, CircleShape))

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Cada movimiento cuenta.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TarjetaAccion(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    acento: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier)
{
    Surface(
        onClick = onClick,
        modifier = modifier.height(164.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)))
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween)
        {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = acento.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center)
            {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = acento,
                    modifier = Modifier.size(22.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp))
            {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface)

                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
