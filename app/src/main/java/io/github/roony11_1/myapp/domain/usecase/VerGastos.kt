package io.github.roony11_1.myapp.domain.usecase

import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.repository.IGastoRepository

class VerGastos(
    private val gastoRepository: IGastoRepository)
{
    suspend operator fun invoke(limit: Int, offset: Int): List<Gasto>
    {
        return gastoRepository.findPage(limit, offset)
    }
}