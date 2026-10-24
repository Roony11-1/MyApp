package io.github.roony11_1.myapp.core.services

import java.time.Instant

class SystemClock : IClock
{
    override fun now(): Long = Instant.now().toEpochMilli()
}