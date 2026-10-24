package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onVerGastosClick: () -> Unit,
    onRegistrarGastoClick: () -> Unit)
{
    Column(modifier = modifier)
    {
        Button(
            modifier = modifier,
            onClick = onRegistrarGastoClick)
        {
            Text("Registrar gasto")
        }

        Button(
            modifier = modifier,
            onClick = onVerGastosClick)
        {
            Text("Ver gastos")
        }
    }
}