package io.github.roony11_1.myapp.data.local.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import io.github.roony11_1.myapp.core.pagination.Page
import io.github.roony11_1.myapp.data.local.db.GastosDatabaseHelper
import io.github.roony11_1.myapp.domain.model.Categoria
import io.github.roony11_1.myapp.domain.model.Cuenta
import io.github.roony11_1.myapp.domain.model.Gasto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

interface IGastoDao
{
    suspend fun insert(gasto: Gasto): Long
    suspend fun selectAll(): List<Gasto>
    suspend fun selectById(id: Long): Gasto?
    suspend fun selectPage(limit: Int, offset: Int): Page<Gasto>
}

class SqliteGastoDao(
    private val helper: GastosDatabaseHelper) : IGastoDao
{

    override suspend fun insert(gasto: Gasto): Long =
        withContext(Dispatchers.IO)
        {
            val db = helper.writableDatabase

            db.beginTransaction()
            try
            {
                val values = ContentValues().apply {
                    put("monto", gasto.monto)
                    put("descripcion", gasto.descripcion)
                    put("cuenta_id", gasto.cuenta.id)
                    put("fecha_hora", gasto.fechaHora)
                }

                val id = db.insertOrThrow("gasto", null, values)

                gasto.categorias.forEach { categoria ->
                    val relacion = ContentValues().apply {
                        put("gasto_id", id)
                        put("categoria_id", categoria.id)
                    }

                    db.insertOrThrow(
                        "gasto_categoria",
                        null,
                        relacion)
                }

                db.setTransactionSuccessful()
                id
            }
            finally
            {
                db.endTransaction()
            }
        }

    override suspend fun selectAll(): List<Gasto> =
        withContext(Dispatchers.IO)
        {
            leerGastos(
                db = helper.readableDatabase,
                suffix = "ORDER BY g.fecha_hora DESC, g.id DESC")
        }

    override suspend fun selectById(id: Long): Gasto? =
        withContext(Dispatchers.IO)
        {
            leerGastos(
                db = helper.readableDatabase,
                suffix = "WHERE g.id = ?",
                args = arrayOf(id.toString())
            ).firstOrNull()
        }

    override suspend fun selectPage(
        limit: Int,
        offset: Int): Page<Gasto> = withContext(Dispatchers.IO)
    {
        require(limit >= 0) { "El número de página no puede ser negativo" }
        require(offset > 0) { "El tamaño de página debe ser positivo" }

        val db = helper.readableDatabase

        db.beginTransaction()
        try
        {
            val totalElements = db.rawQuery(
                "SELECT COUNT(*) FROM gasto",
                null).use { cursor ->
                cursor.moveToFirst()
                cursor.getLong(0)
            }

            val content = leerGastos(
                db = db,
                suffix = """
                    ORDER BY g.fecha_hora DESC, g.id DESC
                    LIMIT ? OFFSET ?
                """.trimIndent(),
                args = arrayOf(
                    offset.toString(),
                    (limit.toLong() * offset).toString()))

            val page = Page(
                content = content,
                totalElements = totalElements,
                totalPages = (
                        (totalElements + offset - 1) / offset).toInt(),
                number = limit,
                size = offset)

            db.setTransactionSuccessful()
            page
        }
        finally
        {
            db.endTransaction()
        }
    }

    private fun leerGastos(
        db: SQLiteDatabase,
        suffix: String,
        args: Array<String>? = null): List<Gasto>
    {
        val filas = db.rawQuery(
            """
                SELECT
                    g.id,
                    g.monto,
                    g.descripcion,
                    g.fecha_hora,
                    c.id AS cuenta_id,
                    c.nombre AS cuenta_nombre
                FROM gasto g
                INNER JOIN cuenta c ON c.id = g.cuenta_id
                $suffix
            """.trimIndent(),
            args).use { cursor ->
            buildList {
                while (cursor.moveToNext())
                {
                    add(
                        FilaGasto(
                            id = cursor.getLong(0),
                            monto = cursor.getDouble(1),
                            descripcion = cursor.getString(2),
                            fechaHora = cursor.getLong(3),
                            cuenta = Cuenta(
                                id = cursor.getLong(4),
                                nombre = cursor.getString(5))))
                }
            }
        }

        val categoriasPorGasto = leerCategorias(
            db,
            filas.map { it.id })

        return filas.map { fila ->
            Gasto(
                id = fila.id,
                monto = fila.monto,
                descripcion = fila.descripcion,
                cuenta = fila.cuenta,
                categorias = categoriasPorGasto[fila.id].orEmpty(),
                fechaHora = fila.fechaHora)
        }
    }

    private fun leerCategorias(
        db: SQLiteDatabase,
        gastosIds: List<Long>): Map<Long, List<Categoria>>
    {
        val resultado = mutableMapOf<Long, MutableList<Categoria>>()

        gastosIds.chunked(500).forEach { lote ->
            val placeholders = lote.joinToString(",") { "?" }

            db.rawQuery(
                """
                    SELECT gc.gasto_id, c.id, c.nombre
                    FROM gasto_categoria gc
                    INNER JOIN categoria c ON c.id = gc.categoria_id
                    WHERE gc.gasto_id IN ($placeholders)
                    ORDER BY gc.gasto_id, c.id
                """.trimIndent(),
                lote.map { it.toString() }.toTypedArray()).use { cursor ->
                while (cursor.moveToNext())
                {
                    val gastoId = cursor.getLong(0)
                    val categoria = Categoria(
                        id = cursor.getLong(1),
                        nombre = cursor.getString(2))

                    resultado.getOrPut(gastoId) {
                        mutableListOf()
                    }.add(categoria)
                }
            }
        }

        return resultado
    }

    private data class FilaGasto(
        val id: Long,
        val monto: Double,
        val descripcion: String,
        val fechaHora: Long,
        val cuenta: Cuenta)
}

/*class InMemoryGastoDao : IGastoDao
{
    private val mutex = Mutex()
    private val gastos = mutableListOf<Gasto>()
    private var nextId = 1L

    override suspend fun insert(gasto: Gasto): Long = mutex.withLock {
        val id = nextId++

        gastos.add(
            Gasto(
                id = id,
                monto = gasto.monto,
                descripcion = gasto.descripcion,
                cuenta = gasto.cuenta,
                categorias = gasto.categorias,
                fechaHora = gasto.fechaHora))
        id
    }

    override suspend fun selectAll(): List<Gasto> = mutex.withLock {
        gastos.toList()
    }

    override suspend fun selectById(id: Long): Gasto? = mutex.withLock {
        gastos.firstOrNull { it.id == id }
    }

    override suspend fun selectPage(
        number: Int,
        size: Int): Page<Gasto> = mutex.withLock {
        require(number >= 0) { "El número de página no puede ser negativo" }
        require(size > 0) { "El tamaño de página debe ser positivo" }

        val totalElements = gastos.size.toLong()
        val offset = number.toLong() * size

        val content = if (offset >= totalElements)
        {
            emptyList()
        }
        else
        {
            gastos
                .sortedWith(
                    compareByDescending<Gasto> { it.fechaHora }
                        .thenByDescending { it.id })
                .drop(offset.toInt())
                .take(size)
        }

        Page(
            content = content,
            totalElements = totalElements,
            totalPages = ((totalElements + size - 1) / size).toInt(),
            number = number,
            size = size)
    }
}*/