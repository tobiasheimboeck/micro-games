package net.developertobi.game.bukkit.command.subcommand

import kotlinx.coroutines.runBlocking
import net.developertobi.game.api.MicroGamesProvider
import net.developertobi.game.api.arena.ArenaId
import net.developertobi.game.api.arena.ArenaManager
import net.developertobi.game.bukkit.localization.LangKeys
import net.developertobi.mclib.api.McLibProvider
import net.developertobi.mclib.api.command.McCommandSender
import net.developertobi.mclib.api.command.extension.findArgument
import net.developertobi.mclib.api.command.extension.generateSuggestions
import net.developertobi.mclib.api.command.subcommand.McSubCommand
import net.developertobi.mclib.api.command.subcommand.McSubCommandExecutor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player

@McSubCommand(
    minLength = 1,
    maxLength = 2,
    parts = "join [arena]",
    permission = "arena.join",
)
class JoinSubCommand : McSubCommandExecutor {

    private val arenaManager = MicroGamesProvider.api.arenaManager

    override fun execute(sender: McCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val arenas = arenaManager.getArenas()
        val arenaArg = findArgument(player, "arena", args, String::class.java)
        
        val arenaId = when {
            arenaArg != null -> ArenaId(arenaArg).takeIf { it in arenas }
            else -> arenas.firstOrNull { id ->
                val ctx = arenaManager.getArenaContext(id) ?: return@firstOrNull false
                ctx.currentPhase?.id?.value != "ending" && (ctx.players.size < ctx.maxPlayers || ctx.allowSpectators)
            }
        }

        when (arenaId) {
            null -> when {
                arenaArg != null -> sender.sendMessage(McLibProvider.api.localizationController.line(LangKeys.COMMAND_JOIN_INVALID_ARENA, Placeholder.unparsed("arena", arenaArg)))
                else -> sender.sendMessage(McLibProvider.api.localizationController.line(LangKeys.COMMAND_JOIN_NO_AVAILABLE))
            }
            else -> handleJoin(sender, player, arenaManager, arenaId)
        }
    }

    private fun handleJoin(sender: McCommandSender, player: Player, arenaManager: ArenaManager, arenaId: ArenaId) {
        if (!arenaManager.addPlayerToArena(player, arenaId)) {
            val ctx = arenaManager.getArenaContext(arenaId)
            val key = when {
                ctx?.currentPhase?.id?.value == "ending" -> LangKeys.COMMAND_JOIN_ENDING
                ctx != null && ctx.players.size >= ctx.maxPlayers && !ctx.allowSpectators -> LangKeys.COMMAND_JOIN_FULL
                else -> LangKeys.COMMAND_JOIN_FAILED
            }
            sender.sendMessage(McLibProvider.api.localizationController.line(key))
        } else {
            sender.sendMessage(McLibProvider.api.localizationController.line(LangKeys.COMMAND_JOIN_SUCCESS, Placeholder.unparsed("arena", arenaId.value)))
        }
    }

    override fun onTabComplete(sender: McCommandSender, args: List<String>): List<String> = runBlocking {
        generateSuggestions(args, 1) { addAll(arenaManager.getArenas().map { it.value }) }
    }
}
