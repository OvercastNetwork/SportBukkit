package org.bukkit.support;

import org.bukkit.Bukkit;
import org.bukkit.CraftBukkitRuntime;
import org.junit.After;
import org.junit.Before;

public class BukkitRuntimeTest {

    private boolean setRuntime;

    @Before
    public final void initRuntime() throws Exception {
        setRuntime = Bukkit.getRuntime() == null;
        if(setRuntime) {
            CraftBukkitRuntime.load();
        }
    }

    @After
    public final void clearRuntime() throws Exception {
        if(setRuntime) {
            Bukkit.setRuntime(null);
        }
    }
}
