package io.github.roony11_1.myapp.ui.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.github.roony11_1.myapp.ui.screens.MainScreen

fun EntryProviderScope<NavKey>.mainScreenEntry(
    onNavigateToVerGastos: () -> Unit,
    onNavigateToRegistrarGasto: () -> Unit)
{
    entry<Inicio>
    {
        MainScreen(
            onVerGastosClick = onNavigateToVerGastos,
            onRegistrarGastoClick = onNavigateToRegistrarGasto)
    }
}