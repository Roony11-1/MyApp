package io.github.roony11_1.myapp.domain.repository

import io.github.roony11_1.myapp.domain.model.Gasto

interface IGastoRepository
{
    suspend fun save(gasto: Gasto): Gasto
    suspend fun findAll(): List<Gasto>
    suspend fun findById(id: Long): Gasto?
}