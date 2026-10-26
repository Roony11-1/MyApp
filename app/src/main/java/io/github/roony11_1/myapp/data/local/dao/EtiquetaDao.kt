package io.github.roony11_1.myapp.data.local.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import io.github.roony11_1.myapp.data.local.db.GastosDatabaseHelper
import io.github.roony11_1.myapp.domain.model.Etiqueta
import io.github.roony11_1.myapp.domain.model.normalizarNombre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface IEtiquetaDao
{
    suspend fun recomendar(query: String, limit: Int = 8): List<Etiqueta>
    suspend fun crear(nombre: String): Etiqueta
    fun obtenerOCrear(db: SQLiteDatabase, nombre: String): Long
}

class SqliteEtiquetaDao(
    private val helper: GastosDatabaseHelper) : IEtiquetaDao
{
    override suspend fun recomendar(
        query: String,
        limit: Int): List<Etiqueta> =
        withContext(Dispatchers.IO)
        {
            helper.readableDatabase.rawQuery(
                """
                SELECT id, nombre
                FROM etiqueta
                WHERE clave_normalizada LIKE ? || '%'
                ORDER BY nombre
                LIMIT ?
            """.trimIndent(),
                arrayOf(query, limit.toString())).use { cursor ->
                buildList {
                    while (cursor.moveToNext())
                    {
                        add(
                            Etiqueta(
                                id = cursor.getLong(0),
                                nombre = cursor.getString(1)))
                    }
                }
            }
        }

    override suspend fun crear(nombre: String): Etiqueta =
        withContext(Dispatchers.IO)
        {
            val db = helper.writableDatabase

            db.beginTransaction()
            try
            {
                val id = obtenerOCrear(db, nombre)
                val etiqueta = leerPorId(db, id)
                    ?: error("No se pudo crear la etiqueta")

                db.setTransactionSuccessful()
                etiqueta
            }
            finally
            {
                db.endTransaction()
            }
        }

    override fun obtenerOCrear(db: SQLiteDatabase, nombre: String): Long
    {
        val normalizado = normalizarNombre(nombre)
        require(normalizado.isNotEmpty()) { "La etiqueta no puede estar vacía" }

        db.rawQuery(
            "SELECT id FROM etiqueta WHERE clave_normalizada = ? LIMIT 1",
            arrayOf(normalizado)).use { cursor ->
            if (cursor.moveToFirst())
                return cursor.getLong(0)
        }

        val values = ContentValues().apply {
            put("nombre", normalizado)
            put("clave_normalizada", normalizado)
        }

        val id = db.insertWithOnConflict(
            "etiqueta",
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE)

        if (id != -1L)
            return id

        return db.rawQuery(
            "SELECT id FROM etiqueta WHERE clave_normalizada = ? LIMIT 1",
            arrayOf(normalizado)).use { cursor ->
            if (cursor.moveToFirst())
                cursor.getLong(0)
            else
                error("No se pudo crear la etiqueta")
        }
    }

    private fun leerPorId(db: SQLiteDatabase, id: Long): Etiqueta? =
        db.rawQuery(
            "SELECT id, nombre FROM etiqueta WHERE id = ? LIMIT 1",
            arrayOf(id.toString())).use { cursor ->
            if (cursor.moveToFirst())
                Etiqueta(
                    id = cursor.getLong(0),
                    nombre = cursor.getString(1))
            else
                null
        }
}
