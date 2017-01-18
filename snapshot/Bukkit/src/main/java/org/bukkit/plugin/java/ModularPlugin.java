package org.bukkit.plugin.java;

import com.google.inject.Module;
import tc.oc.inject.ProtectedBinder;

class ModularPlugin extends JavaPlugin {

    private final Module module;

    ModularPlugin(Module module) {
        super(module.getClass().getClassLoader());
        this.module = module;
    }

    public void configure(ProtectedBinder binder) {
        binder.install(module);
    }
}
