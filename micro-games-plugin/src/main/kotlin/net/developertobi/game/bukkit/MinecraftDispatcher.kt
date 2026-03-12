package net.developertobi.game.bukkit

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import org.bukkit.plugin.java.JavaPlugin
import kotlin.coroutines.CoroutineContext

/**
 * CoroutineDispatcher that runs on the main Minecraft server thread.
 * Use with [kotlinx.coroutines.withContext] after awaiting async results to safely call Bukkit API.
 */
class MinecraftDispatcher(
    private val plugin: JavaPlugin,
) : CoroutineDispatcher() {

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        plugin.server.scheduler.runTask(plugin, block)
    }
}
