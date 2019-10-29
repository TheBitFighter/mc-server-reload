package dev.theposch.mcserverreload

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class ServerReloadPlugin : JavaPlugin() {
    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if (command?.name.equals("bing", true)) {
            logger.info("Command fired")
            return true
        }
        return false
    }
}