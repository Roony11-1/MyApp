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
    suspend fun selectActivas(): List<Cuenta>
    suspend fun crear(nombre: String): Cuenta
    suspend fun cambiarActiva(id: Long, activa: Boolean): Boolean
}

class SqliteCuentaDao(private val helper: GastosDatabaseHelper) : ICuentaDao
{
    override suspend fun selectAll(): List<Cuenta> = leerCuentas(soloActivas = false)

    override suspend fun selectActivas(): List<Cuenta> = leerCuentas(soloActivas = true)

    private suspend fun leerCuentas(soloActivas: Boolean): List<Cuenta> =
        withContext(Dispatchers.IO)
        {
            val filtro = if (soloActivas) "WHERE activa = 1" else ""

            helper.readableDatabase.rawQuery(
                """
                SELECT id, nombre, activa
                FROM cuenta
                $filtro
                ORDER BY id
            """.trimIndent(),
                null).use { cursor ->
                buildList {
                    while (cursor.moveToNext())
                    {
                        add(
                            Cuenta(
                                id = cursor.getLong(0),
                                nombre = cursor.getString(1),
                                activa = cursor.getInt(2) == 1))
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
                    SELECT id, nombre, activa
                    FROM cuenta
                    WHERE nombre = ? COLLATE NOCASE
                    LIMIT 1
                """.trimIndent(),
                    arrayOf(normalizado)).use { cursor ->
                    if (cursor.moveToFirst())
                        Cuenta(
                            id = cursor.getLong(0),
                            nombre = cursor.getString(1),
                            activa = cursor.getInt(2) == 1)
                    else
                        null
                }

                val cuenta = existente?.let { actual ->
                    if (actual.activa)
                    {
                        actual
                    }
                    else
                    {
                        db.execSQL(
                            "UPDATE cuenta SET activa = 1 WHERE id = ?",
                            arrayOf(actual.id))

                        actual.copy(activa = true)
                    }
                } ?: run {
                    val values = ContentValues().apply {
                        put("nombre", normalizado)
                        put("activa", 1)
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

    override suspend fun cambiarActiva(
        id: Long,
        activa: Boolean): Boolean = withContext(Dispatchers.IO)
    {
        val values = ContentValues().apply {
            put("activa", if (activa) 1 else 0)
        }

        helper.writableDatabase.update(
            "cuenta",
            values,
            "id = ?",
            arrayOf(id.toString())) > 0
    }
}
