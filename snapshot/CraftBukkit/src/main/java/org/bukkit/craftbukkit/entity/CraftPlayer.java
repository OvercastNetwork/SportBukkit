package org.bukkit.craftbukkit.entity;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.WeakHashMap;
import java.util.Arrays;

import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.*;
import net.minecraft.server.PacketPlayOutTitle.EnumTitleAction;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.*;
import org.bukkit.Achievement;
import org.bukkit.BanList;
import org.bukkit.Statistic;
import org.bukkit.Material;
import org.bukkit.Statistic.Type;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.craftbukkit.conversations.ConversationTracker;
import org.bukkit.craftbukkit.CraftEffect;
import org.bukkit.craftbukkit.CraftOfflinePlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.CraftStatistic;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.map.CraftMapView;
import org.bukkit.craftbukkit.map.RenderData;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.util.BungeeChatUtils;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.Skins;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerUnregisterChannelEvent;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapView;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.RayBlockIntersection;
import org.bukkit.util.Vector;

@DelegateDeserialization(CraftOfflinePlayer.class)
public class CraftPlayer extends CraftHumanEntity implements Player {
    private long firstPlayed = 0;
    private long lastPlayed = 0;
    private boolean hasPlayedBefore = false;
    private final ConversationTracker conversationTracker = new ConversationTracker();
    private final Set<String> channels = new HashSet<String>();
    private final Set<UUID> hiddenPlayers = new HashSet<UUID>();
    private int hash = 0;
    private double health = 20;
    private boolean scaledHealth = false;
    private double healthScale = 20;
    private Skin realSkin;
    private boolean showInvisibles;
    private PlayerResourcePackStatusEvent.Status resourcePackStatus;
    private boolean quitting;

    private final Map<CommandSender, String> fakeNames = new WeakHashMap<CommandSender, String>();
    private final Map<CommandSender, String> fakeDisplayNames = new WeakHashMap<CommandSender, String>();
    private final Map<CommandSender, Skin> fakeSkins = new WeakHashMap<CommandSender, Skin>();

    public CraftPlayer(CraftServer server, EntityPlayer entity) {
        super(server, entity);

        firstPlayed = System.currentTimeMillis();
        this.realSkin = this.getSkin();
    }

    public GameProfile getProfile() {
        return getHandle().getProfile();
    }

    @Override
    public boolean isOp() {
        return server.getHandle().isOp(getProfile());
    }

    @Override
    public void setOp(boolean value) {
        if (value == isOp()) return;

        if (value) {
            server.getHandle().addOp(getProfile());
        } else {
            server.getHandle().removeOp(getProfile());
        }

        perm.recalculatePermissions();
    }

    public boolean isOnline() {
        return server.getPlayer(getUniqueId()) != null;
    }

    @Override
    public boolean willBeOnline() {
        return !quitting;
    }

    public void startQuitting() {
        quitting = true;
    }

    public InetSocketAddress getAddress() {
        if (getHandle().playerConnection == null) return null;

        SocketAddress addr = getHandle().playerConnection.networkManager.getSocketAddress();
        if (addr instanceof InetSocketAddress) {
            return (InetSocketAddress) addr;
        } else {
            return null;
        }
    }

    @Override
    public double getEyeHeight() {
        return getEyeHeight(false);
    }

    @Override
    public double getEyeHeight(boolean ignoreSneaking) {
        if (ignoreSneaking) {
            return 1.62D;
        } else {
            if (isSneaking()) {
                return 1.54D;
            } else {
                return 1.62D;
            }
        }
    }

    @Override
    public void sendRawMessage(String message) {
        if (getHandle().playerConnection == null) return;

        for (IChatBaseComponent component : CraftChatMessage.fromString(message)) {
            getHandle().playerConnection.sendPacket(new PacketPlayOutChat(component));
        }
    }

    @Override
    public void sendMessage(String message) {
        if (!conversationTracker.isConversingModaly()) {
            this.sendRawMessage(message);
        }
    }

    @Override
    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    @Override
    public Set<Skin.Part> getSkinParts() {
        return Skins.partsFromFlags((int) this.getHandle().getDataWatcher().get(EntityHuman.SKIN_PARTS));
    }

    @Override
    public boolean hasFakeSkin(CommandSender viewer) {
        return viewer != null && this.fakeSkins.containsKey(viewer);
    }

    @Override
    public Skin getFakeSkin(CommandSender viewer) {
        return viewer == null ? null : this.fakeSkins.get(viewer);
    }

    @Override
    public void setFakeSkin(CommandSender viewer, Skin newSkin) {
        this.setFakeNameAndSkin(viewer, null, newSkin, false, true);
    }

    @Override
    public Skin getSkin() {
        return Skins.fromProperties(this.getProfile().getProperties());
    }

    @Override
    public Skin getSkin(CommandSender viewer) {
        if(this.hasFakeSkin(viewer)) {
            return this.getFakeSkin(viewer);
        } else {
            return this.getSkin();
        }
    }

    @Override
    public Skin getRealSkin() {
        return this.realSkin;
    }

    @Override
    public void clearFakeSkins() {
        Set<CommandSender> viewers = new HashSet<CommandSender>(this.fakeSkins.keySet());
        for(CommandSender viewer : viewers) this.removeNameOnClient(viewer);
        this.fakeSkins.clear();
        for(CommandSender viewer : viewers) this.addNameOnClient(viewer);
    }

    @Override
    public void setSkin(Skin newSkin) {
        if(newSkin == null) {
            newSkin = this.realSkin;
        }

        Skin oldSkin = this.getSkin();
        if(Objects.equal(oldSkin, newSkin)) {
            return;
        }
        Set<Player> viewers = new HashSet<Player>(this.getServer().getOnlinePlayers());
        viewers.removeAll(this.fakeSkins.keySet());

        for(Player viewer : viewers) this.removeNameOnClient(viewer);
        Skins.setProperties(newSkin, this.getProfile().getProperties());
        for(Player viewer : viewers) this.addNameOnClient(viewer);
    }

    @Override
    public void clearFakeNamesAndSkins() {
        Set<CommandSender> viewers = new HashSet<CommandSender>(this.fakeNames.keySet());
        viewers.addAll(this.fakeSkins.keySet());

        for(CommandSender viewer : viewers) this.removeNameOnClient(viewer);
        this.fakeNames.clear();
        this.fakeSkins.clear();
        for(CommandSender viewer : viewers) this.addNameOnClient(viewer);
    }

    @Override
    public boolean hasFakeName(CommandSender viewer) {
        return viewer != null && this.fakeNames.containsKey(viewer);
    }

    @Override
    public void clearFakeNames() {
        Set<CommandSender> viewers = new HashSet<CommandSender>(this.fakeNames.keySet());
        for(CommandSender viewer : viewers) this.removeNameOnClient(viewer);
        this.fakeNames.clear();
        for(CommandSender viewer : viewers) this.addNameOnClient(viewer);
    }

    @Override
    public String getFakeName(CommandSender viewer) {
        return viewer == null ? null : this.fakeNames.get(viewer);
    }

    @Override
    public void setFakeName(CommandSender viewer, String name) {
        this.setFakeNameAndSkin(viewer, name, null, true, false);
    }

    @Override
    public void setFakeNameAndSkin(CommandSender viewer, String name, Skin skin) {
        this.setFakeNameAndSkin(viewer, name, skin, true, true);
    }

    private void setFakeNameAndSkin(CommandSender viewer, String newName, Skin newSkin, boolean nameChanged, boolean skinChanged) {
        String oldName = this.fakeNames.get(viewer);
        nameChanged = nameChanged && !Objects.equal(oldName, newName);
        Skin oldSkin = this.fakeSkins.get(viewer);
        skinChanged = skinChanged && !Objects.equal(oldSkin, newSkin);

        if(nameChanged || skinChanged) {
            if(newName != null && newName.length() > 16) {
                throw new IllegalArgumentException("Fake player names are limited to 16 characters in length");
            }

            this.removeNameOnClient(viewer);

            if(newName == null) {
                this.fakeNames.remove(viewer);
            } else {
                this.fakeNames.put(viewer, newName);
            }

            if(newSkin == null) {
                this.fakeSkins.remove(viewer);
            } else {
                this.fakeSkins.put(viewer, newSkin);
            }

            this.addNameOnClient(viewer);
        }
    }

    public PacketPlayOutPlayerInfo makePlayerListAddPacket(Player viewer) {
        GameProfile profile = getHandle().getProfile();
        String name = getName(viewer);
        Skin skin = getSkin(viewer);

        if(!name.equals(getName()) || !skin.equals(getSkin())) {
            profile = new GameProfile(getUniqueId(), name);
            Skins.setProperties(skin, profile.getProperties());
        }

        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
        packet.add(packet.new PlayerInfoData(profile,
                                             getHandle().ping,
                                             getHandle().playerInteractManager.getGameMode(),
                                             getHandle().listName));
        return packet;
    }

    /**
     * Remove this player from the given viewer's scoreboard and player list.
     */
    private void removeNameOnClient(CommandSender viewer) {
        if(viewer instanceof CraftPlayer) {
            CraftPlayer craftViewer = (CraftPlayer) viewer;
            EntityPlayer viewerEntity = craftViewer.getHandle();
            if(viewerEntity.playerConnection != null) {
                if(craftViewer.canSee(this)) {
                    EntityTrackerEntry entry = ((WorldServer) this.entity.world).tracker.trackedEntities.get(getHandle().getId());
                    if(entry != null) {
                        entry.clear(craftViewer.getHandle());
                    }

                    viewerEntity.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, this.getHandle()));
                }

                ScoreboardTeam team = craftViewer.getScoreboard().getHandle().getPlayerTeam(this.getName());
                if(team != null) {
                    viewerEntity.playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(team, Arrays.asList(this.getName()), 4));
                }
            }
        }
    }

    /**
     * Add this player to the given viewer's scoreboard and player list.
     */
    private void addNameOnClient(CommandSender viewer) {
        if(viewer instanceof CraftPlayer) {
            CraftPlayer craftViewer = (CraftPlayer) viewer;
            EntityPlayer viewerEntity = craftViewer.getHandle();
            if(viewerEntity.playerConnection != null) {
                ScoreboardTeam team = craftViewer.getScoreboard().getHandle().getPlayerTeam(this.getName());
                if(team != null) {
                    viewerEntity.playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(team, Arrays.asList(this.getName()), 3));
                }

                if(craftViewer.canSee(this)) {
                    viewerEntity.playerConnection.sendPacket(this.makePlayerListAddPacket(craftViewer));

                    EntityTrackerEntry entry = (EntityTrackerEntry) ((WorldServer) this.entity.world).tracker.trackedEntities.get(getHandle().getId());
                    if(entry != null) {
                        entry.updatePlayer(craftViewer.getHandle());
                    }
                }
            }
        }
    }

    @Override
    public boolean hasFakeDisplayName(CommandSender viewer) {
        return this.fakeDisplayNames.containsKey(viewer);
    }

    @Override
    public void clearFakeDisplayNames() {
        this.fakeDisplayNames.clear();
    }

    @Override
    public String getFakeDisplayName(CommandSender viewer) {
        return viewer == null ? null : this.fakeDisplayNames.get(viewer);
    }

    @Override
    public void setFakeDisplayName(CommandSender viewer, String name) {
        if(name == null)  {
            this.fakeDisplayNames.remove(viewer);
        } else {
            this.fakeDisplayNames.put(viewer, name);
        }
    }

    @Override
    public String getName(CommandSender viewer) {
        String name = this.getFakeName(viewer);
        if(name == null) {
            name = this.getName();
        }
        return name;
    }

    @Override
    public String getPlayerListName(CommandSender viewer) {
        String name = this.getFakeName(viewer);
        if(name == null) {
            name = this.getPlayerListName();
        }
        return name;
    }

    @Override
    public String getDisplayName(CommandSender viewer) {
        String name = this.getFakeDisplayName(viewer);
        if(name == null) {
            name = this.getDisplayName();
        }
        return name;
    }

    @Override
    public String getDisplayName() {
        return getHandle().displayName;
    }

    @Override
    public void setDisplayName(final String name) {
        getHandle().displayName = name == null ? getName() : name;
    }

    @Override
    public String getPlayerListName() {
        return getHandle().listName == null ? getName() : CraftChatMessage.fromComponent(getHandle().listName, EnumChatFormat.WHITE);
    }

    @Override
    public void setPlayerListName(String name) {
        if (name == null) {
            name = getName();
        }
        getHandle().listName = name.equals(getName()) ? null : CraftChatMessage.fromString(name)[0];
        for (EntityPlayer player : (List<EntityPlayer>)server.getHandle().players) {
            if (player.getBukkitEntity().canSee(this)) {
                player.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, getHandle()));
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OfflinePlayer)) {
            return false;
        }
        OfflinePlayer other = (OfflinePlayer) obj;
        if ((this.getUniqueId() == null) || (other.getUniqueId() == null)) {
            return false;
        }

        boolean uuidEquals = this.getUniqueId().equals(other.getUniqueId());
        boolean idEquals = true;

        if (other instanceof CraftPlayer) {
            idEquals = this.getEntityId() == ((CraftPlayer) other).getEntityId();
        }

        return uuidEquals && idEquals;
    }

    @Override
    public void kickPlayer(String message) {
        if (getHandle().playerConnection == null) return;

        getHandle().playerConnection.disconnect(message == null ? "" : message);
    }

    @Override
    public void setCompassTarget(Location loc) {
        if (getHandle().playerConnection == null) return;

        // Do not directly assign here, from the packethandler we'll assign it.
        getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnPosition(new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())));
    }

    @Override
    public Location getCompassTarget() {
        return getHandle().compassTarget;
    }

    @Override
    public void chat(String msg) {
        if (getHandle().playerConnection == null) return;

        getHandle().playerConnection.chat(msg, false);
    }

    @Override
    public boolean performCommand(String command) {
        return server.dispatchCommand(this, command);
    }

    @Override
    public void playNote(Location loc, byte instrument, byte note) {
        if (getHandle().playerConnection == null) return;

        String instrumentName = null;
        switch (instrument) {
        case 0:
            instrumentName = "harp";
            break;
        case 1:
            instrumentName = "basedrum";
            break;
        case 2:
            instrumentName = "snare";
            break;
        case 3:
            instrumentName = "hat";
            break;
        case 4:
            instrumentName = "bass";
            break;
        }

        float f = (float) Math.pow(2.0D, (note - 12.0D) / 12.0D);
        getHandle().playerConnection.sendPacket(new PacketPlayOutCustomSoundEffect(CraftSound.getSoundEffect("block.note." + instrumentName), net.minecraft.server.SoundCategory.MUSIC, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 3.0f, f));
    }

    @Override
    public void playNote(Location loc, Instrument instrument, Note note) {
        if (getHandle().playerConnection == null) return;

        String instrumentName = null;
        switch (instrument.ordinal()) {
            case 0:
                instrumentName = "harp";
                break;
            case 1:
                instrumentName = "basedrum";
                break;
            case 2:
                instrumentName = "snare";
                break;
            case 3:
                instrumentName = "hat";
                break;
            case 4:
                instrumentName = "bass";
                break;
        }
        float f = (float) Math.pow(2.0D, (note.getId() - 12.0D) / 12.0D);
        getHandle().playerConnection.sendPacket(new PacketPlayOutCustomSoundEffect(CraftSound.getSoundEffect("block.note." + instrumentName), net.minecraft.server.SoundCategory.MUSIC, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 3.0f, f));
    }

    @Override
    public void playSound(Location loc, Sound sound, float volume, float pitch) {
        playSound(loc, sound, org.bukkit.SoundCategory.MASTER, volume, pitch);
    }

    @Override
    public void playSound(Location loc, String sound, float volume, float pitch) {
        playSound(loc, sound, org.bukkit.SoundCategory.MASTER, volume, pitch);
    }

    @Override
    public void playSound(Location loc, Sound sound, org.bukkit.SoundCategory category, float volume, float pitch) {
        if (loc == null || sound == null || category == null || getHandle().playerConnection == null) return;

        PacketPlayOutCustomSoundEffect packet = new PacketPlayOutCustomSoundEffect(CraftSound.getSoundEffect(CraftSound.getSound(sound)), net.minecraft.server.SoundCategory.valueOf(category.name()), loc.getX(), loc.getY(), loc.getZ(), volume, pitch);
        getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void playSound(Location loc, String sound, org.bukkit.SoundCategory category, float volume, float pitch) {
        if (loc == null || sound == null || category == null || getHandle().playerConnection == null) return;

        PacketPlayOutCustomSoundEffect packet = new PacketPlayOutCustomSoundEffect(sound, net.minecraft.server.SoundCategory.valueOf(category.name()), loc.getX(), loc.getY(), loc.getZ(), volume, pitch);
        getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void stopSound(Sound sound) {
        stopSound(sound, null);
    }

    @Override
    public void stopSound(String sound) {
        stopSound(sound, null);
    }

    @Override
    public void stopSound(Sound sound, org.bukkit.SoundCategory category) {
        stopSound(CraftSound.getSound(sound), category);
    }

    @Override
    public void stopSound(String sound, org.bukkit.SoundCategory category) {
        if (getHandle().playerConnection == null) return;
        PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());

        packetdataserializer.a(category == null ? "" : net.minecraft.server.SoundCategory.valueOf(category.name()).a());
        packetdataserializer.a(sound);
        getHandle().playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|StopSound", packetdataserializer));
    }

    @Override
    public <T> void playEffect(Location loc, Effect effect, T data) {
        if (data != null) {
            Validate.isTrue(effect.getData() != null && effect.getData().isAssignableFrom(data.getClass()), "Wrong kind of data for this effect!");
        } else {
            Validate.isTrue(effect.getData() == null, "Wrong kind of data for this effect!");
        }

        if (data != null && data.getClass().equals(MaterialData.class)) {
            MaterialData materialData = (MaterialData) data;
            Validate.isTrue(!materialData.getItemType().isBlock(), "Material must be block");
            playEffect(loc, effect, materialData.getItemType().getId(), materialData.getData(), 0, 0, 0, 1, 1, 64);
        } else {
            int datavalue = data == null ? 0 : CraftEffect.getDataValue(effect, data);
            playEffect(loc, effect, datavalue);
        }
    }

    @Override
    public void playEffect(Location location, Effect effect, int data) {
        playEffect(location, effect, data, 0, 0f, 0f, 0f, 1f, 1, 64);
    }

    @Override
    public void playEffect(Location location, Effect effect, int id, int data, float offsetX, float offsetY, float offsetZ, float speed, int particleCount, int radius) {
        Validate.notNull(location, "Location cannot be null");
        Validate.notNull(effect, "Effect cannot be null");
        Validate.notNull(location.getWorld(), "World cannot be null");

        Packet packet = null;
        if (effect.getType() != Effect.Type.PARTICLE) {
            int packetData = effect.getId();
            packet = new PacketPlayOutWorldEvent(packetData, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), id, false);
        } else {
            StringBuilder particleFullName = new StringBuilder();
            particleFullName.append(effect.getName());

            if (effect.getData() != null && (effect.getData().equals(Material.class) || effect.getData().equals(MaterialData.class))) {
                particleFullName.append('_').append(id);
            }

            if (effect.getData() != null && effect.getData().equals(MaterialData.class)) {
                particleFullName.append('_').append(data);
            }
            packet = new PacketPlayOutWorldParticles(CraftEffect.getNMSParticle(effect), false, (float)location.getX(), (float)location.getY(), (float)location.getZ(), offsetX, offsetY, offsetZ, particleCount, radius);
        }

        if (!location.getWorld().equals(getWorld())) {
            return;
        }

        getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void sendBlockChange(Location loc, Material material, byte data) {
        sendBlockChange(loc, material.getId(), data);
    }

    @Override
    public void sendBlockChange(Location loc, int material, byte data) {
        if (getHandle().playerConnection == null) return;

        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(((CraftWorld) loc.getWorld()).getHandle(), new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

        packet.block = CraftMagicNumbers.getBlock(material).fromLegacyData(data);
        getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void sendSignChange(Location loc, String[] lines) {
        if (lines == null) {
            lines = new String[4];
        }
        final IChatBaseComponent[] nms = new IChatBaseComponent[4];
        for(int i = 0; i < 4; i++) {
            nms[i] = lines != null && lines[i] == null ? new ChatComponentText("")
                                                       : CraftChatMessage.fromString(lines[i])[0];
        }
        sendSignChange(loc, nms);
    }

    @Override
    public void sendSignChange(Location loc, BaseComponent[] lines) {
        final IChatBaseComponent[] nms = new IChatBaseComponent[4];
        for(int i = 0; i < 4; i++) {
            nms[i] = lines != null && lines[i] == null ? new ChatComponentText("")
                                                       : BungeeChatUtils.toNms(lines[i]);
        }
        sendSignChange(loc, nms);
    }

    private void sendSignChange(Location loc, IChatBaseComponent[] lines) {
        Validate.notNull(loc, "Location can not be null");

        if (lines.length < 4) {
            throw new IllegalArgumentException("Must have at least 4 lines");
        }

        if (getHandle().playerConnection == null) {
            return;
        }

        TileEntitySign sign = new TileEntitySign();
        sign.setPosition(new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        System.arraycopy(lines, 0, sign.lines, 0, sign.lines.length);

        getHandle().playerConnection.sendPacket(sign.getUpdatePacket());
    }

    @Override
    public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data) {
        if (getHandle().playerConnection == null) return false;

        /*
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        int cx = x >> 4;
        int cz = z >> 4;

        if (sx <= 0 || sy <= 0 || sz <= 0) {
            return false;
        }

        if ((x + sx - 1) >> 4 != cx || (z + sz - 1) >> 4 != cz || y < 0 || y + sy > 128) {
            return false;
        }

        if (data.length != (sx * sy * sz * 5) / 2) {
            return false;
        }

        Packet51MapChunk packet = new Packet51MapChunk(x, y, z, sx, sy, sz, data);

        getHandle().playerConnection.sendPacket(packet);

        return true;
        */

        throw new NotImplementedException("Chunk changes do not yet work"); // TODO: Chunk changes.
    }

    @Override
    public void sendMap(MapView map) {
        if (getHandle().playerConnection == null) return;

        RenderData data = ((CraftMapView) map).render(this);
        Collection<MapIcon> icons = new ArrayList<MapIcon>();
        for (MapCursor cursor : data.cursors) {
            if (cursor.isVisible()) {
                icons.add(new MapIcon(MapIcon.Type.a(cursor.getRawType()), cursor.getX(), cursor.getY(), cursor.getDirection()));
            }
        }

        PacketPlayOutMap packet = new PacketPlayOutMap(map.getId(), map.getScale().getValue(), true, icons, data.buffer, 0, 0, 128, 128);
        getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        EntityPlayer entity = getHandle();

        if (getHealth() == 0 || entity.dead) {
            return false;
        }

        if (entity.playerConnection == null) {
           return false;
        }

        if (entity.isVehicle()) {
            return false;
        }

        // From = Players current Location
        Location from = this.getLocation();
        // To = Players new Location if Teleport is Successful
        Location to = location;
        // Create & Call the Teleport Event.
        PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, to, cause);
        server.getPluginManager().callEvent(event);

        // Return False to inform the Plugin that the Teleport was unsuccessful/cancelled.
        if (event.isCancelled()) {
            return false;
        }

        // If this player is riding another entity, we must dismount before teleporting.
        entity.stopRiding();

        // Update the From Location
        from = event.getFrom();
        // Grab the new To Location dependent on whether the event was cancelled.
        to = event.getTo();
        // Grab the To and From World Handles.
        WorldServer fromWorld = ((CraftWorld) from.getWorld()).getHandle();
        WorldServer toWorld = ((CraftWorld) to.getWorld()).getHandle();

        // Close any foreign inventory
        if (getHandle().activeContainer != getHandle().defaultContainer) {
            getHandle().closeInventory();
        }

        // Check if the fromWorld and toWorld are the same.
        if (fromWorld == toWorld) {
            entity.playerConnection.teleport(to);
        } else {
            server.getHandle().moveToWorld(entity, toWorld.dimension, true, to, true);
        }
        return true;
    }

    @Override
    public void setSneaking(boolean sneak) {
        getHandle().setSneaking(sneak);
    }

    @Override
    public boolean isSneaking() {
        return getHandle().isSneaking();
    }

    @Override
    public boolean isSprinting() {
        return getHandle().isSprinting();
    }

    @Override
    public void setSprinting(boolean sprinting) {
        getHandle().setSprinting(sprinting);
    }

    @Override
    public void loadData() {
        server.getHandle().playerFileData.load(getHandle());
    }

    @Override
    public void saveData() {
        server.getHandle().playerFileData.save(getHandle());
    }

    @Deprecated
    @Override
    public void updateInventory() {
        getHandle().updateInventory(getHandle().activeContainer);
    }

    @Override
    public void setSleepingIgnored(boolean isSleeping) {
        getHandle().fauxSleeping = isSleeping;
        ((CraftWorld) getWorld()).getHandle().checkSleepStatus();
    }

    @Override
    public boolean isSleepingIgnored() {
        return getHandle().fauxSleeping;
    }

    @Override
    public void awardAchievement(Achievement achievement) {
        Validate.notNull(achievement, "Achievement cannot be null");
        if (achievement.hasParent() && !hasAchievement(achievement.getParent())) {
            awardAchievement(achievement.getParent());
        }
        getHandle().getStatisticManager().setStatistic(getHandle(), CraftStatistic.getNMSAchievement(achievement), 1);
        getHandle().getStatisticManager().updateStatistics(getHandle());
    }

    @Override
    public void removeAchievement(Achievement achievement) {
        Validate.notNull(achievement, "Achievement cannot be null");
        for (Achievement achieve : Achievement.values()) {
            if (achieve.getParent() == achievement && hasAchievement(achieve)) {
                removeAchievement(achieve);
            }
        }
        getHandle().getStatisticManager().setStatistic(getHandle(), CraftStatistic.getNMSAchievement(achievement), 0);
    }

    @Override
    public boolean hasAchievement(Achievement achievement) {
        Validate.notNull(achievement, "Achievement cannot be null");
        return getHandle().getStatisticManager().hasAchievement(CraftStatistic.getNMSAchievement(achievement));
    }

    @Override
    public void incrementStatistic(Statistic statistic) {
        incrementStatistic(statistic, 1);
    }

    @Override
    public void decrementStatistic(Statistic statistic) {
        decrementStatistic(statistic, 1);
    }

    @Override
    public int getStatistic(Statistic statistic) {
        Validate.notNull(statistic, "Statistic cannot be null");
        Validate.isTrue(statistic.getType() == Type.UNTYPED, "Must supply additional paramater for this statistic");
        return getHandle().getStatisticManager().getStatisticValue(CraftStatistic.getNMSStatistic(statistic));
    }

    @Override
    public void incrementStatistic(Statistic statistic, int amount) {
        Validate.isTrue(amount > 0, "Amount must be greater than 0");
        setStatistic(statistic, getStatistic(statistic) + amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, int amount) {
        Validate.isTrue(amount > 0, "Amount must be greater than 0");
        setStatistic(statistic, getStatistic(statistic) - amount);
    }

    @Override
    public void setStatistic(Statistic statistic, int newValue) {
        Validate.notNull(statistic, "Statistic cannot be null");
        Validate.isTrue(statistic.getType() == Type.UNTYPED, "Must supply additional paramater for this statistic");
        Validate.isTrue(newValue >= 0, "Value must be greater than or equal to 0");
        net.minecraft.server.Statistic nmsStatistic = CraftStatistic.getNMSStatistic(statistic);
        getHandle().getStatisticManager().setStatistic(getHandle(), nmsStatistic, newValue);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) {
        incrementStatistic(statistic, material, 1);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material) {
        decrementStatistic(statistic, material, 1);
    }

    @Override
    public int getStatistic(Statistic statistic, Material material) {
        Validate.notNull(statistic, "Statistic cannot be null");
        Validate.notNull(material, "Material cannot be null");
        Validate.isTrue(statistic.getType() == Type.BLOCK || statistic.getType() == Type.ITEM, "This statistic does not take a Material parameter");
        net.minecraft.server.Statistic nmsStatistic = CraftStatistic.getMaterialStatistic(statistic, material);
        Validate.notNull(nmsStatistic, "The supplied Material does not have a corresponding statistic");
        return getHandle().getStatisticManager().getStatisticValue(nmsStatistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material, int amount) {
        Validate.isTrue(amount > 0, "Amount must be greater than 0");
        setStatistic(statistic, material, getStatistic(statistic, material) + amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material, int amount) {
        Validate.isTrue(amount > 0, "Amount must be greater than 0");
        setStatistic(statistic, material, getStatistic(statistic, material) - amount);
    }

    @Override
    public void setStatistic(Statistic statistic, Material material, int newValue) {
        Validate.notNull(statistic, "Statistic cannot be null");
        Validate.notNull(material, "Material cannot be null");
        Validate.isTrue(newValue >= 0, "Value must be greater than or equal to 0");
        Validate.isTrue(statistic.getType() == Type.BLOCK || statistic.getType() == Type.ITEM, "This statistic does not take a Material parameter");
        net.minecraft.server.Statistic nmsStatistic = CraftStatistic.getMaterialStatistic(statistic, material);
        Validate.notNull(nmsStatistic, "The supplied Material does not have a corresponding statistic");
        getHandle().getStatisticManager().setStatistic(getHandle(), nmsStatistic, newValue);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) {
        incrementStatistic(statistic, entityType, 1);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) {
        decrementStatistic(statistic, entityType, 1);
    }

    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) {
        Validate.notNull(statistic, "Statistic cannot be null");
        Validate.notNull(entityType, "EntityType cannot be null");
        Validate.isTrue(statistic.getType() == Type.ENTITY, "This statistic does not take an EntityType parameter");
        net.minecraft.server.Statistic nmsStatistic = CraftStatistic.getEntityStatistic(statistic, entityType);
        Validate.notNull(nmsStatistic, "The supplied EntityType does not have a corresponding statistic");
        return getHandle().getStatisticManager().getStatisticValue(nmsStatistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int amount) {
        Validate.isTrue(amount > 0, "Amount must be greater than 0");
        setStatistic(statistic, entityType, getStatistic(statistic, entityType) + amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {
        Validate.isTrue(amount > 0, "Amount must be greater than 0");
        setStatistic(statistic, entityType, getStatistic(statistic, entityType) - amount);
    }

    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int newValue) {
        Validate.notNull(statistic, "Statistic cannot be null");
        Validate.notNull(entityType, "EntityType cannot be null");
        Validate.isTrue(newValue >= 0, "Value must be greater than or equal to 0");
        Validate.isTrue(statistic.getType() == Type.ENTITY, "This statistic does not take an EntityType parameter");
        net.minecraft.server.Statistic nmsStatistic = CraftStatistic.getEntityStatistic(statistic, entityType);
        Validate.notNull(nmsStatistic, "The supplied EntityType does not have a corresponding statistic");
        getHandle().getStatisticManager().setStatistic(getHandle(), nmsStatistic, newValue);
    }

    @Override
    public void setPlayerTime(long time, boolean relative) {
        getHandle().timeOffset = time;
        getHandle().relativeTime = relative;
    }

    @Override
    public long getPlayerTimeOffset() {
        return getHandle().timeOffset;
    }

    @Override
    public long getPlayerTime() {
        return getHandle().getPlayerTime();
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return getHandle().relativeTime;
    }

    @Override
    public void resetPlayerTime() {
        setPlayerTime(0, true);
    }

    @Override
    public void setPlayerWeather(WeatherType type) {
        getHandle().setPlayerWeather(type, true);
    }

    @Override
    public WeatherType getPlayerWeather() {
        return getHandle().getPlayerWeather();
    }

    @Override
    public void resetPlayerWeather() {
        getHandle().resetPlayerWeather();
    }

    @Override
    public boolean isBanned() {
        return server.getBanList(BanList.Type.NAME).isBanned(getName());
    }

    @Override
    public void setBanned(boolean value) {
        if (value) {
            server.getBanList(BanList.Type.NAME).addBan(getName(), null, null, null);
        } else {
            server.getBanList(BanList.Type.NAME).pardon(getName());
        }
    }

    @Override
    public boolean isWhitelisted() {
        return server.getHandle().getWhitelist().isWhitelisted(getProfile());
    }

    @Override
    public void setWhitelisted(boolean value) {
        if (value) {
            server.getHandle().addWhitelist(getProfile());
        } else {
            server.getHandle().removeWhitelist(getProfile());
        }
    }

    @Override
    public void setGameMode(GameMode mode) {
        if (getHandle().playerConnection == null) return;

        if (mode == null) {
            throw new IllegalArgumentException("Mode cannot be null");
        }

        if (mode != getGameMode()) {
            PlayerGameModeChangeEvent event = new PlayerGameModeChangeEvent(this, mode);
            server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            getHandle().setSpectatorTarget(getHandle());
            getHandle().playerInteractManager.setGameMode(EnumGamemode.getById(mode.getValue()));
            getHandle().fallDistance = 0;
            getHandle().playerConnection.sendPacket(new PacketPlayOutGameStateChange(3, mode.getValue()));
        }
    }

    @Override
    public GameMode getGameMode() {
        return GameMode.getByValue(getHandle().playerInteractManager.getGameMode().getId());
    }

    @Override
    public void giveExp(int exp) {
        getHandle().giveExp(exp);
    }

    @Override
    public void giveExpLevels(int levels) {
        getHandle().levelDown(levels);
    }

    @Override
    public float getExp() {
        return getHandle().exp;
    }

    @Override
    public void setExp(float exp) {
        getHandle().exp = exp;
        getHandle().lastSentExp = -1;
    }

    @Override
    public int getLevel() {
        return getHandle().expLevel;
    }

    @Override
    public void setLevel(int level) {
        getHandle().expLevel = level;
        getHandle().lastSentExp = -1;
    }

    @Override
    public int getTotalExperience() {
        return getHandle().expTotal;
    }

    @Override
    public void setTotalExperience(int exp) {
        getHandle().expTotal = exp;
    }

    @Override
    public float getExhaustion() {
        return getHandle().getFoodData().exhaustionLevel;
    }

    @Override
    public void setExhaustion(float value) {
        getHandle().getFoodData().exhaustionLevel = value;
    }

    @Override
    public float getSaturation() {
        return getHandle().getFoodData().saturationLevel;
    }

    @Override
    public void setSaturation(float value) {
        getHandle().getFoodData().saturationLevel = value;
    }

    @Override
    public int getFoodLevel() {
        return getHandle().getFoodData().foodLevel;
    }

    @Override
    public void setFoodLevel(int value) {
        getHandle().getFoodData().foodLevel = value;
    }

    @Override
    public boolean getFastNaturalRegeneration() {
        return getHandle().fastNaturalRegeneration;
    }

    @Override
    public void setFastNaturalRegeneration(boolean enabled) {
        getHandle().fastNaturalRegeneration = enabled;
    }

    @Override
    public boolean getSlowNaturalRegeneration() {
        return getHandle().slowNaturalRegeneration;
    }

    @Override
    public void setSlowNaturalRegeneration(boolean enabled) {
        getHandle().slowNaturalRegeneration = enabled;
    }

    @Override
    public Location getBedSpawnLocation() {
        World world = getServer().getWorld(getHandle().spawnWorld);
        BlockPosition bed = getHandle().getBed();

        if (world != null && bed != null) {
            bed = EntityHuman.getBed(((CraftWorld) world).getHandle(), bed, getHandle().isRespawnForced());
            if (bed != null) {
                return new Location(world, bed.getX(), bed.getY(), bed.getZ());
            }
        }
        return null;
    }

    @Override
    public void setBedSpawnLocation(Location location) {
        setBedSpawnLocation(location, false);
    }

    @Override
    public void setBedSpawnLocation(Location location, boolean override) {
        if (location == null) {
            getHandle().setRespawnPosition(null, override);
        } else {
            getHandle().setRespawnPosition(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), override);
            getHandle().spawnWorld = location.getWorld().getName();
        }
    }

    @Override
    public void hidePlayer(Player player) {
        Validate.notNull(player, "hidden player cannot be null");
        if (getHandle().playerConnection == null) return;
        if (equals(player)) return;
        if (hiddenPlayers.contains(player.getUniqueId())) return;
        hiddenPlayers.add(player.getUniqueId());

        // Remove this player from the hidden player's EntityTrackerEntry
        EntityTracker tracker = ((WorldServer) entity.world).tracker;
        EntityPlayer other = ((CraftPlayer) player).getHandle();
        EntityTrackerEntry entry = (EntityTrackerEntry) tracker.trackedEntities.get(other.getId());
        if (entry != null) {
            entry.clear(getHandle());
        }

        // Remove the hidden player from this player user list
        if (!other.joining) {
            getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, other));
        }
    }

    @Override
    public void showPlayer(Player player) {
        Validate.notNull(player, "shown player cannot be null");
        if (getHandle().playerConnection == null) return;
        if (equals(player)) return;
        if (!hiddenPlayers.contains(player.getUniqueId())) return;
        hiddenPlayers.remove(player.getUniqueId());

        EntityTracker tracker = ((WorldServer) entity.world).tracker;
        EntityPlayer other = ((CraftPlayer) player).getHandle();

        getHandle().playerConnection.sendPacket(other.getBukkitEntity().makePlayerListAddPacket(this));

        EntityTrackerEntry entry = (EntityTrackerEntry) tracker.trackedEntities.get(other.getId());
        if (entry != null && !entry.trackedPlayers.contains(getHandle())) {
            entry.updatePlayer(getHandle());
        }
    }

    public void removeDisconnectingPlayer(Player player) {
        hiddenPlayers.remove(player.getUniqueId());
    }

    @Override
    public boolean canSee(Player player) {
        return !hiddenPlayers.contains(player.getUniqueId());
    }

    @Override
    public boolean canSeeInvisibles() {
        return this.showInvisibles;
    }

    @Override
    public void showInvisibles(boolean show) {
        if(show != this.showInvisibles) {
            this.showInvisibles = show;
            EntityTracker tracker = ((WorldServer) this.entity.world).tracker;
            for(Entity entity : getHandle().world.entityList) {
                if(entity.isInvisible()) {
                    EntityTrackerEntry trackerEntry = tracker.trackedEntities.get(entity.getId());
                    if(trackerEntry != null && trackerEntry.trackedPlayers.contains(getHandle())) {
                        getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true));
                    }
                }
            }
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("name", getName());

        return result;
    }

    @Override
    public Player getPlayer() {
        return this;
    }

    @Override
    public EntityPlayer getHandle() {
        return (EntityPlayer) entity;
    }

    public void setHandle(final EntityPlayer entity) {
        super.setHandle(entity);
    }

    @Override
    public String toString() {
        return "CraftPlayer{" + "name=" + getName() + '}';
    }

    @Override
    public int hashCode() {
        if (hash == 0 || hash == 485) {
            hash = 97 * 5 + (this.getUniqueId() != null ? this.getUniqueId().hashCode() : 0);
        }
        return hash;
    }

    @Override
    public long getFirstPlayed() {
        return firstPlayed;
    }

    @Override
    public long getLastPlayed() {
        return lastPlayed;
    }

    @Override
    public boolean hasPlayedBefore() {
        return hasPlayedBefore;
    }

    public void setFirstPlayed(long firstPlayed) {
        this.firstPlayed = firstPlayed;
    }

    public void readExtraData(NBTTagCompound nbttagcompound) {
        hasPlayedBefore = true;
        if (nbttagcompound.hasKey("bukkit")) {
            NBTTagCompound data = nbttagcompound.getCompound("bukkit");

            if (data.hasKey("firstPlayed")) {
                firstPlayed = data.getLong("firstPlayed");
                lastPlayed = data.getLong("lastPlayed");
            }

            if (data.hasKey("newExp")) {
                EntityPlayer handle = getHandle();
                handle.newExp = data.getInt("newExp");
                handle.newTotalExp = data.getInt("newTotalExp");
                handle.newLevel = data.getInt("newLevel");
                handle.expToDrop = data.getInt("expToDrop");
                handle.keepLevel = data.getBoolean("keepLevel");
            }
        }
    }

    public void setExtraData(NBTTagCompound nbttagcompound) {
        if (!nbttagcompound.hasKey("bukkit")) {
            nbttagcompound.set("bukkit", new NBTTagCompound());
        }

        NBTTagCompound data = nbttagcompound.getCompound("bukkit");
        EntityPlayer handle = getHandle();
        data.setInt("newExp", handle.newExp);
        data.setInt("newTotalExp", handle.newTotalExp);
        data.setInt("newLevel", handle.newLevel);
        data.setInt("expToDrop", handle.expToDrop);
        data.setBoolean("keepLevel", handle.keepLevel);
        data.setLong("firstPlayed", getFirstPlayed());
        data.setLong("lastPlayed", System.currentTimeMillis());
        data.setString("lastKnownName", handle.getName());
    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return conversationTracker.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation) {
        conversationTracker.abandonConversation(conversation, new ConversationAbandonedEvent(conversation, new ManuallyAbandonedConversationCanceller()));
    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
        conversationTracker.abandonConversation(conversation, details);
    }

    @Override
    public void acceptConversationInput(String input) {
        conversationTracker.acceptConversationInput(input);
    }

    @Override
    public boolean isConversing() {
        return conversationTracker.isConversing();
    }

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        StandardMessenger.validatePluginMessage(server.getMessenger(), source, channel, message);
        if (getHandle().playerConnection == null) return;

        if (channels.contains(channel)) {
            PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload(channel, new PacketDataSerializer(Unpooled.wrappedBuffer(message)));
            getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void setTexturePack(String url) {
        setResourcePack(url);
    }

    @Override
    public void setResourcePack(String url) {
        setResourcePack(url, "null");
    }

    @Override
    public void setResourcePack(String url, String hash) {
        Validate.notNull(url, "Resource pack URL cannot be null");
        Validate.notNull(hash, "Hash cannot be null");
        Validate.isTrue(hash.matches("^[0-9a-f]{40}$"), "Hash must be a 40 character SHA-1 digest with lowercase letters");

        getHandle().setResourcePack(url, hash);
    }

    @Override
    public PlayerResourcePackStatusEvent.Status getResourcePackStatus() {
        return resourcePackStatus;
    }

    @Override
    public boolean hasResourcePack() {
        return this.resourcePackStatus == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED;
    }

    public void setResourcePackStatus(PlayerResourcePackStatusEvent.Status status) {
        this.resourcePackStatus = status;
    }

    public void addChannel(String channel) {
        if (channels.add(channel)) {
            server.getPluginManager().callEvent(new PlayerRegisterChannelEvent(this, channel));
        }
    }

    public void removeChannel(String channel) {
        if (channels.remove(channel)) {
            server.getPluginManager().callEvent(new PlayerUnregisterChannelEvent(this, channel));
        }
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return ImmutableSet.copyOf(channels);
    }

    public void sendSupportedChannels() {
        if (getHandle().playerConnection == null) return;
        Set<String> listening = server.getMessenger().getIncomingChannels();

        if (!listening.isEmpty()) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            for (String channel : listening) {
                try {
                    stream.write(channel.getBytes("UTF8"));
                    stream.write((byte) 0);
                } catch (IOException ex) {
                    Logger.getLogger(CraftPlayer.class.getName()).log(Level.SEVERE, "Could not send Plugin Channel REGISTER to " + getName(), ex);
                }
            }

            getHandle().playerConnection.sendPacket(new PacketPlayOutCustomPayload("REGISTER", new PacketDataSerializer(Unpooled.wrappedBuffer(stream.toByteArray()))));
        }
    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        server.getPlayerMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public MetadataValue getMetadata(String metadataKey, Plugin owningPlugin) {
        return server.getPlayerMetadata().getMetadata(this, metadataKey, owningPlugin);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return server.getPlayerMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return server.getPlayerMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        server.getPlayerMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    @Override
    public boolean setWindowProperty(Property prop, int value) {
        Container container = getHandle().activeContainer;
        if (container.getBukkitView().getType() != prop.getType()) {
            return false;
        }
        getHandle().setContainerData(container, prop.getId(), value);
        return true;
    }

    public void disconnect(String reason) {
        this.clearFakeNames();
        conversationTracker.abandonAllConversations();
        perm.clearPermissions();
    }

    @Override
    public boolean isFlying() {
        return getHandle().abilities.isFlying;
    }

    @Override
    public void setFlying(boolean value) {
        if (!getAllowFlight() && value) {
            throw new IllegalArgumentException("Cannot make player fly if getAllowFlight() is false");
        }

        getHandle().abilities.isFlying = value;
        getHandle().updateAbilities();
    }

    public void setCollidesWithEntities(boolean yes) {
        getHandle().collidesWithEntities = yes;
        getHandle().setCanObstruct(yes);
    }

    public boolean getCollidesWithEntities() {
        return getHandle().collidesWithEntities;
    }

    @Override
    public boolean getAllowFlight() {
        return getHandle().abilities.canFly;
    }

    @Override
    public void setAllowFlight(boolean value) {
        if (isFlying() && !value) {
            getHandle().abilities.isFlying = false;
        }

        getHandle().abilities.canFly = value;
        getHandle().updateAbilities();
    }

    @Override
    public int getNoDamageTicks() {
        return Math.max(getHandle().invulnerableTicks,
                        Math.max(0, getHandle().noDamageTicks - getHandle().maxNoDamageTicks / 2));
    }

    @Override
    public void setFlySpeed(float value) {
        validateSpeed(value);
        EntityPlayer player = getHandle();
        player.abilities.flySpeed = value / 2f;
        player.updateAbilities();

    }

    @Override
    public void setWalkSpeed(float value) {
        validateSpeed(value);
        EntityPlayer player = getHandle();
        player.abilities.walkSpeed = value / 2f;
        player.updateAbilities();
    }

    @Override
    public float getFlySpeed() {
        return getHandle().abilities.flySpeed * 2f;
    }

    @Override
    public float getWalkSpeed() {
        return getHandle().abilities.walkSpeed * 2f;
    }

    private void validateSpeed(float value) {
        if (value < 0) {
            if (value < -1f) {
                throw new IllegalArgumentException(value + " is too low");
            }
        } else {
            if (value > 1f) {
                throw new IllegalArgumentException(value + " is too high");
            }
        }
    }

    @Override
    public void setMaxHealth(double amount) {
        super.setMaxHealth(amount);
        this.health = Math.min(this.health, health);
        getHandle().triggerHealthUpdate();
    }

    @Override
    public void resetMaxHealth() {
        super.resetMaxHealth();
        getHandle().triggerHealthUpdate();
    }

    @Override
    public CraftScoreboard getScoreboard() {
        return this.server.getScoreboardManager().getPlayerBoard(this);
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) {
        Validate.notNull(scoreboard, "Scoreboard cannot be null");
        PlayerConnection playerConnection = getHandle().playerConnection;
        if (playerConnection == null) {
            throw new IllegalStateException("Cannot set scoreboard yet");
        }
        if (playerConnection.isDisconnected()) {
            return;
        }

        this.server.getScoreboardManager().setPlayerBoard(this, scoreboard);
    }

    @Override
    public void setHealthScale(double value) {
        Validate.isTrue((float) value > 0F, "Must be greater than 0");
        healthScale = value;
        scaledHealth = true;
        updateScaledHealth();
    }

    @Override
    public double getHealthScale() {
        return healthScale;
    }

    @Override
    public void setHealthScaled(boolean scale) {
        if (scaledHealth != (scaledHealth = scale)) {
            updateScaledHealth();
        }
    }

    @Override
    public boolean isHealthScaled() {
        return scaledHealth;
    }

    public float getScaledHealth() {
        return (float) (isHealthScaled() ? getHealth() * getHealthScale() / getMaxHealth() : getHealth());
    }

    @Override
    public double getHealth() {
        return health;
    }

    public void setRealHealth(double health) {
        this.health = health;
    }

    public void updateScaledHealth() {
        AttributeMapServer attributemapserver = (AttributeMapServer) getHandle().getAttributeMap();
        Collection set = attributemapserver.c(); // PAIL: Rename

        injectScaledMaxHealth(set, true);

        getHandle().getDataWatcher().set(EntityLiving.HEALTH, (float) getScaledHealth());
        sendHealthUpdate();
        getHandle().playerConnection.sendPacket(new PacketPlayOutUpdateAttributes(getHandle().getId(), set));

        getHandle().maxHealthCache = getMaxHealth();
    }

    public void sendHealthUpdate() {
        getHandle().playerConnection.sendPacket(new PacketPlayOutUpdateHealth(getScaledHealth(), getHandle().getFoodData().getFoodLevel(), getHandle().getFoodData().getSaturationLevel()));
    }

    public void injectScaledMaxHealth(Collection collection, boolean force) {
        if (!scaledHealth && !force) {
            return;
        }
        for (Object genericInstance : collection) {
            IAttribute attribute = ((AttributeInstance) genericInstance).getAttribute();
            if (attribute.getName().equals("generic.maxHealth")) {
                collection.remove(genericInstance);
                break;
            }
        }
        collection.add(new AttributeModifiable(getHandle().getAttributeMap(), (new AttributeRanged(null, "generic.maxHealth", scaledHealth ? healthScale : getMaxHealth(), 0.0D, Float.MAX_VALUE)).a("Max Health").a(true)));
    }

    @Override
    public org.bukkit.entity.Entity getSpectatorTarget() {
        Entity followed = getHandle().getSpecatorTarget();
        return followed == getHandle() ? null : followed.getBukkitEntity();
    }

    @Override
    public void setSpectatorTarget(org.bukkit.entity.Entity entity) {
        Preconditions.checkArgument(getGameMode() == GameMode.SPECTATOR, "Player must be in spectator mode");
        getHandle().setSpectatorTarget((entity == null) ? null : ((CraftEntity) entity).getHandle());
    }

    @Override
    public void sendTitle(String title, String subtitle) {
        sendTitle(title, subtitle, 10, 70, 20);
    }

    @Override
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (title != null) {
            PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, CraftChatMessage.fromString(title)[0]);
            getHandle().playerConnection.sendPacket(packetTitle);
        }

        if (subtitle != null) {
            PacketPlayOutTitle packetSubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, CraftChatMessage.fromString(subtitle)[0]);
            getHandle().playerConnection.sendPacket(packetSubtitle);
        }

        PacketPlayOutTitle times = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
        getHandle().playerConnection.sendPacket(times);
    }

    @Override
    public void resetTitle() {
        PacketPlayOutTitle packetReset = new PacketPlayOutTitle(EnumTitleAction.RESET, null);
        getHandle().playerConnection.sendPacket(packetReset);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count) {
        spawnParticle(particle, x, y, z, count, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, T data) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, T data) {
        spawnParticle(particle, x, y, z, count, 0, 0, 0, data);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ) {
        spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, 1, data);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        if (data != null && !particle.getDataType().isInstance(data)) {
            throw new IllegalArgumentException("data should be " + particle.getDataType() + " got " + data.getClass());
        }
        PacketPlayOutWorldParticles packetplayoutworldparticles = new PacketPlayOutWorldParticles(CraftParticle.toNMS(particle), true, (float) x, (float) y, (float) z, (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count, CraftParticle.toData(particle, data));
        getHandle().playerConnection.sendPacket(packetplayoutworldparticles);

    }

    @Override
    public String getHostname() {
        return getHandle().hostname;
    }

    @Override
    public String getLocale() {
        final String locale = getHandle().locale;
        return locale != null ? locale : "en_US";
    }

    @Override
    public void setLocale(String locale) {
        getHandle().locale = locale;
    }

    @Override
    public java.util.Locale getCurrentLocale() {
        try {
            final String[] parts = getLocale().split("[-_]");
            switch(parts.length) {
                case 0: return java.util.Locale.US;
                case 1: return new Locale(parts[0]);
                case 2: return new Locale(parts[0], parts[1]);
                default: return new Locale(parts[0], parts[1], parts[2]);
            }
        } catch(IllegalArgumentException e) {
            return java.util.Locale.US;
        }
    }

    @Override
    public void sendMessage(net.md_5.bungee.api.ChatMessageType position, BaseComponent message) {
        if ( getHandle().playerConnection == null ) return;
        getHandle().playerConnection.sendPacket(new PacketPlayOutChat(message, position));
    }

    @Override
    public void sendMessage(net.md_5.bungee.api.ChatMessageType position, BaseComponent... messages) {
        sendMessage(position, new TextComponent(messages));
    }

    @Override
    public void sendMessage(BaseComponent component) {
      sendMessage(ChatMessageType.CHAT, component);
    }

    @Override
    public void sendMessage(BaseComponent... components) {
        sendMessage(ChatMessageType.CHAT, components);
    }

    @Override
    public void setPlayerListHeaderFooter(BaseComponent header, BaseComponent footer) {
        getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerListHeaderFooter(header, footer));
    }

    @Override
    public void setPlayerListHeaderFooter(BaseComponent[] header, BaseComponent[] footer) {
        this.setPlayerListHeaderFooter(header == null ? null : new TextComponent(header),
                                       footer == null ? null : new TextComponent(footer));
    }

    @Override
    public void setTitleTimes(int fadeInTicks, int stayTicks, int fadeOutTicks) {
        getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, (BaseComponent) null, fadeInTicks, stayTicks, fadeOutTicks));
    }

    @Override
    public void setSubtitle(BaseComponent[] subtitle) {
        setSubtitle(new TextComponent(subtitle));
    }

    @Override
    public void setSubtitle(BaseComponent subtitle) {
        getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitle, 0, 0, 0));
    }

    @Override
    public void showTitle(BaseComponent[] title) {
        showTitle(new TextComponent(title));
    }

    @Override
    public void showTitle(BaseComponent title) {
        getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, title, 0, 0, 0));
    }

    @Override
    public void showTitle(BaseComponent[] title, BaseComponent[] subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        setTitleTimes(fadeInTicks, stayTicks, fadeOutTicks);
        setSubtitle(subtitle);
        showTitle(title);
    }

    @Override
    public void showTitle(BaseComponent title, BaseComponent subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        setTitleTimes(fadeInTicks, stayTicks, fadeOutTicks);
        setSubtitle(subtitle);
        showTitle(title);
    }

    @Override
    public void hideTitle() {
        getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.CLEAR, (BaseComponent) null, 0, 0, 0));
    }

    @Override
    public Vector getPredictedVelocity() {
        return getHandle().getPredictedVelocity();
    }

    @Override
    public Vector getClientVelocity() {
        return getHandle().getClientVelocity();
    }

    @Override
    public Vector getUnackedImpulse() {
        return getHandle().getUnackedImpulse();
    }

    @Override
    public boolean hasUnackedVelocity() {
        return getHandle().hasUnackedVelocity();
    }

    @Override
    public boolean teleportRelative(org.bukkit.util.Vector deltaPos, float deltaYaw, float deltaPitch) {
        return teleportRelative(deltaPos, deltaYaw, deltaPitch, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public boolean teleportRelative(org.bukkit.util.Vector deltaPos, float deltaYaw, float deltaPitch, PlayerTeleportEvent.TeleportCause cause) {
        return !(getHandle().playerConnection == null || getHandle().playerConnection.isDisconnected()) &&
               getHandle().playerConnection.teleportRelative(deltaPos.getX(), deltaPos.getY(), deltaPos.getZ(),
                                                             deltaYaw, deltaPitch,
                                                             java.util.EnumSet.allOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class),
                                                             cause);
    }

    @Override
    public boolean isDigging() {
        return getHandle().playerInteractManager.d;
    }

    @Override
    public org.bukkit.block.Block getDiggingBlock() {
        BlockPosition pos = getHandle().playerInteractManager.f;
        return getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public double getBlockReach() {
        return getGameMode() == GameMode.CREATIVE ? 6 : 4.5;
    }

    @Override
    public RayBlockIntersection getTargetedBlock(double distance, boolean nonSolids, boolean liquids) {
        return getWorld().rayTraceBlock(getEyeLocation(), distance, nonSolids, liquids);
    }

    @Override
    public RayBlockIntersection getTargetedBlock(boolean nonSolids, boolean liquids) {
        return getTargetedBlock(getBlockReach(), nonSolids, liquids);
    }

    @Override
    public int getProtocolVersion() {
        return getHandle().protocolVersion;
    }

    @Override
    public void startItemCooldown(Material item, int ticks) {
        getHandle().getItemCooldown().a(CraftMagicNumbers.getItem(item), ticks);
    }

    @Override
    public float getRemainingItemCooldown(Material item) {
        return getHandle().getItemCooldown().a(CraftMagicNumbers.getItem(item), 0F);
    }

    @Override
    public EnumSet<PoseFlag> getPoseFlags() {
        final EnumSet<PoseFlag> flags = super.getPoseFlags();
        if(isSneaking()) flags.add(PoseFlag.SNEAKING);
        if(isSprinting()) flags.add(PoseFlag.SPRINTING);
        if(isDigging()) flags.add(PoseFlag.DIGGING);
        if(isFlying()) flags.add(PoseFlag.FLYING);
        return flags;
    }
}
