package org.bukkit;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import com.google.inject.Provides;
import org.bukkit.command.CommandMap;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventBus;
import org.bukkit.help.HelpMap;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import tc.oc.inject.SingletonModule;
import tc.oc.minecraft.api.command.Console;
import tc.oc.minecraft.api.plugin.PluginFinder;
import tc.oc.minecraft.api.server.LocalServer;
import tc.oc.minecraft.api.text.TextRenderContext;

/**
 * Bindings for things that belong to a {@link Server}.
 *
 * Does not bind {@link Server} itself.
 *
 * @see ServerInstanceModule
 */
public class ServerModule extends SingletonModule {

    @Override
    protected void configure() {
        install(new BukkitModule());

        bind(tc.oc.minecraft.api.server.Server.class).to(LocalServer.class);
        bind(LocalServer.class).to(Server.class);
        bind(BukkitRuntime.class).to(Server.class);
        bind(Console.class).to(ConsoleCommandSender.class);
        bind(PluginFinder.class).to(PluginManager.class);
    }

    @Provides
    PluginManager pluginManager(Server server) {
        return server.getPluginManager();
    }

    @Provides
    EventBus eventBus(Server server) {
        return server.eventBus();
    }

    @Provides
    BukkitScheduler bukkitScheduler(Server server) {
        return server.getScheduler();
    }

    @Provides
    CommandMap commandMap(Server server) {
        return server.getCommandMap();
    }

    @Provides
    HelpMap helpMap(Server server) {
        return server.getHelpMap();
    }

    @Provides
    ConsoleCommandSender consoleCommandSender(Server server) {
        return server.getConsoleSender();
    }

    @Provides
    Messenger messenger(Server server) {
        return server.getMessenger();
    }

    @Provides
    ScoreboardManager scoreboardManager(Server server) {
        return server.getScoreboardManager();
    }

    @Provides
    ServicesManager servicesManager(Server server) {
        return server.getServicesManager();
    }

    @Provides
    TextRenderContext textRenderContext(Server server) {
        return server.textRenderContext();
    }

    @Provides
    Collection<World> worlds(Server server) {
        return server.worldsById().values();
    }

    @Provides
    Map<UUID, World> worldsById(Server server) {
        return server.worldsById();
    }

    @Provides
    Map<String, World> worldsByName(Server server) {
        return server.worldsByName();
    }

    @Provides
    Collection<Player> onlinePlayers(Server server) {
        return (Collection<Player>) server.getOnlinePlayers();
    }

    @Provides
    Map<UUID, Player> playersById(Server server) {
        return server.playersById();
    }
}
