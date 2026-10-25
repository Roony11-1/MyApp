package io.github.roony11_1.myapp.data.local.dao

import io.github.roony11_1.myapp.data.local.db.GastosDatabaseHelper
import io.github.roony11_1.myapp.domain.model.Categoria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


interface ICategoriaDao
{
    suspend fun selectAll(): List<Categoria>
}

class SqliteCategoriaDao(private val helper: GastosDatabaseHelper) : ICategoriaDao
{
    override suspend fun selectAll(): List<Categoria> =
        withContext(Dispatchers.IO)
        {
            helper.readableDatabase.rawQuery(
                """
                SELECT id, nombre
                FROM categoria
                ORDER BY id
            """.trimIndent(),
                null).use { cursor ->
                buildList {
                    while (cursor.moveToNext())
                    {
                        add(
                            Categoria(
                                id = cursor.getLong(0),
                                nombre = cursor.getString(1)))
                    }
                }
            }
        }
}