package io.github.roony11_1.myapp.data.local.dao

import io.github.roony11_1.myapp.domain.model.Gasto
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface IGastoDao
{
    suspend fun insert(gasto: Gasto): Long
    suspend fun selectAll(): List<Gasto>
    suspend fun selectById(id: Long): Gasto?
}

class InMemoryGastoDao : IGastoDao
{
    private val mutex = Mutex()
    private val gastos = mutableListOf<Gasto>()
    private var nextId = 1L

    override suspend fun insert(gasto: Gasto): Long = mutex.withLock {
        val id = nextId++
        gastos.add(gasto.copy(id = id))
        id
    }

    override suspend fun selectAll(): List<Gasto> = mutex.withLock {
        gastos.toList()
    }

    override suspend fun selectById(id: Long): Gasto? = mutex.withLock {
        gastos.firstOrNull { it.id == id }
    }
}