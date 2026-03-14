package net.developertobi.game.bukkit.command.subcommand

import net.developertobi.game.api.MicroGamesProvider
import net.developertobi.game.bukkit.localization.LangKeys
import net.developertobi.mclib.api.McLibProvider
import net.developertobi.mclib.api.command.McCommandSender
import net.developertobi.mclib.api.command.subcommand.McSubCommand
import net.developertobi.mclib.api.command.subcommand.McSubCommandExecutor
import org.bukkit.entity.Player

@McSubCommand(
    length = 1,
    parts = "leave",
    permission = "arena.leave",
)
class LeaveSubCommand : McSubCommandExecutor {

    override fun execute(sender: McCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return
        val success = MicroGamesProvider.api.arenaManager.removePlayerFromArena(player)
      
       sender.sendMessage(McLibProvider.api.localizationController.line(if (success) LangKeys.COMMAND_LEAVE_SUCCESS else LangKeys.COMMAND_LEAVE_NOT_IN_ARENA))
    }

    override fun onTabComplete(sender: McCommandSender, args: List<String>): List<String> = emptyList()
}
