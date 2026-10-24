
package io.github.roony11_1.myapp.data.local.repository

import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.repository.IGastoRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryGastoRepository : IGastoRepository
{
    private val mutex = Mutex()
    private val gastos = mutableListOf<Gasto>()
    private var nextId = 1L

    override suspend fun save(gasto: Gasto): Gasto = mutex.withLock {
        val nuevo = gasto.copy(id = nextId++)
        gastos.add(nuevo)
        nuevo
    }

    override suspend fun findAll(): List<Gasto> = mutex.withLock {
        gastos.toList()
    }

    override suspend fun findById(id: Long): Gasto? = mutex.withLock {
        gastos.firstOrNull { it.id == id }
    }
}