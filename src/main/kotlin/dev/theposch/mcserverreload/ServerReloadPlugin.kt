package dev.theposch.mcserverreload

import org.bukkit.plugin.java.JavaPlugin

class ServerReloadPlugin : JavaPlugin() {
    override fun onEnable() {
        logger.info("Enabling HotReload version")
    }

    override fun onDisable() {
        logger.info("Disabling HotReload unloaded")
    }
}