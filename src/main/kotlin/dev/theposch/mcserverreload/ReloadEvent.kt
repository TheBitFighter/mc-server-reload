package dev.theposch.mcserverreload

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class ReloadEvent (message:String) : Event() {

    override fun getHandlers(): HandlerList {
        return handlers
    }

    fun getHandlerList():HandlerList {
        return handlers
    }
}