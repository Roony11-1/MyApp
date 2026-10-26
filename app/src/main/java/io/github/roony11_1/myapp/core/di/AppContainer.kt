package io.github.roony11_1.myapp.core.di

import android.content.Context
import io.github.roony11_1.myapp.core.services.IClock
import io.github.roony11_1.myapp.core.services.SystemClock
import io.github.roony11_1.myapp.data.local.dao.ICategoriaDao
import io.github.roony11_1.myapp.data.local.dao.ICuentaDao
import io.github.roony11_1.myapp.data.local.dao.IGastoDao
import io.github.roony11_1.myapp.data.local.dao.SqliteCategoriaDao
import io.github.roony11_1.myapp.data.local.dao.SqliteCuentaDao
import io.github.roony11_1.myapp.data.local.dao.SqliteGastoDao
import io.github.roony11_1.myapp.data.local.db.GastosDatabaseHelper
import io.github.roony11_1.myapp.data.local.repository.GastoRepository
import io.github.roony11_1.myapp.domain.repository.CategoriaRepository
import io.github.roony11_1.myapp.domain.repository.CuentaRepository
import io.github.roony11_1.myapp.domain.repository.ICategoriaRepository
import io.github.roony11_1.myapp.domain.repository.ICuentaRepository
import io.github.roony11_1.myapp.domain.repository.IGastoRepository

class AppContainer(context: Context)
{
    private val appContext = context.applicationContext
    val clock: IClock by lazy { SystemClock() }
    private val databaseHelper: GastosDatabaseHelper by lazy {
        GastosDatabaseHelper(appContext)
    }
    private val gastoDao: IGastoDao by lazy { SqliteGastoDao(databaseHelper) }
    private val categoriaDao: ICategoriaDao by lazy { SqliteCategoriaDao(databaseHelper) }
    private val cuentaDao: ICuentaDao by lazy { SqliteCuentaDao(databaseHelper) }
    val gastoRepository: IGastoRepository by lazy { GastoRepository(gastoDao) }
    val categoriaRepository: ICategoriaRepository by lazy { CategoriaRepository(categoriaDao) }
    val cuentaRepository: ICuentaRepository by lazy { CuentaRepository(cuentaDao) }
}