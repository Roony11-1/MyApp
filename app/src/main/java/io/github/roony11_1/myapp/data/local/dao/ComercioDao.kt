package io.github.roony11_1.myapp.data.local.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import io.github.roony11_1.myapp.data.local.db.GastosDatabaseHelper
import io.github.roony11_1.myapp.domain.model.Comercio
import io.github.roony11_1.myapp.domain.model.normalizarNombre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface IComercioDao
{
    suspend fun selectAll(): List<Comercio>
    suspend fun recomendar(query: String, limit: Int = 8): List<Comercio>
    suspend fun crear(nombre: String): Comercio
    fun obtenerOCrear(db: SQLiteDatabase, nombre: String): Long
}

class SqliteComercioDao(
    private val helper: GastosDatabaseHelper) : IComercioDao
{
    override suspend fun selectAll(): List<Comercio> =
        withContext(Dispatchers.IO)
        {
            helper.readableDatabase.rawQuery(
                """
                SELECT id, nombre
                FROM comercio
                ORDER BY nombre
            """.trimIndent(),
                null).use { cursor ->
                buildList {
                    while (cursor.moveToNext())
                    {
                        add(
                            Comercio(
                                id = cursor.getLong(0),
                                nombre = cursor.getString(1)))
                    }
                }
            }
        }

    override suspend fun recomendar(
        query: String,
        limit: Int): List<Comercio> =
        withContext(Dispatchers.IO)
        {
            helper.readableDatabase.rawQuery(
                """
                SELECT id, nombre
                FROM comercio
                WHERE clave_normalizada LIKE ? || '%'
                ORDER BY nombre
                LIMIT ?
            """.trimIndent(),
                arrayOf(query, limit.toString())).use { cursor ->
                buildList {
                    while (cursor.moveToNext())
                    {
                        add(
                            Comercio(
                                id = cursor.getLong(0),
                                nombre = cursor.getString(1)))
                    }
                }
            }
        }

    override suspend fun crear(nombre: String): Comercio =
        withContext(Dispatchers.IO)
        {
            val db = helper.writableDatabase

            db.beginTransaction()
            try
            {
                val id = obtenerOCrear(db, nombre)
                val comercio = leerPorId(db, id)
                    ?: error("No se pudo crear el comercio")

                db.setTransactionSuccessful()
                comercio
            }
            finally
            {
                db.endTransaction()
            }
        }

    override fun obtenerOCrear(db: SQLiteDatabase, nombre: String): Long
    {
        val normalizado = normalizarNombre(nombre)
        require(normalizado.isNotEmpty()) { "El nombre es obligatorio" }

        db.rawQuery(
            "SELECT id FROM comercio WHERE clave_normalizada = ? LIMIT 1",
            arrayOf(normalizado)).use { cursor ->
            if (cursor.moveToFirst())
                return cursor.getLong(0)
        }

        val values = ContentValues().apply {
            put("nombre", normalizado)
            put("clave_normalizada", normalizado)
        }

        val id = db.insertWithOnConflict(
            "comercio",
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE)

        if (id != -1L)
            return id

        return db.rawQuery(
            "SELECT id FROM comercio WHERE clave_normalizada = ? LIMIT 1",
            arrayOf(normalizado)).use { cursor ->
            if (cursor.moveToFirst())
                cursor.getLong(0)
            else
                error("No se pudo crear el comercio")
        }
    }

    private fun leerPorId(db: SQLiteDatabase, id: Long): Comercio? =
        db.rawQuery(
            "SELECT id, nombre FROM comercio WHERE id = ? LIMIT 1",
            arrayOf(id.toString())).use { cursor ->
            if (cursor.moveToFirst())
                Comercio(
                    id = cursor.getLong(0),
                    nombre = cursor.getString(1))
            else
                null
        }
}
