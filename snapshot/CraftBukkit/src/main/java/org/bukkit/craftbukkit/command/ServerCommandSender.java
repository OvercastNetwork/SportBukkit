package org.bukkit.craftbukkit.command;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Set;

public abstract class ServerCommandSender implements CommandSender {
    private final PermissibleBase perm = new PermissibleBase(this);

    public ServerCommandSender() {
    }

    public boolean isPermissionSet(String name) {
        return perm.isPermissionSet(name);
    }

    public boolean isPermissionSet(Permission perm) {
        return this.perm.isPermissionSet(perm);
    }

    public boolean hasPermission(String name) {
        return perm.hasPermission(name);
    }

    public boolean hasPermission(Permission perm) {
        return this.perm.hasPermission(perm);
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return perm.addAttachment(plugin, name, value);
    }

    public PermissionAttachment addAttachment(Plugin plugin) {
        return perm.addAttachment(plugin);
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return perm.addAttachment(plugin, name, value, ticks);
    }

    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return perm.addAttachment(plugin, ticks);
    }

    public void removeAttachment(PermissionAttachment attachment) {
        perm.removeAttachment(attachment);
    }

    public void recalculatePermissions() {
        perm.recalculatePermissions();
    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return perm.getEffectivePermissions();
    }

    @Override
    public boolean removeAttachments(Plugin plugin) {
        return perm.removeAttachments(plugin);
    }

    @Override
    public boolean removeAttachments(String name) {
        return perm.removeAttachments(name);
    }

    @Override
    public boolean removeAttachments(Permission permission) {
        return perm.removeAttachments(permission);
    }

    @Override
    public boolean removeAttachments(Plugin plugin, String name) {
        return perm.removeAttachments(plugin, name);
    }

    @Override
    public boolean removeAttachments(Plugin plugin, Permission permission) {
        return perm.removeAttachments(plugin, permission);
    }

    @Override
    public PermissionAttachmentInfo getEffectivePermission(String name) {
        return perm.getEffectivePermission(name);
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments() {
        return perm.getAttachments();
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments(Plugin plugin) {
        return perm.getAttachments(plugin);
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments(String name) {
        return perm.getAttachments(name);
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments(Permission permission) {
        return perm.getAttachments(permission);
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments(Plugin plugin, String name) {
        return perm.getAttachments(plugin, name);
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments(Plugin plugin, Permission permission) {
        return perm.getAttachments(plugin, permission);
    }

    public boolean isPlayer() {
        return false;
    }

    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public String getName(CommandSender viewer) {
        return this.getName();
    }

    @Override
    public void sendMessage(BaseComponent... message) {
        sendMessage(BaseComponent.toLegacyText(message));
    }

    @Override
    public void sendMessage(BaseComponent message) {
        sendMessage(BaseComponent.toLegacyText(message));
    }
}
