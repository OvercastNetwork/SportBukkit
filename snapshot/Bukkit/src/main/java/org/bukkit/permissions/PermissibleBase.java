package org.bukkit.permissions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Base Permissible for use in any Permissible object via proxy or extension
 */
public class PermissibleBase implements Permissible {
    private ServerOperator opable = null;
    private Permissible parent = this;
    private final Set<PermissionAttachment> attachments = new LinkedHashSet<PermissionAttachment>();
    private final ListMultimap<String, PermissionAttachmentInfo> permissions = ArrayListMultimap.create();

    public PermissibleBase(ServerOperator opable) {
        this.opable = opable;

        if (opable instanceof Permissible) {
            this.parent = (Permissible) opable;
        }

        recalculatePermissions();
    }

    public boolean isOp() {
        if (opable == null) {
            return false;
        } else {
            return opable.isOp();
        }
    }

    public void setOp(boolean value) {
        if (opable == null) {
            throw new UnsupportedOperationException("Cannot change op value as no ServerOperator is set");
        } else {
            opable.setOp(value);
        }
    }

    public boolean isPermissionSet(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Permission name cannot be null");
        }

        return permissions.containsKey(name.toLowerCase(java.util.Locale.ENGLISH));
    }

    public boolean isPermissionSet(Permission perm) {
        if (perm == null) {
            throw new IllegalArgumentException("Permission cannot be null");
        }

        return isPermissionSet(perm.getName());
    }

    public boolean hasPermission(String inName) {
        if (inName == null) {
            throw new IllegalArgumentException("Permission name cannot be null");
        }

        String name = inName.toLowerCase(java.util.Locale.ENGLISH);

        if (isPermissionSet(name)) {
            return getEffectivePermission(name).getValue();
        } else {
            Permission perm = Bukkit.getServer().getPluginManager().getPermission(name);

            if (perm != null) {
                return perm.getDefault().getValue(isOp());
            } else {
                return Permission.DEFAULT_PERMISSION.getValue(isOp());
            }
        }
    }

    public boolean hasPermission(Permission perm) {
        if (perm == null) {
            throw new IllegalArgumentException("Permission cannot be null");
        }

        String name = perm.getName().toLowerCase(java.util.Locale.ENGLISH);

        if (isPermissionSet(name)) {
            return getEffectivePermission(name).getValue();
        }
        return perm.getDefault().getValue(isOp());
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        if (name == null) {
            throw new IllegalArgumentException("Permission name cannot be null");
        } else if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        } else if (!plugin.isEnabled()) {
            throw new IllegalArgumentException("Plugin " + plugin.getDescription().getFullName() + " is disabled");
        }

        PermissionAttachment result = addAttachment(plugin);
        result.setPermission(name, value);

        recalculatePermissions();

        return result;
    }

    public PermissionAttachment addAttachment(Plugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        } else if (!plugin.isEnabled()) {
            throw new IllegalArgumentException("Plugin " + plugin.getDescription().getFullName() + " is disabled");
        }

        PermissionAttachment result = new PermissionAttachment(plugin, parent);

        attachments.add(result);
        recalculatePermissions();

        return result;
    }

    public void removeAttachment(PermissionAttachment attachment) {
        if (attachment == null) {
            throw new IllegalArgumentException("Attachment cannot be null");
        }

        if (attachments.contains(attachment)) {
            attachments.remove(attachment);
            PermissionRemovedExecutor ex = attachment.getRemovalCallback();

            if (ex != null) {
                ex.attachmentRemoved(attachment);
            }

            recalculatePermissions();
        } else {
            throw new IllegalArgumentException("Given attachment is not part of Permissible object " + parent);
        }
    }

    @Override
    public boolean removeAttachments(Plugin plugin) {
        boolean changed = false;
        for(Iterator<PermissionAttachment> iterator = attachments.iterator(); iterator.hasNext(); ) {
            PermissionAttachment attachment = iterator.next();
            if(attachment.getPlugin() == plugin) {
                iterator.remove();
                changed = true;
            }
        }
        if(changed) recalculatePermissions();
        return changed;
    }

    @Override
    public boolean removeAttachments(String name) {
        boolean changed = false;
        for(PermissionAttachmentInfo info : permissions.get(name.toLowerCase())) {
            attachments.remove(info.getAttachment());
            changed = true;
        }
        if(changed) recalculatePermissions();
        return changed;
    }

    @Override
    public boolean removeAttachments(Permission permission) {
        return removeAttachments(permission.getName());
    }

    @Override
    public boolean removeAttachments(Plugin plugin, String name) {
        boolean changed = false;
        for(PermissionAttachmentInfo info : permissions.get(name.toLowerCase())) {
            if(info.getAttachment().getPlugin() == plugin) {
                attachments.remove(info.getAttachment());
                changed = true;
            }
        }
        if(changed) recalculatePermissions();
        return changed;
    }

    @Override
    public boolean removeAttachments(Plugin plugin, Permission permission) {
        return removeAttachments(plugin, permission.getName());
    }

    public void recalculatePermissions() {
        clearPermissions();
        Set<Permission> defaults = Bukkit.getServer().getPluginManager().getDefaultPermissions(isOp());
        Bukkit.getServer().getPluginManager().subscribeToDefaultPerms(isOp(), parent);

        for (Permission perm : defaults) {
            String name = perm.getName().toLowerCase(java.util.Locale.ENGLISH);
            permissions.put(name, new PermissionAttachmentInfo(parent, name, null, true));
            Bukkit.getServer().getPluginManager().subscribeToPermission(name, parent);
            calculateChildPermissions(perm.getChildren(), false, null);
        }

        for (PermissionAttachment attachment : attachments) {
            calculateChildPermissions(attachment.getPermissions(), false, attachment);
        }
    }

    public synchronized void clearPermissions() {
        Set<String> perms = permissions.keySet();

        for (String name : perms) {
            Bukkit.getServer().getPluginManager().unsubscribeFromPermission(name, parent);
        }

        Bukkit.getServer().getPluginManager().unsubscribeFromDefaultPerms(false, parent);
        Bukkit.getServer().getPluginManager().unsubscribeFromDefaultPerms(true, parent);

        permissions.clear();
    }

    private void calculateChildPermissions(Map<String, Boolean> children, boolean invert, PermissionAttachment attachment) {
        Set<String> keys = children.keySet();

        for (String name : keys) {
            Permission perm = Bukkit.getServer().getPluginManager().getPermission(name);
            boolean value = children.get(name) ^ invert;
            String lname = name.toLowerCase(java.util.Locale.ENGLISH);

            permissions.put(lname, new PermissionAttachmentInfo(parent, lname, attachment, value));
            Bukkit.getServer().getPluginManager().subscribeToPermission(name, parent);

            if (perm != null) {
                calculateChildPermissions(perm.getChildren(), !value, attachment);
            }
        }
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        if (name == null) {
            throw new IllegalArgumentException("Permission name cannot be null");
        } else if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        } else if (!plugin.isEnabled()) {
            throw new IllegalArgumentException("Plugin " + plugin.getDescription().getFullName() + " is disabled");
        }

        PermissionAttachment result = addAttachment(plugin, ticks);

        if (result != null) {
            result.setPermission(name, value);
        }

        return result;
    }

    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        } else if (!plugin.isEnabled()) {
            throw new IllegalArgumentException("Plugin " + plugin.getDescription().getFullName() + " is disabled");
        }

        PermissionAttachment result = addAttachment(plugin);

        if (Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new RemoveAttachmentRunnable(result), ticks) == -1) {
            Bukkit.getServer().getLogger().log(Level.WARNING, "Could not add PermissionAttachment to " + parent + " for plugin " + plugin.getDescription().getFullName() + ": Scheduler returned -1");
            result.remove();
            return null;
        } else {
            return result;
        }
    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        HashSet<PermissionAttachmentInfo> effective = new HashSet<PermissionAttachmentInfo>(permissions.keySet().size());
        for(String name : permissions.keySet()) {
            effective.add(getEffectivePermission(name));
        }
        return effective;
    }

    @Override
    public PermissionAttachmentInfo getEffectivePermission(String name) {
        List<PermissionAttachmentInfo> list = permissions.get(name.toLowerCase());
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments() {
        return ImmutableList.copyOf(permissions.values());
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments(Plugin plugin) {
        ImmutableList.Builder<PermissionAttachmentInfo> builder = ImmutableList.builder();
        for(PermissionAttachmentInfo info : permissions.values()) {
            if(info.getAttachment() != null && info.getAttachment().getPlugin() == plugin) {
                builder.add(info);
            }
        }
        return builder.build();
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments(String name) {
        return ImmutableList.copyOf(permissions.get(name.toLowerCase()));
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments(Permission permission) {
        return getAttachments(permission.getName());
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments(Plugin plugin, String name) {
        ImmutableList.Builder<PermissionAttachmentInfo> builder = ImmutableList.builder();
        for(PermissionAttachmentInfo info : permissions.get(name.toLowerCase())) {
            if(info.getAttachment() != null && info.getAttachment().getPlugin() == plugin) {
                builder.add(info);
            }
        }
        return builder.build();
    }

    @Override
    public Collection<PermissionAttachmentInfo> getAttachments(Plugin plugin, Permission permission) {
        return getAttachments(plugin, permission.getName());
    }

    private class RemoveAttachmentRunnable implements Runnable {
        private PermissionAttachment attachment;

        public RemoveAttachmentRunnable(PermissionAttachment attachment) {
            this.attachment = attachment;
        }

        public void run() {
            attachment.remove();
        }
    }
}
