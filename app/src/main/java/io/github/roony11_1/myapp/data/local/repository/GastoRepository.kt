
package io.github.roony11_1.myapp.data.local.repository

import io.github.roony11_1.myapp.data.local.dao.IGastoDao
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.repository.IGastoRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GastoRepository(private val dao: IGastoDao) : IGastoRepository
{
    override suspend fun save(gasto: Gasto): Gasto
    {
       val id = dao.insert(gasto)

        return gasto.copy(id = id)
    }

    override suspend fun findAll(): List<Gasto> = dao.selectAll()

    override suspend fun findById(id: Long): Gasto? = dao.selectById(id)
}