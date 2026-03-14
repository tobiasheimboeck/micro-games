package net.developertobi.game.bukkit.command.subcommand

import net.developertobi.game.api.MicroGamesProvider
import net.developertobi.game.bukkit.localization.LangKeys
import net.developertobi.mclib.api.McLibProvider
import net.developertobi.mclib.api.command.McCommandSender
import net.developertobi.mclib.api.command.subcommand.McSubCommand
import net.developertobi.mclib.api.command.subcommand.McSubCommandExecutor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder

@McSubCommand(
    length = 1,
    parts = "list",
    permission = "arena.list",
)
class ListSubCommand : McSubCommandExecutor {

    override fun execute(sender: McCommandSender, args: List<String>) {
        val arenaManager = MicroGamesProvider.api.arenaManager

        sender.sendMessage(McLibProvider.api.localizationController.line(LangKeys.COMMAND_LIST_HEADER))

        arenaManager.getArenas().sortedBy { it.value }.forEach { arenaId ->
            val ctx = arenaManager.getArenaContext(arenaId)
            val count = ctx?.players?.size ?: 0
            val max = ctx?.maxPlayers ?: 0
            sender.sendMessage(McLibProvider.api.localizationController.line(LangKeys.COMMAND_LIST_LINE, Placeholder.unparsed("arena", arenaId.value), Placeholder.unparsed("count", count.toString()), Placeholder.unparsed("max", max.toString())))
        }
    }

    override fun onTabComplete(sender: McCommandSender, args: List<String>): List<String> = emptyList()
}
