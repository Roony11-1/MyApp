package io.github.roony11_1.myapp.domain.usecase

import io.github.roony11_1.myapp.core.pagination.Page
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.model.GastosFiltros
import io.github.roony11_1.myapp.domain.repository.IGastoRepository

class VerGastos(
    private val gastoRepository: IGastoRepository)
{
    suspend operator fun invoke(
        filtros: GastosFiltros,
        number: Int,
        size: Int): Page<Gasto>
    {
        return gastoRepository.findPage(filtros, number, size)
    }
}
