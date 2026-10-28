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
    suspend fun selectActivas(): List<Categoria>
    suspend fun crear(nombre: String): Categoria
    suspend fun cambiarActiva(id: Long, activa: Boolean): Boolean
}

class SqliteCategoriaDao(private val helper: GastosDatabaseHelper) : ICategoriaDao
{
    override suspend fun selectAll(): List<Categoria> = leerCategorias(soloActivas = false)

    override suspend fun selectActivas(): List<Categoria> = leerCategorias(soloActivas = true)

    private suspend fun leerCategorias(soloActivas: Boolean): List<Categoria> =
        withContext(Dispatchers.IO)
        {
            val filtro = if (soloActivas) "WHERE activa = 1" else ""

            helper.readableDatabase.rawQuery(
                """
                SELECT id, nombre, activa
                FROM categoria
                $filtro
                ORDER BY id
            """.trimIndent(),
                null).use { cursor ->
                buildList {
                    while (cursor.moveToNext())
                    {
                        add(
                            Categoria(
                                id = cursor.getLong(0),
                                nombre = cursor.getString(1),
                                activa = cursor.getInt(2) == 1))
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
                    SELECT id, nombre, activa
                    FROM categoria
                    WHERE nombre = ? COLLATE NOCASE
                    LIMIT 1
                """.trimIndent(),
                    arrayOf(normalizado)).use { cursor ->
                    if (cursor.moveToFirst())
                        Categoria(
                            id = cursor.getLong(0),
                            nombre = cursor.getString(1),
                            activa = cursor.getInt(2) == 1)
                    else
                        null
                }

                val categoria = existente?.let { actual ->
                    if (actual.activa)
                    {
                        actual
                    }
                    else
                    {
                        db.execSQL(
                            "UPDATE categoria SET activa = 1 WHERE id = ?",
                            arrayOf(actual.id))

                        actual.copy(activa = true)
                    }
                } ?: run {
                    val values = ContentValues().apply {
                        put("nombre", normalizado)
                        put("activa", 1)
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

    override suspend fun cambiarActiva(
        id: Long,
        activa: Boolean): Boolean = withContext(Dispatchers.IO)
    {
        val values = ContentValues().apply {
            put("activa", if (activa) 1 else 0)
        }

        helper.writableDatabase.update(
            "categoria",
            values,
            "id = ?",
            arrayOf(id.toString())) > 0
    }
}
