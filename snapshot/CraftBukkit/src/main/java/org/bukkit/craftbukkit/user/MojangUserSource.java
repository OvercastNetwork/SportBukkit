package org.bukkit.craftbukkit.user;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import org.bukkit.craftbukkit.CraftOfflinePlayer;
import org.bukkit.craftbukkit.CraftOfflinePlayerFactory;
import tc.oc.minecraft.api.user.User;
import tc.oc.minecraft.api.user.UserSearch;
import tc.oc.minecraft.api.user.UserSource;

@Singleton
public class MojangUserSource implements UserSource {

    private final CraftOfflinePlayerFactory userFactory;
    private final MinecraftSessionService sessions;
    private final GameProfileRepository profiles;

    @Inject MojangUserSource(CraftOfflinePlayerFactory userFactory, MinecraftSessionService sessions, GameProfileRepository profiles) {
        this.userFactory = userFactory;
        this.sessions = sessions;
        this.profiles = profiles;
    }

    @Override
    public User search(UserSearch search, Supplier<User> next) {
        // Never fetch on the main thread
        if(search.sync()) return next.get();

        final CraftOfflinePlayer op;
        if(search.query() instanceof UUID) {
            op = userFactory.createUser(
                sessions.fillProfileProperties(new GameProfile((UUID) search.query(), null), true),
                Optional.of(Instant.now())
            );
        } else {
            // Despite the weird callback, this does appear to be entirely synchronous
            final NameCallback callback = new NameCallback();
            profiles.findProfilesByNames(new String[] {(String) search.query()}, Agent.MINECRAFT, callback);
            if(callback.user == null) return next.get();
            op = callback.user;
        }
        return search.filter().test(op) ? op : next.get();
    }

    private class NameCallback implements ProfileLookupCallback {

        @Nullable CraftOfflinePlayer user;

        @Override
        public void onProfileLookupSucceeded(GameProfile profile) {
            user = userFactory.createUser(profile, Optional.of(Instant.now()));
        }

        @Override
        public void onProfileLookupFailed(GameProfile profile, Exception e) {}
    }
}
