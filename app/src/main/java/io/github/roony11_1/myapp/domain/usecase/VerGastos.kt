package io.github.roony11_1.myapp.domain.usecase

import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.repository.IGastoRepository

class VerGastos(
    private val gastoRepository: IGastoRepository)
{
    suspend operator fun invoke(): List<Gasto>
    {
        return gastoRepository.findAll()
    }
}