package io.github.roony11_1.myapp.data.local.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import io.github.roony11_1.myapp.core.pagination.Page
import io.github.roony11_1.myapp.data.local.db.GastosDatabaseHelper
import io.github.roony11_1.myapp.domain.model.Categoria
import io.github.roony11_1.myapp.domain.model.Comercio
import io.github.roony11_1.myapp.domain.model.Cuenta
import io.github.roony11_1.myapp.domain.model.EstadoGasto
import io.github.roony11_1.myapp.domain.model.Etiqueta
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.model.normalizarNombre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface IGastoDao
{
    suspend fun insert(gasto: Gasto): Long
    suspend fun selectAll(): List<Gasto>
    suspend fun selectById(id: Long): Gasto?
    suspend fun selectPage(number: Int, size: Int): Page<Gasto>
    suspend fun buscarSimilares(descripcion: String, limite: Int = 5): List<Gasto>
    suspend fun anular(id: Long): Boolean
}

class SqliteGastoDao(
    private val helper: GastosDatabaseHelper,
    private val comercioDao: IComercioDao,
    private val etiquetaDao: IEtiquetaDao) : IGastoDao
{

    override suspend fun insert(gasto: Gasto): Long =
        withContext(Dispatchers.IO)
        {
            val db = helper.writableDatabase

            db.beginTransaction()
            try
            {
                val comercioId = gasto.comercio?.let {
                    comercioDao.obtenerOCrear(db, it.nombre)
                }

                val values = ContentValues().apply {
                    put("monto", gasto.monto)
                    put("descripcion", gasto.descripcion)
                    put("cuenta_id", gasto.cuenta.id)
                    put("fecha_hora", gasto.fechaHora)

                    if (comercioId != null)
                        put("comercio_id", comercioId)
                    else
                        putNull("comercio_id")

                    val notas = gasto.notasLimpia
                    if (notas != null)
                        put("notas", notas)
                    else
                        putNull("notas")

                    put("estado", gasto.estado.name)
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

                gasto.etiquetas.forEach { etiqueta ->
                    val etiquetaId = etiquetaDao.obtenerOCrear(
                        db,
                        etiqueta.nombre)

                    val relacion = ContentValues().apply {
                        put("gasto_id", id)
                        put("etiqueta_id", etiquetaId)
                    }

                    db.insertWithOnConflict(
                        "gasto_etiqueta",
                        null,
                        relacion,
                        SQLiteDatabase.CONFLICT_IGNORE)
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
        number: Int,
        size: Int): Page<Gasto> = withContext(Dispatchers.IO)
    {
        require(number >= 0) { "El número de página no puede ser negativo" }
        require(size > 0) { "El tamaño de página debe ser positivo" }

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
                    size.toString(),
                    (number.toLong() * size).toString()))

            val page = Page(
                content = content,
                totalElements = totalElements,
                totalPages = (
                        (totalElements + size - 1) / size).toInt(),
                number = number,
                size = size)

            db.setTransactionSuccessful()
            page
        }
        finally
        {
            db.endTransaction()
        }
    }

    override suspend fun buscarSimilares(
        descripcion: String,
        limite: Int): List<Gasto> = withContext(Dispatchers.IO)
    {
        if (limite <= 0)
            return@withContext emptyList()

        val tokens = normalizarNombre(descripcion)
            .split(' ')
            .filter { it.isNotBlank() }

        if (tokens.isEmpty())
            return@withContext emptyList()

        val condiciones = tokens.joinToString(" AND ") {
            "g.descripcion LIKE ? ESCAPE '\\'"
        }

        val args = (
                listOf(EstadoGasto.VIGENTE.name) +
                        tokens.map { "%${escaparLike(it)}%" } +
                        limite.toString()).toTypedArray()

        leerGastos(
            db = helper.readableDatabase,
            suffix = """
                WHERE g.estado = ? AND $condiciones
                ORDER BY g.fecha_hora DESC, g.id DESC
                LIMIT ?
            """.trimIndent(),
            args = args)
    }

    override suspend fun anular(id: Long): Boolean =
        withContext(Dispatchers.IO)
        {
            val values = ContentValues().apply {
                put("estado", EstadoGasto.ANULADO.name)
            }

            helper.writableDatabase.update(
                "gasto",
                values,
                "id = ? AND estado = ?",
                arrayOf(id.toString(), EstadoGasto.VIGENTE.name)) > 0
        }

    private fun escaparLike(texto: String): String =
        texto
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")

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
                    c.nombre AS cuenta_nombre,
                    g.notas,
                    m.id AS comercio_id,
                    m.nombre AS comercio_nombre,
                    g.estado
                FROM gasto g
                INNER JOIN cuenta c ON c.id = g.cuenta_id
                LEFT JOIN comercio m ON m.id = g.comercio_id
                $suffix
            """.trimIndent(),
            args).use { cursor ->
            buildList {
                while (cursor.moveToNext())
                {
                    val comercio = if (cursor.isNull(7))
                    {
                        null
                    }
                    else
                    {
                        Comercio(
                            id = cursor.getLong(7),
                            nombre = cursor.getString(8))
                    }

                    add(
                        FilaGasto(
                            id = cursor.getLong(0),
                            monto = cursor.getDouble(1),
                            descripcion = cursor.getString(2),
                            fechaHora = cursor.getLong(3),
                            cuenta = Cuenta(
                                id = cursor.getLong(4),
                                nombre = cursor.getString(5)),
                            notas = if (cursor.isNull(6))
                                null
                            else
                                cursor.getString(6),
                            comercio = comercio,
                            estado = when (cursor.getString(9))
                            {
                                EstadoGasto.ANULADO.name -> EstadoGasto.ANULADO
                                else -> EstadoGasto.VIGENTE
                            }))
                }
            }
        }

        val categoriasPorGasto = leerCategorias(
            db,
            filas.map { it.id })

        val etiquetasPorGasto = leerEtiquetas(
            db,
            filas.map { it.id })

        return filas.map { fila ->
            Gasto(
                id = fila.id,
                monto = fila.monto,
                descripcion = fila.descripcion,
                cuenta = fila.cuenta,
                categorias = categoriasPorGasto[fila.id].orEmpty(),
                fechaHora = fila.fechaHora,
                comercio = fila.comercio,
                etiquetas = etiquetasPorGasto[fila.id].orEmpty(),
                notas = fila.notas,
                estado = fila.estado)
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

    private fun leerEtiquetas(
        db: SQLiteDatabase,
        gastosIds: List<Long>): Map<Long, List<Etiqueta>>
    {
        val resultado = mutableMapOf<Long, MutableList<Etiqueta>>()

        gastosIds.chunked(500).forEach { lote ->
            val placeholders = lote.joinToString(",") { "?" }

            db.rawQuery(
                """
                    SELECT ge.gasto_id, e.id, e.nombre
                    FROM gasto_etiqueta ge
                    INNER JOIN etiqueta e ON e.id = ge.etiqueta_id
                    WHERE ge.gasto_id IN ($placeholders)
                    ORDER BY ge.gasto_id, e.id
                """.trimIndent(),
                lote.map { it.toString() }.toTypedArray()).use { cursor ->
                while (cursor.moveToNext())
                {
                    val gastoId = cursor.getLong(0)
                    val etiqueta = Etiqueta(
                        id = cursor.getLong(1),
                        nombre = cursor.getString(2))

                    resultado.getOrPut(gastoId) {
                        mutableListOf()
                    }.add(etiqueta)
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
        val cuenta: Cuenta,
        val notas: String?,
        val comercio: Comercio?,
        val estado: EstadoGasto)
}
