package org.bukkit.craftbukkit;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.OptionalBinder;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Server;
import org.bukkit.ServerModule;
import org.bukkit.craftbukkit.event.CraftServerEventRegistry;
import org.bukkit.craftbukkit.scheduler.CraftScheduler;
import org.bukkit.craftbukkit.user.MojangUserSource;
import org.bukkit.craftbukkit.user.UserUpdateListener;
import tc.oc.minecraft.api.user.UserFactory;
import tc.oc.minecraft.api.user.UserSourceBinder;

public class CraftServerModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ServerModule());

        bind(CraftServerEventRegistry.class);
        bind(CraftScheduler.class).in(Singleton.class);
        bind(UserUpdateListener.class).asEagerSingleton();

        OptionalBinder.newOptionalBinder(binder(), UserFactory.class)
                      .setBinding().to(CraftOfflinePlayerFactory.class);

        new UserSourceBinder(binder())
            .addBinding().to(MojangUserSource.class);
    }

    @Provides
    CraftServer craftServer(Server server) {
        return (CraftServer) server;
    }

    @Provides
    MinecraftServer minecraftServer(CraftServer craftServer) {
        return craftServer.getServer();
    }

    @Provides
    MinecraftSessionService minecraftSessionService(MinecraftServer minecraftServer) {
        return minecraftServer.az();
    }

    @Provides
    GameProfileRepository gameProfileRepository(MinecraftServer minecraftServer) {
        return minecraftServer.getGameProfileRepository();
    }
}
