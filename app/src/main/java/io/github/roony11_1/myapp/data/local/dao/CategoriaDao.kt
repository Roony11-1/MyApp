package io.github.roony11_1.myapp.data.local.dao

import android.content.ContentValues
import io.github.roony11_1.myapp.data.local.db.GastosDatabaseHelper
import io.github.roony11_1.myapp.domain.model.Categoria
import io.github.roony11_1.myapp.domain.model.normalizarNombre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


interface ICategoriaDao
{
    suspend fun selectAll(): List<Categoria>
    suspend fun crear(nombre: String): Categoria
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

    override suspend fun crear(nombre: String): Categoria =
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
                    FROM categoria
                    WHERE nombre = ? COLLATE NOCASE
                    LIMIT 1
                """.trimIndent(),
                    arrayOf(normalizado)).use { cursor ->
                    if (cursor.moveToFirst())
                        Categoria(
                            id = cursor.getLong(0),
                            nombre = cursor.getString(1))
                    else
                        null
                }

                val categoria = existente ?: run {
                    val values = ContentValues().apply {
                        put("nombre", normalizado)
                    }

                    val id = db.insertOrThrow(
                        "categoria",
                        null,
                        values)

                    Categoria(id = id, nombre = normalizado)
                }

                db.setTransactionSuccessful()
                categoria
            }
            finally
            {
                db.endTransaction()
            }
        }
}
