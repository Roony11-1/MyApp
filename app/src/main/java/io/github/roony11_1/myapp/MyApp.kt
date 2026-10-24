package io.github.roony11_1.myapp

import android.app.Application
import io.github.roony11_1.myapp.core.di.AppContainer

class MyApp : Application()
{
    lateinit var container: AppContainer
        private set

    override fun onCreate()
    {
        super.onCreate()

        container = AppContainer(this)
    }
}