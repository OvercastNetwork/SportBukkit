package org.bukkit.permissions;

import com.google.inject.Binder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;

public class PermissionBinder {

    private final Multibinder<Permission> permissions;

    public PermissionBinder(Binder binder) {
        this.permissions = Multibinder.newSetBinder(binder, Permission.class);
    }

    public LinkedBindingBuilder<Permission> bindPermission() {
        return permissions.addBinding();
    }
}
