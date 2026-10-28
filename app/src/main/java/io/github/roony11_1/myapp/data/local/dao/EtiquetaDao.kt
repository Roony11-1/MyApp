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
    suspend fun selectAll(): List<Etiqueta>
    suspend fun selectActivas(): List<Etiqueta>
    suspend fun recomendar(query: String, limit: Int = 8): List<Etiqueta>
    suspend fun crear(nombre: String): Etiqueta
    suspend fun cambiarActiva(id: Long, activa: Boolean): Boolean
    fun obtenerOCrear(db: SQLiteDatabase, nombre: String): Long
}

class SqliteEtiquetaDao(
    private val helper: GastosDatabaseHelper) : IEtiquetaDao
{
    override suspend fun selectAll(): List<Etiqueta> = leerEtiquetas(soloActivas = false)

    override suspend fun selectActivas(): List<Etiqueta> = leerEtiquetas(soloActivas = true)

    private suspend fun leerEtiquetas(soloActivas: Boolean): List<Etiqueta> =
        withContext(Dispatchers.IO)
        {
            val filtro = if (soloActivas) "WHERE activa = 1" else ""

            helper.readableDatabase.rawQuery(
                """
                SELECT id, nombre, activa
                FROM etiqueta
                $filtro
                ORDER BY nombre
            """.trimIndent(),
                null).use { cursor ->
                buildList {
                    while (cursor.moveToNext())
                    {
                        add(
                            Etiqueta(
                                id = cursor.getLong(0),
                                nombre = cursor.getString(1),
                                activa = cursor.getInt(2) == 1))
                    }
                }
            }
        }

    override suspend fun recomendar(
        query: String,
        limit: Int): List<Etiqueta> =
        withContext(Dispatchers.IO)
        {
            helper.readableDatabase.rawQuery(
                """
                SELECT id, nombre, activa
                FROM etiqueta
                WHERE activa = 1
                  AND clave_normalizada LIKE ? || '%'
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
                                nombre = cursor.getString(1),
                                activa = cursor.getInt(2) == 1))
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

    override suspend fun cambiarActiva(
        id: Long,
        activa: Boolean): Boolean = withContext(Dispatchers.IO)
    {
        val values = ContentValues().apply {
            put("activa", if (activa) 1 else 0)
        }

        helper.writableDatabase.update(
            "etiqueta",
            values,
            "id = ?",
            arrayOf(id.toString())) > 0
    }

    override fun obtenerOCrear(db: SQLiteDatabase, nombre: String): Long
    {
        val normalizado = normalizarNombre(nombre)
        require(normalizado.isNotEmpty()) { "La etiqueta no puede estar vacía" }

        val existente = db.rawQuery(
            "SELECT id FROM etiqueta WHERE clave_normalizada = ? LIMIT 1",
            arrayOf(normalizado)).use { cursor ->
            if (cursor.moveToFirst())
                cursor.getLong(0)
            else
                null
        }

        if (existente != null)
        {
            db.execSQL(
                "UPDATE etiqueta SET activa = 1 WHERE id = ?",
                arrayOf(existente))

            return existente
        }

        val values = ContentValues().apply {
            put("nombre", normalizado)
            put("clave_normalizada", normalizado)
            put("activa", 1)
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
            {
                val existenteId = cursor.getLong(0)
                db.execSQL(
                    "UPDATE etiqueta SET activa = 1 WHERE id = ?",
                    arrayOf(existenteId))
                existenteId
            }
            else
            {
                error("No se pudo crear la etiqueta")
            }
        }
    }

    private fun leerPorId(db: SQLiteDatabase, id: Long): Etiqueta? =
        db.rawQuery(
            "SELECT id, nombre, activa FROM etiqueta WHERE id = ? LIMIT 1",
            arrayOf(id.toString())).use { cursor ->
            if (cursor.moveToFirst())
                Etiqueta(
                    id = cursor.getLong(0),
                    nombre = cursor.getString(1),
                    activa = cursor.getInt(2) == 1)
            else
                null
        }
}
