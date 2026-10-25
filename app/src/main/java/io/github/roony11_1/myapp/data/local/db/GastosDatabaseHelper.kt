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
        TODO("Not yet implemented")
    }

    override fun onCreate(db: SQLiteDatabase)
    {
        db.execSQL("""
            CREATE TABLE cuenta (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL CHECK (length(trim(nombre)) > 0))
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE categoria (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL CHECK (length(trim(nombre)) > 0))
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE gasto (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                monto REAL NOT NULL CHECK (monto > 0),
                descripcion TEXT NOT NULL
                    CHECK (length(trim(descripcion)) > 0),
                cuenta_id INTEGER NOT NULL,
                fecha_hora INTEGER NOT NULL,
                FOREIGN KEY (cuenta_id) REFERENCES cuenta(id))
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE gasto_categoria (
                gasto_id INTEGER NOT NULL,
                categoria_id INTEGER NOT NULL,
                PRIMARY KEY (gasto_id, categoria_id),
                FOREIGN KEY (gasto_id) REFERENCES gasto(id)
                    ON DELETE CASCADE,
                FOREIGN KEY (categoria_id) REFERENCES categoria(id))
        """.trimIndent())

        db.execSQL("""
            CREATE INDEX idx_gasto_fecha_hora_id
            ON gasto (fecha_hora DESC, id DESC)
        """.trimIndent())
    }
    companion object
    {
        private const val DATABASE_NAME = "gastos.db"
        private const val DATABASE_VERSION = 1
    }
}