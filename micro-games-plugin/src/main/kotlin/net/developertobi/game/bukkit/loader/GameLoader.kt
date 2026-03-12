package net.developertobi.game.bukkit.loader

import net.developertobi.game.api.game.MicroGame
import net.developertobi.game.api.game.getProperties
import org.bukkit.plugin.java.JavaPlugin
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.ServiceLoader

/**
 * Loads Micro Games from JAR files via ClassLoader.
 *
 * Scans the `games` folder in the plugin data folder for JAR files,
 * creates a URLClassLoader per JAR, and discovers implementations via ServiceLoader.
 */
class GameLoader(private val plugin: JavaPlugin) {

    private val loadedJars = mutableListOf<LoadedJar>()

    val loadedGames: List<MicroGame>
        get() = loadedJars.flatMap { it.games }

    fun loadAll() {
        val gamesDir = plugin.dataFolder.toPath().resolve("games")
        if (!Files.exists(gamesDir)) {
            Files.createDirectories(gamesDir)
            plugin.logger.info("Created games directory: $gamesDir")
            return
        }

        Files.list(gamesDir)
            .filter { it.toString().endsWith(".jar") }
            .forEach { jarPath ->
                loadJar(jarPath)
            }
    }

    fun unloadAll() {
        loadedJars.forEach { loaded ->
            loaded.games.forEach { game ->
                try {
                    game.onUnload()
                } catch (e: Exception) {
                    plugin.logger.warning("Error unloading game ${game.getProperties().id}: ${e.message}")
                }
            }
            try {
                loaded.classLoader.close()
            } catch (e: Exception) {
                plugin.logger.warning("Error closing ClassLoader for ${loaded.jarPath}: ${e.message}")
            }
        }
        loadedJars.clear()
    }

    private fun loadJar(jarPath: Path) {
        val url = jarPath.toUri().toURL()
        val parentLoader = MicroGame::class.java.classLoader
        val classLoader = URLClassLoader(arrayOf(url), parentLoader)

        val loader = ServiceLoader.load(MicroGame::class.java, classLoader)
        val games = loader.toList()

        if (games.isEmpty()) {
            plugin.logger.warning("No MicroGame found in $jarPath (missing META-INF/services?)")
            classLoader.close()
            return
        }

        games.forEach { game ->
            try {
                game.onLoad()
                plugin.logger.info("Loaded game: ${game.getProperties().name} (${game.getProperties().id})")
            } catch (e: Exception) {
                plugin.logger.warning("Error loading game from $jarPath: ${e.message}")
            }
        }
        loadedJars.add(LoadedJar(jarPath, classLoader, games))
    }

    private data class LoadedJar(
        val jarPath: Path,
        val classLoader: URLClassLoader,
        val games: List<MicroGame>,
    )
}
