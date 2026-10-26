package io.github.roony11_1.myapp.data.local.dao

import io.github.roony11_1.myapp.data.local.db.GastosDatabaseHelper
import io.github.roony11_1.myapp.domain.model.Cuenta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ICuentaDao
{
    suspend fun selectAll(): List<Cuenta>
}

class SqliteCuentaDao(private val helper: GastosDatabaseHelper) : ICuentaDao
{
    override suspend fun selectAll(): List<Cuenta> =
        withContext(Dispatchers.IO)
        {
            helper.readableDatabase.rawQuery(
                """
                SELECT id, nombre
                FROM cuenta
                ORDER BY id
            """.trimIndent(),
                null).use { cursor ->
                buildList {
                    while (cursor.moveToNext())
                    {
                        add(
                            Cuenta(
                                id = cursor.getLong(0),
                                nombre = cursor.getString(1)))
                    }
                }
            }
        }
}