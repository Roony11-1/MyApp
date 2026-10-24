package io.github.roony11_1.myapp.core.di

import android.content.Context
import io.github.roony11_1.myapp.core.services.IClock
import io.github.roony11_1.myapp.core.services.SystemClock
import io.github.roony11_1.myapp.data.local.repository.InMemoryGastoRepository
import io.github.roony11_1.myapp.domain.repository.IGastoRepository

class AppContainer(context: Context)
{
    val clock: IClock = SystemClock()

    val gastoRepository: IGastoRepository = InMemoryGastoRepository()
}