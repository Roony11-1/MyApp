package io.github.roony11_1.myapp.domain.repository

import io.github.roony11_1.myapp.core.pagination.Page
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.model.GastosFiltros
import io.github.roony11_1.myapp.domain.model.MesHistorial
import io.github.roony11_1.myapp.domain.model.ResumenMes

interface IGastoRepository
{
    suspend fun save(gasto: Gasto): Gasto
    suspend fun findAll(): List<Gasto>
    suspend fun findById(id: Long): Gasto?
    suspend fun findPage(
        filtros: GastosFiltros,
        number: Int,
        size: Int): Page<Gasto>
    suspend fun buscarSimilares(descripcion: String, limite: Int = 5): List<Gasto>
    suspend fun anular(id: Long): Boolean
    suspend fun resumenMes(desde: Long, hastaExclusivo: Long): ResumenMes
    suspend fun historialMeses(
        desde: Long?,
        hastaExclusivo: Long): List<MesHistorial>
}
