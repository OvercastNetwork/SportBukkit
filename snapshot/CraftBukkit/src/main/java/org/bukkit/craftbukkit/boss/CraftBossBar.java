package org.bukkit.craftbukkit.boss;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.BossBattle;
import net.minecraft.server.BossBattleServer;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.PacketPlayOutBoss;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.BungeeChatUtils;
import org.bukkit.entity.Player;

import static com.google.common.base.Preconditions.checkNotNull;

public class CraftBossBar implements BossBar {

    private final BossBattleServer handle;
    private BaseComponent title;
    private double progress;
    private final Set<BarFlag> flags;
    private BarColor color;
    private BarStyle style;
    private PacketPlayOutBoss.Action dirty;
    private boolean autoUpdate = true;

    public CraftBossBar(BaseComponent title, BarColor color, BarStyle style, BarFlag... flags) {
        this.title = checkNotNull(title);
        this.flags = flags.length > 0 ? EnumSet.of(flags[0], flags) : EnumSet.noneOf(BarFlag.class);
        this.color = checkNotNull(color);
        this.style = checkNotNull(style);

        handle = new BossBattleServer(
                BungeeChatUtils.toNms(title),
                convertColor(color),
                convertStyle(style)
        );

        updateFlags();
        this.progress = handle.getProgress();
    }

    private BossBattle.BarColor convertColor(BarColor color) {
        BossBattle.BarColor nmsColor = BossBattle.BarColor.valueOf(color.name());
        return (nmsColor == null) ? BossBattle.BarColor.WHITE : nmsColor;
    }

    private BossBattle.BarStyle convertStyle(BarStyle style) {
        switch (style) {
            default:
            case SOLID:
                return BossBattle.BarStyle.PROGRESS;
            case SEGMENTED_6:
                return BossBattle.BarStyle.NOTCHED_6;
            case SEGMENTED_10:
                return BossBattle.BarStyle.NOTCHED_10;
            case SEGMENTED_12:
                return BossBattle.BarStyle.NOTCHED_12;
            case SEGMENTED_20:
                return BossBattle.BarStyle.NOTCHED_20;
        }
    }

    private void updateFlags() {
        handle.a(hasFlag(BarFlag.DARKEN_SKY));
        handle.b(hasFlag(BarFlag.PLAY_BOSS_MUSIC));
        handle.c(hasFlag(BarFlag.CREATE_FOG));
    }

    private void markDirty(PacketPlayOutBoss.Action action) {
        if(dirty == null) {
            dirty = action;
        } else {
            dirty = PacketPlayOutBoss.Action.ADD;
        }

        if(autoUpdate) sendUpdate();
    }

    private void sendUpdate() {
        if(dirty != null) {
            handle.sendUpdate(dirty);
            dirty = null;
        }
    }

    @Override
    public BaseComponent getTitle() {
        return title;
    }

    @Override
    public void setTitle(BaseComponent title) {
        if(!this.title.equals(title)) {
            this.title = title;
            handle.title = BungeeChatUtils.toNms(title);
            markDirty(PacketPlayOutBoss.Action.UPDATE_NAME);
        }
    }

    @Override
    public BarColor getColor() {
        return color;
    }

    @Override
    public void setColor(BarColor color) {
        if(!this.color.equals(color)) {
            this.color = color;
            handle.color = convertColor(color);
            markDirty(PacketPlayOutBoss.Action.UPDATE_STYLE);
        }
    }

    @Override
    public BarStyle getStyle() {
        return style;
    }

    @Override
    public void setStyle(BarStyle style) {
        if(!this.style.equals(style)) {
            this.style = style;
            handle.style = convertStyle(style);
            markDirty(PacketPlayOutBoss.Action.UPDATE_STYLE);
        }
    }

    @Override
    public void setFlags(Set<BarFlag> flags) {
        if(!this.flags.equals(flags)) {
            this.flags.clear();
            this.flags.addAll(flags);
            updateFlags();
            markDirty(PacketPlayOutBoss.Action.UPDATE_PROPERTIES);
        }
    }

    @Override
    public void addFlag(BarFlag flag) {
        if(flags.remove(flag)) {
            updateFlags();
            markDirty(PacketPlayOutBoss.Action.UPDATE_PROPERTIES);
        }
    }

    @Override
    public void removeFlag(BarFlag flag) {
        if(flags.add(flag)) {
            updateFlags();
            markDirty(PacketPlayOutBoss.Action.UPDATE_PROPERTIES);
        }
    }

    @Override
    public boolean hasFlag(BarFlag flag) {
        return flags.contains(flag);
    }

    @Override
    public void setProgress(double progress) {
    	Preconditions.checkArgument(progress >= 0.0 && progress <= 1.0, "Progress must be between 0.0 and 1.0 (%s)", progress);
        if(this.progress != progress) {
            this.progress = progress;
            handle.a((float) progress);
            markDirty(PacketPlayOutBoss.Action.UPDATE_PCT);
        }
    }

    @Override
    public double getProgress() {
        return handle.getProgress();
    }

    @Override
    public void addPlayer(Player player) {
        handle.addPlayer(((CraftPlayer) player).getHandle());
    }

    @Override
    public void removePlayer(Player player) {
        handle.removePlayer(((CraftPlayer) player).getHandle());
    }

    @Override
    public List<Player> getPlayers() {
        ImmutableList.Builder<Player> players = ImmutableList.builder();
        for (EntityPlayer p : handle.getPlayers()) {
            players.add(p.getBukkitEntity());
        }
        return players.build();
    }

    @Override
    public void setVisible(boolean visible) {
        handle.setVisible(visible);
    }

    @Override
    public boolean isVisible() {
        return handle.visible;
    }

    @Override
    public void show() {
        handle.setVisible(true);
    }

    @Override
    public void hide() {
        handle.setVisible(false);
        dirty = null;
    }

    @Override
    public void removeAll() {
        for (Player player : getPlayers()) {
            removePlayer(player);
        }
        dirty = null;
    }

    @Override
    public void update(BaseComponent title, double progress, BarColor color, BarStyle style, Set<BarFlag> flags) {
        autoUpdate = false;
        try {
            setTitle(title);
            setProgress(progress);
            setColor(color);
            setStyle(style);
            setFlags(flags);
            sendUpdate();
        } finally {
            autoUpdate = true;
        }
    }
}
