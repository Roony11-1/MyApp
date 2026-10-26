package io.github.roony11_1.myapp.domain.repository

import io.github.roony11_1.myapp.core.pagination.Page
import io.github.roony11_1.myapp.domain.model.Gasto

interface IGastoRepository
{
    suspend fun save(gasto: Gasto): Gasto
    suspend fun findAll(): List<Gasto>
    suspend fun findById(id: Long): Gasto?
    suspend fun findPage(number: Int, size: Int): Page<Gasto>
    suspend fun buscarSimilares(descripcion: String, limite: Int = 5): List<Gasto>
    suspend fun anular(id: Long): Boolean
}