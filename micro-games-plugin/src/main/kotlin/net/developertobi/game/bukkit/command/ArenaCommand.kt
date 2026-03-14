package net.developertobi.game.bukkit.command

import kotlinx.coroutines.runBlocking
import net.developertobi.mclib.api.command.McCommand
import net.developertobi.mclib.api.command.McCommandSender
import net.developertobi.mclib.api.command.McCommandType
import net.developertobi.mclib.api.command.McMainCommandExecutor
import net.developertobi.mclib.api.command.extension.generateSimpleSuggestions
import net.developertobi.mclib.api.command.extension.generateSuggestions
import net.developertobi.mclib.api.command.extension.sendUsageFormatted
import net.developertobi.game.bukkit.command.subcommand.JoinSubCommand
import net.developertobi.game.bukkit.command.subcommand.LeaveSubCommand
import net.developertobi.game.bukkit.command.subcommand.ListSubCommand
import net.developertobi.mclib.api.command.subcommand.McSubCommandExecutor

@McCommand(
    type = McCommandType.PAPER,
    name = "arena",
    title = "Arena",
)
class ArenaCommand : McMainCommandExecutor() {

    override fun initSubCommands(subCommandExecutors: MutableList<McSubCommandExecutor>) {
        subCommandExecutors.add(JoinSubCommand())
        subCommandExecutors.add(LeaveSubCommand())
        subCommandExecutors.add(ListSubCommand())
    }

    override fun defaultExecute(sender: McCommandSender) {
        sendUsageFormatted(sender)
    }

    override fun onDefaultTabComplete(sender: McCommandSender, args: List<String>): List<String> = runBlocking {
        buildList {
            addAll(generateSimpleSuggestions(args, 0) { addAll(listOf("join", "leave", "list")) })
            addAll(generateSuggestions(args, 1) { addAll(listOf("join", "leave", "list")) })
        }
    }
}
