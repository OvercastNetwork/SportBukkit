package org.bukkit.craftbukkit;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.mojang.authlib.GameProfile;
import tc.oc.minecraft.api.user.User;
import tc.oc.minecraft.api.user.UserFactory;
import tc.oc.minecraft.api.user.UserUtils;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class CraftOfflinePlayerFactory implements UserFactory {

    private final Provider<CraftServer> server;

    @Inject CraftOfflinePlayerFactory(Provider<CraftServer> server) {
        this.server = server;
    }

    public CraftOfflinePlayer createUser(GameProfile profile, Optional<Instant> updatedAt) {
        return new CraftOfflinePlayer(server.get(), profile, updatedAt);
    }

    @Override
    public CraftOfflinePlayer createUser(UUID id) {
        return createUser(new GameProfile(checkNotNull(id), null), Optional.empty());
    }

    @Override
    public CraftOfflinePlayer createUser(String name) {
        return createUser(new GameProfile(UserUtils.offlinePlayerId(name), name), Optional.empty());
    }

    @Override
    public CraftOfflinePlayer createUser(UUID id, String name, Instant updatedAt) {
        return createUser(new GameProfile(checkNotNull(id), checkNotNull(name)), Optional.of(updatedAt));
    }

    @Override
    public CraftOfflinePlayer copyUser(User user) {
        return user instanceof CraftOfflinePlayer
               ? (CraftOfflinePlayer) user
               : (CraftOfflinePlayer) UserFactory.super.copyUser(user);
    }
}
