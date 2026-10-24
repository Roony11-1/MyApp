package io.github.roony11_1.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import io.github.roony11_1.myapp.ui.navigation.Inicio
import io.github.roony11_1.myapp.ui.navigation.RegistrarGasto
import io.github.roony11_1.myapp.ui.navigation.VerGasto
import io.github.roony11_1.myapp.ui.navigation.VerGastos
import io.github.roony11_1.myapp.ui.navigation.mainScreenEntry
import io.github.roony11_1.myapp.ui.navigation.registrarGastoEntry
import io.github.roony11_1.myapp.ui.navigation.verGastoDetalleEntry
import io.github.roony11_1.myapp.ui.navigation.verGastosEntry
import io.github.roony11_1.myapp.ui.theme.MyAppTheme

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as MyApp).container

        setContent {
            MyAppTheme {
                val backStack = rememberNavBackStack(Inicio)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        modifier = Modifier.padding(innerPadding),
                        entryProvider = entryProvider
                        {
                            mainScreenEntry(
                                onNavigateToVerGastos = { backStack.add(VerGastos) },
                                onNavigateToRegistrarGasto = { backStack.add(RegistrarGasto) })

                            registrarGastoEntry(
                                container = container,
                                onGuardadoExitoso = { backStack.removeLastOrNull() })

                            verGastosEntry(
                                container,
                                onNavigateToGastoClick = { id -> backStack.add(VerGasto(id)) })

                            verGastoDetalleEntry(container)
                        }
                    )
                }
            }
        }
    }
}