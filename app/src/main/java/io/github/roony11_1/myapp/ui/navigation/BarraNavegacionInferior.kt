package io.github.roony11_1.myapp.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

@Composable
fun BarraNavegacionInferior(
    rutaActual: NavKey?,
    onInicioClick: () -> Unit,
    onRegistrarClick: () -> Unit,
    onHistorialClick: () -> Unit,
    onCatalogosClick: () -> Unit)
{
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    {
        ItemBarra(
            titulo = "Inicio",
            icono = Icons.Rounded.Home,
            seleccionado = rutaActual is Inicio,
            onClick = onInicioClick)

        ItemBarra(
            titulo = "Registrar",
            icono = Icons.Rounded.AddCircle,
            seleccionado = rutaActual is RegistrarGasto,
            onClick = onRegistrarClick)

        ItemBarra(
            titulo = "Historial",
            icono = Icons.Rounded.ReceiptLong,
            seleccionado = rutaActual is VerGastos,
            onClick = onHistorialClick)

        ItemBarra(
            titulo = "Catálogos",
            icono = Icons.Rounded.Category,
            seleccionado = rutaActual is Catalogos,
            onClick = onCatalogosClick)
    }
}

@Composable
private fun RowScope.ItemBarra(
    titulo: String,
    icono: ImageVector,
    seleccionado: Boolean,
    onClick: () -> Unit)
{
    NavigationBarItem(
        selected = seleccionado,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icono,
                contentDescription = titulo)
        },
        label = {
            Text(text = titulo)
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant))
}
