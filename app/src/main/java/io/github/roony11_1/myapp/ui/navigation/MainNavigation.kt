package io.github.roony11_1.myapp.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.github.roony11_1.myapp.core.di.AppContainer
import io.github.roony11_1.myapp.ui.screens.MainScreen
import io.github.roony11_1.myapp.ui.viewmodel.HomeViewModel

fun EntryProviderScope<NavKey>.mainScreenEntry(
    container: AppContainer,
    onNavigateToRegistrarGasto: () -> Unit,
    onNavigateToVerGastos: (Int, Int) -> Unit,
    onNavigateToCatalogos: () -> Unit)
{
    entry<Inicio>
    {
        val viewModel: HomeViewModel = viewModel(
            factory = HomeViewModel.factory(container))

        MainScreen(
            viewModel = viewModel,
            onVerGastosClick = onNavigateToVerGastos,
            onRegistrarGastoClick = onNavigateToRegistrarGasto,
            onCatalogoClick = onNavigateToCatalogos)
    }
}
