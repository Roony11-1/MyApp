package io.github.roony11_1.myapp.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GastosDatabaseHelper(context: Context)
    : SQLiteOpenHelper(
        context.applicationContext,
        DATABASE_NAME,
        null,
        DATABASE_VERSION)
{
    override fun onConfigure(db: SQLiteDatabase)
    {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)
    {
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)
    {
    }

    override fun onCreate(db: SQLiteDatabase)
    {
        db.execSQL(CREAR_TABLA_CUENTA)
        db.execSQL(CREAR_TABLA_CATEGORIA)
        db.execSQL(CREAR_TABLA_COMERCIO)
        db.execSQL(CREAR_TABLA_ETIQUETA)
        db.execSQL(CREAR_TABLA_GASTO)
        db.execSQL(CREAR_TABLA_GASTO_CATEGORIA)
        db.execSQL(CREAR_TABLA_GASTO_ETIQUETA)
        db.execSQL(CREAR_INDICE_GASTO_FECHA)
        db.execSQL(CREAR_INDICE_GASTO_COMERCIO)
        db.execSQL(CREAR_INDICE_GASTO_ETIQUETA)
        db.execSQL(CREAR_INDICE_GASTO_ESTADO)

        db.execSQL(
            "INSERT INTO cuenta (id, nombre) VALUES (?, ?)",
            arrayOf<Any>(1L, "Cuenta por defecto")
        )

        val categorias = listOf(
            1L to "Cuentas",
            2L to "Alimentación",
            3L to "Gasto hormiga",
            4L to "Transporte"
        )

        categorias.forEach { (id, nombre) ->
            db.execSQL(
                "INSERT INTO categoria (id, nombre) VALUES (?, ?)",
                arrayOf<Any>(id, nombre))
        }
    }
    companion object
    {
        private const val DATABASE_NAME = "gastos.db"
        private const val DATABASE_VERSION = 1

        private val CREAR_TABLA_CUENTA = """
            CREATE TABLE cuenta (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL CHECK (length(trim(nombre)) > 0),
                activa INTEGER NOT NULL DEFAULT 1
                    CHECK (activa IN (0, 1)))
        """.trimIndent()

        private val CREAR_TABLA_CATEGORIA = """
            CREATE TABLE categoria (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL CHECK (length(trim(nombre)) > 0),
                activa INTEGER NOT NULL DEFAULT 1
                    CHECK (activa IN (0, 1)))
        """.trimIndent()

        private val CREAR_TABLA_COMERCIO = """
            CREATE TABLE comercio (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                clave_normalizada TEXT NOT NULL UNIQUE)
        """.trimIndent()

        private val CREAR_TABLA_ETIQUETA = """
            CREATE TABLE etiqueta (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                clave_normalizada TEXT NOT NULL UNIQUE,
                activa INTEGER NOT NULL DEFAULT 1
                    CHECK (activa IN (0, 1)))
        """.trimIndent()

        private val CREAR_TABLA_GASTO = """
            CREATE TABLE gasto (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                monto REAL NOT NULL CHECK (monto > 0),
                descripcion TEXT NOT NULL
                    CHECK (length(trim(descripcion)) > 0),
                cuenta_id INTEGER NOT NULL,
                fecha_hora INTEGER NOT NULL,
                comercio_id INTEGER NULL REFERENCES comercio(id),
                notas TEXT NULL,
                estado TEXT NOT NULL DEFAULT 'VIGENTE'
                    CHECK (estado IN ('VIGENTE', 'ANULADO')),
                FOREIGN KEY (cuenta_id) REFERENCES cuenta(id))
        """.trimIndent()

        private val CREAR_TABLA_GASTO_CATEGORIA = """
            CREATE TABLE gasto_categoria (
                gasto_id INTEGER NOT NULL,
                categoria_id INTEGER NOT NULL,
                PRIMARY KEY (gasto_id, categoria_id),
                FOREIGN KEY (gasto_id) REFERENCES gasto(id)
                    ON DELETE CASCADE,
                FOREIGN KEY (categoria_id) REFERENCES categoria(id))
        """.trimIndent()

        private val CREAR_TABLA_GASTO_ETIQUETA = """
            CREATE TABLE gasto_etiqueta (
                gasto_id INTEGER NOT NULL,
                etiqueta_id INTEGER NOT NULL,
                PRIMARY KEY (gasto_id, etiqueta_id),
                FOREIGN KEY (gasto_id) REFERENCES gasto(id)
                    ON DELETE CASCADE,
                FOREIGN KEY (etiqueta_id) REFERENCES etiqueta(id))
        """.trimIndent()

        private val CREAR_INDICE_GASTO_FECHA = """
            CREATE INDEX idx_gasto_fecha_hora_id
            ON gasto (fecha_hora DESC, id DESC)
        """.trimIndent()

        private val CREAR_INDICE_GASTO_COMERCIO = """
            CREATE INDEX idx_gasto_comercio
            ON gasto (comercio_id)
        """.trimIndent()

        private val CREAR_INDICE_GASTO_ETIQUETA = """
            CREATE INDEX idx_gasto_etiqueta_etiqueta
            ON gasto_etiqueta (etiqueta_id)
        """.trimIndent()

        private val CREAR_INDICE_GASTO_ESTADO = """
            CREATE INDEX idx_gasto_estado
            ON gasto (estado)
        """.trimIndent()
    }
}
