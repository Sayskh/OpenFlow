package org.openflow.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * Dispatcher abstraction layer for OpenFlow coroutine workflows.
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val render: CoroutineDispatcher
}

/**
 * Standard production dispatcher provider with dedicated single-threaded render dispatcher.
 */
class DefaultDispatcherProvider(
    override val main: CoroutineDispatcher = Dispatchers.Main.immediate,
    override val io: CoroutineDispatcher = Dispatchers.IO,
    override val default: CoroutineDispatcher = Dispatchers.Default,
    override val render: CoroutineDispatcher = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "OpenFlow-RenderThread").apply {
            isDaemon = true
            priority = Thread.MAX_PRIORITY
        }
    }.asCoroutineDispatcher()
) : DispatcherProvider

/**
 * Test dispatcher provider allowing deterministic dispatcher overrides.
 */
class TestDispatcherProvider(
    override val main: CoroutineDispatcher = Dispatchers.Unconfined,
    override val io: CoroutineDispatcher = Dispatchers.Unconfined,
    override val default: CoroutineDispatcher = Dispatchers.Unconfined,
    override val render: CoroutineDispatcher = Dispatchers.Unconfined
) : DispatcherProvider
