package io.github.roony11_1.myapp.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.github.roony11_1.myapp.core.di.AppContainer
import io.github.roony11_1.myapp.ui.screens.CatalogosScreen
import io.github.roony11_1.myapp.ui.viewmodel.CatalogosViewModel

fun EntryProviderScope<NavKey>.catalogosEntry(container: AppContainer)
{
    entry<Catalogos>
    {
        val viewModel: CatalogosViewModel = viewModel(
            factory = CatalogosViewModel.factory(container))

        CatalogosScreen(viewModel = viewModel)
    }
}
