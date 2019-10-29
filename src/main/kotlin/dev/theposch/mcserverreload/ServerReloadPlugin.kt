package dev.theposch.mcserverreload

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.http4k.core.*
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.CachingFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.SunHttp
import org.http4k.server.asServer


class ServerReloadPlugin : JavaPlugin() {

    private var server : Http4kServer? = null

    override fun onEnable() {
        // Save the default configuration if not yet present
        saveDefaultConfig()

        // Read the server port from the configuration file
        val port = config.getInt("port")
        logger.info("Starting server on port $port")

        // Try to start the server
        try {
            server = startServer(port)
        } catch (e:Exception) {
            logger.info(e.printStackTrace().toString())
            logger.info("Could not start server on port $port")
        }
    }

    override fun onDisable() {
        // Stop the server
        server?.stop()
        //server?.block();
        logger.info("Server stopped!")
    }

    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if (command?.name.equals("hotreload", true)) {
            logger.info("Command fired")
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "reload")
            return true
        }
        return false
    }

    private fun startServer (port:Int) : Http4kServer {

        // Reload handler
        val reloadHandler : HttpHandler = {_ -> Response(OK).body("reloading...") }

        // Ping handler
        val pingHandler : HttpHandler = {_ -> Response(OK).body("pong!")}

        // Create the filter to actually reload the server
        val reloadFilter = Filter {
            next: HttpHandler -> {
                request: Request ->
                val response = next(request)
                logger.info("Reload triggered!")
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "reload")
                response
            }
        }

        // Create a filter to log ping requests
        val pingFilter = Filter {
            next : HttpHandler -> {
                request: Request ->
                val response = next(request)
                logger.info("Someone pinged the server!")
                response
            }
        }

        // Apply the filter to the ping handler
        val pingComposite = CachingFilters.Response.NoCache().then(pingFilter)
        val pingFiltered : HttpHandler = pingComposite.then(pingHandler)

        // Apply the filter to the reload handler
        val reloadComposite = CachingFilters.Response.NoCache().then(reloadFilter)
        val reloadFiltered : HttpHandler = reloadComposite.then(reloadHandler)

        // Bind routes
        val routes : HttpHandler = routes(
            "/ping" bind Method.GET to pingFiltered,
            "/reload" bind Method.GET to reloadFiltered
        )

        // Start the server
        return routes.asServer(SunHttp(port)).start()
    }
}