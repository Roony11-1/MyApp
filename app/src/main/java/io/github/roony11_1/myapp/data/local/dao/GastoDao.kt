package io.github.roony11_1.myapp.data.local.dao

import io.github.roony11_1.myapp.core.pagination.Page
import io.github.roony11_1.myapp.domain.model.Gasto
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface IGastoDao
{
    suspend fun insert(gasto: Gasto): Long
    suspend fun selectAll(): List<Gasto>
    suspend fun selectById(id: Long): Gasto?
    suspend fun selectPage(limit: Int, offset: Int): Page<Gasto>
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
        number: Int,
        size: Int): Page<Gasto> = mutex.withLock {
        require(number >= 0) { "El número de página no puede ser negativo" }
        require(size > 0) { "El tamaño de página debe ser positivo" }

        val totalElements = gastos.size.toLong()
        val offset = number.toLong() * size

        val content = if (offset >= totalElements)
        {
            emptyList()
        }
        else
        {
            gastos
                .sortedWith(
                    compareByDescending<Gasto> { it.fechaHora }
                        .thenByDescending { it.id })
                .drop(offset.toInt())
                .take(size)
        }

        Page(
            content = content,
            totalElements = totalElements,
            totalPages = ((totalElements + size - 1) / size).toInt(),
            number = number,
            size = size)
    }
}