
package org.bukkit.craftbukkit.command;

import java.util.Collection;
import java.util.Set;

import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.ICommandListener;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class ProxiedNativeCommandSender implements ProxiedCommandSender {

    private final ICommandListener orig;
    private final CommandSender caller;
    private final CommandSender callee;

    public ProxiedNativeCommandSender(ICommandListener orig, CommandSender caller, CommandSender callee) {
        this.orig = orig;
        this.caller = caller;
        this.callee = callee;
    }

    public ICommandListener getHandle() {
        return orig;
    }

    @Override
    public CommandSender getCaller() {
        return caller;
    }

    @Override
    public CommandSender getCallee() {
        return callee;
    }

    @Override
    public void sendMessage(String message) {
        getCaller().sendMessage(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        getCaller().sendMessage(messages);
    }

    @Override
    public void sendMessage(BaseComponent... message) {
        getCaller().sendMessage(message);
    }

    @Override
    public void sendMessage(BaseComponent message) {
        getCaller().sendMessage(message);
    }

    @Override
    public Server getServer() {
        return getCallee().getServer();
    }

    @Override
    public String getName() {
        return getCallee().getName();
    }

    @Override
    public String getName(CommandSender viewer) {
        return getCallee().getName(viewer);
    }

    @Override
    public boolean isPermissionSet(String name) {
        return getCaller().isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return getCaller().isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
        return getCaller().hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return getCaller().hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return getCaller().addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return getCaller().addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return getCaller().addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return getCaller().addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        getCaller().removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        getCaller().recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return getCaller().getEffectivePermissions();
    }

    @Override
    public boolean removeAttachments(Plugin plugin) {
        return getCaller().removeAttachments(plugin);
    }

    @Override
    public boolean removeAttachments(String name) {
        return getCaller().removeAttachments(name);
    }

    @Override
    public boolean removeAttachments(Permission permission) {
        return getCaller().removeAttachments(permission);
    }

    @Override
    public boolean removeAttachments(Plugin plugin, String name) {
        return getCaller().removeAttachments(plugin, name);
    }

    @Override
    public boolean removeAttachments(Plugin plugin, Permission permission) {
        return getCaller().removeAttachments(plugin, permission);
    }

    @Override
    public PermissionAttachmentInfo getEffectivePermission(String name) {
        return getCaller().getEffectivePermission(name);
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments() {
        return getCaller().getAttachments();
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments(Plugin plugin) {
        return getCaller().getAttachments(plugin);
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments(String name) {
        return getCaller().getAttachments(name);
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments(Permission permission) {
        return getCaller().getAttachments(permission);
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments(Plugin plugin, String name) {
        return getCaller().getAttachments(plugin, name);
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments(Plugin plugin, Permission permission) {
        return getCaller().getAttachments(plugin, permission);
    }

    @Override
    public boolean isOp() {
        return getCaller().isOp();
    }

    @Override
    public void setOp(boolean value) {
        getCaller().setOp(value);
    }

}
