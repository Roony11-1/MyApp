package io.github.roony11_1.myapp.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.github.roony11_1.myapp.core.di.AppContainer
import io.github.roony11_1.myapp.ui.screens.VerGastosScreen
import io.github.roony11_1.myapp.ui.viewmodel.VerGastosViewModel

fun EntryProviderScope<NavKey>.verGastosEntry(
    container: AppContainer,
    onNavigateToGastoClick: (Long) -> Unit,
    onBack: () -> Unit)
{
    entry<VerGastos> { key ->
        val viewModel: VerGastosViewModel = viewModel(
            factory = VerGastosViewModel.factory(
                container = container,
                anioInicial = key.anio,
                mesInicial = key.mes))

        VerGastosScreen(
            viewModel = viewModel,
            onGastoClick = onNavigateToGastoClick,
            onBack = onBack)
    }
}
