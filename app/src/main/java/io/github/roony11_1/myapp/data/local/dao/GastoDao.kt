package io.github.roony11_1.myapp.data.local.dao

import io.github.roony11_1.myapp.domain.model.Gasto
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface IGastoDao
{
    suspend fun insert(gasto: Gasto): Long
    suspend fun selectAll(): List<Gasto>
    suspend fun selectById(id: Long): Gasto?
    suspend fun selectPage(limit: Int, offset: Int): List<Gasto>
}

class InMemoryGastoDao : IGastoDao
{
    private val mutex = Mutex()
    private val gastos = mutableListOf<Gasto>()
    private var nextId = 1L

    override suspend fun insert(gasto: Gasto): Long = mutex.withLock {
        val id = nextId++

        gastos.add(
            Gasto(
                id = id,
                monto = gasto.monto,
                descripcion = gasto.descripcion,
                cuenta = gasto.cuenta,
                categorias = gasto.categorias,
                fechaHora = gasto.fechaHora))
        id
    }

    override suspend fun selectAll(): List<Gasto> = mutex.withLock {
        gastos.toList()
    }

    override suspend fun selectById(id: Long): Gasto? = mutex.withLock {
        gastos.firstOrNull { it.id == id }
    }

    override suspend fun selectPage(
        limit: Int,
        offset: Int): List<Gasto> = mutex.withLock {
        require(limit > 0) { "El límite debe ser positivo" }
        require(offset >= 0) { "El desplazamiento no puede ser negativo" }

        gastos
            .sortedWith(
                compareByDescending<Gasto> { it.fechaHora }
                    .thenByDescending { it.id })
            .drop(offset)
            .take(limit)
    }
}