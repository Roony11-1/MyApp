package io.github.roony11_1.myapp.data.local.dao

import android.content.ContentValues
import io.github.roony11_1.myapp.data.local.db.GastosDatabaseHelper
import io.github.roony11_1.myapp.domain.model.Cuenta
import io.github.roony11_1.myapp.domain.model.normalizarNombre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ICuentaDao
{
    suspend fun selectAll(): List<Cuenta>
    suspend fun crear(nombre: String): Cuenta
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

    override suspend fun crear(nombre: String): Cuenta =
        withContext(Dispatchers.IO)
        {
            val normalizado = normalizarNombre(nombre)
            require(normalizado.isNotEmpty()) { "El nombre es obligatorio" }

            val db = helper.writableDatabase

            db.beginTransaction()
            try
            {
                val existente = db.rawQuery(
                    """
                    SELECT id, nombre
                    FROM cuenta
                    WHERE nombre = ? COLLATE NOCASE
                    LIMIT 1
                """.trimIndent(),
                    arrayOf(normalizado)).use { cursor ->
                    if (cursor.moveToFirst())
                        Cuenta(
                            id = cursor.getLong(0),
                            nombre = cursor.getString(1))
                    else
                        null
                }

                val cuenta = existente ?: run {
                    val values = ContentValues().apply {
                        put("nombre", normalizado)
                    }

                    val id = db.insertOrThrow(
                        "cuenta",
                        null,
                        values)

                    Cuenta(id = id, nombre = normalizado)
                }

                db.setTransactionSuccessful()
                cuenta
            }
            finally
            {
                db.endTransaction()
            }
        }
}
