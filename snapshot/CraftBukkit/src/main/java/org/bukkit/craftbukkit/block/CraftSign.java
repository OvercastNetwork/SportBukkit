package org.bukkit.craftbukkit.block;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.TileEntitySign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.BungeeChatUtils;

public class CraftSign extends CraftBlockState implements Sign {
    private final TileEntitySign sign;
    private final BaseComponent[] lines = new BaseComponent[4];

    public CraftSign(final Block block) {
        super(block);

        CraftWorld world = (CraftWorld) block.getWorld();
        sign = (TileEntitySign) world.getTileEntityAt(getX(), getY(), getZ());
        importFromNms();
    }

    public CraftSign(final Material material, final TileEntitySign te) {
        super(material);
        sign = te;
        importFromNms();
    }

    private void importFromNms() {
        for(int i = 0; i < lines.length; i++) {
            lines[i] = sign.lines.length > i ? BungeeChatUtils.toBungee(sign.lines[i])
                                             : new TextComponent();
        }
    }

    @Override
    public BaseComponent[] lines() {
        return lines;
    }

    @Override
    public BaseComponent line(int index) {
        return lines[index];
    }

    @Override
    public void setLine(int index, BaseComponent line) {
        lines[index] = line;
    }

    public String[] getLines() {
        return BaseComponent.toLegacyArray(lines);
    }

    public String getLine(int index) throws IndexOutOfBoundsException {
        return lines[index].toLegacyText();
    }

    public void setLine(int index, String line) throws IndexOutOfBoundsException {
        lines[index] = TextComponent.fromLegacyToComponent(line, false);
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);

        if (result) {
            for(int i = 0; i < sign.lines.length; i++) {
                sign.lines[i] = BungeeChatUtils.toNms(lines[i]);
            }
            sign.update();
        }

        return result;
    }

    @Override
    public TileEntitySign getTileEntity() {
        return sign;
    }
}
