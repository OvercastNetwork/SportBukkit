package org.bukkit.craftbukkit.user;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import org.bukkit.craftbukkit.event.CraftServerEventRegistry;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import tc.oc.minecraft.api.user.User;
import tc.oc.minecraft.api.user.UserFactory;
import tc.oc.minecraft.api.user.UserSource;

/**
 * Refreshes all {@link UserSource}s when a player logs in
 */
public class UserUpdateListener implements Listener {

    private final List<UserSource> userSources;
    private final UserFactory userFactory;

    @Inject UserUpdateListener(Set<UserSource> userSources, UserFactory userFactory, CraftServerEventRegistry eventRegistry) {
        this.userSources = new ArrayList<>(userSources);
        Collections.reverse(this.userSources); // Update sources in the same order as cache propagates
        this.userFactory = userFactory;
        eventRegistry.registerListener(this);
    }

    @EventHandler
    public void callEvent(AsyncPlayerPreLoginEvent event) throws EventException {
        final User user = userFactory.createUser(event.getUniqueId(), event.getName(), Instant.now());
        userSources.forEach(source -> source.update(user));
    }
}
