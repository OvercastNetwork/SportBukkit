package org.bukkit.craftbukkit.util;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.minecraft.server.ChatClickable;
import net.minecraft.server.ChatClickable.EnumClickAction;
import net.minecraft.server.ChatComponentScore;
import net.minecraft.server.ChatComponentSelector;
import net.minecraft.server.ChatComponentText;
import net.minecraft.server.ChatHoverable;
import net.minecraft.server.ChatHoverable.EnumHoverAction;
import net.minecraft.server.ChatMessage;
import net.minecraft.server.ChatModifier;
import net.minecraft.server.EnumChatFormat;
import net.minecraft.server.IChatBaseComponent;

public final class BungeeChatUtils {
    private BungeeChatUtils() {}

    private static <T extends Enum<T>> T convertEnum(Enum<?> from, Class<T> to) {
        return from == null ? null : to.getEnumConstants()[from.ordinal()];
    }

    public static EnumChatFormat toNms(ChatColor bungee) {
        return convertEnum(bungee, EnumChatFormat.class);
    }

    public static EnumHoverAction toNms(HoverEvent.Action bungee) {
        return convertEnum(bungee, EnumHoverAction.class);
    }

    public static EnumClickAction toNms(ClickEvent.Action bungee) {
        return convertEnum(bungee, EnumClickAction.class);
    }

    public static ChatHoverable toNms(HoverEvent bungee) {
        return bungee == null ? null : new ChatHoverable(toNms(bungee.getAction()), toNms(bungee.getValue()));
    }

    public static ChatClickable toNms(ClickEvent bungee) {
        return bungee == null ? null : new ChatClickable(toNms(bungee.getAction()), bungee.getValue());
    }

    public static ChatModifier toNmsModifier(BaseComponent bungee) {
        final ChatModifier mod = new ChatModifier();

        mod.setColor(toNms(bungee.getColorRaw()));

        mod.setRandom(bungee.isObfuscatedRaw());
        mod.setBold(bungee.isBoldRaw());
        mod.setStrikethrough(bungee.isStrikethroughRaw());
        mod.setUnderline(bungee.isUnderlinedRaw());
        mod.setItalic(bungee.isItalicRaw());

        mod.setChatHoverable(toNms(bungee.getHoverEvent()));
        mod.setChatClickable(toNms(bungee.getClickEvent()));
        mod.setInsertion(bungee.getInsertion());

        return mod;
    }

    public static IChatBaseComponent toNms(BaseComponent bungee) {
        final IChatBaseComponent nms;
        if(bungee instanceof TextComponent) {
            nms = new ChatComponentText(((TextComponent) bungee).getText());
        } else if(bungee instanceof TranslatableComponent) {
            final TranslatableComponent trans = (TranslatableComponent) bungee;
            if(trans.getWith() == null || trans.getWith().isEmpty()) {
                nms = new ChatMessage(trans.getTranslate());
            } else {
                final IChatBaseComponent[] with = new IChatBaseComponent[trans.getWith().size()];
                for(int i = 0; i < trans.getWith().size(); i++) {
                    with[i] = toNms(trans.getWith().get(i));
                }
                nms = new ChatMessage(trans.getTranslate(), with);
            }
        } else if(bungee instanceof SelectorComponent) {
            nms = new ChatComponentSelector(((SelectorComponent) bungee).getSelector());
        } else if(bungee instanceof ScoreComponent) {
            nms = new ChatComponentScore(((ScoreComponent) bungee).getName(),
                                         ((ScoreComponent) bungee).getObjective());
        } else {
            throw new IllegalArgumentException("Don't know how to convert a " + bungee.getClass().getName());
        }

        nms.setChatModifier(toNmsModifier(bungee));

        if(bungee.getExtra() != null && !bungee.getExtra().isEmpty()) {
            for(BaseComponent extra : bungee.getExtra()) {
                nms.addSibling(toNms(extra));
            }
        }

        return nms;
    }

    public static IChatBaseComponent toNms(BaseComponent[] bungees) {
        if(bungees.length == 1) {
            return toNms(bungees[0]);
        } else {
            final IChatBaseComponent nms = new ChatComponentText("");
            for(BaseComponent bungee : bungees) {
                nms.addSibling(toNms(bungee));
            }
            return nms;
        }
    }

    public static ChatColor toBungee(EnumChatFormat nms) {
        return convertEnum(nms, ChatColor.class);
    }

    public static HoverEvent.Action toBungee(EnumHoverAction nms) {
        return convertEnum(nms, HoverEvent.Action.class);
    }

    public static ClickEvent.Action toBungee(EnumClickAction nms) {
        return convertEnum(nms, ClickEvent.Action.class);
    }

    public static HoverEvent toBungee(ChatHoverable nms) {
        return nms == null ? null : new HoverEvent(toBungee(nms.a()), new BaseComponent[]{ toBungee(nms.b()) });
    }

    public static ClickEvent toBungee(ChatClickable nms) {
        return nms == null ? null : new ClickEvent(toBungee(nms.a()), nms.b());
    }

    public static void toBungeeModifier(BaseComponent bungee, ChatModifier nms) {
        bungee.setColor(toBungee(nms.getColorRaw()));

        bungee.setObfuscated(nms.isRandomRaw());
        bungee.setBold(nms.isBoldRaw());
        bungee.setStrikethrough(nms.isStrikethroughRaw());
        bungee.setUnderlined(nms.isUnderlinedRaw());
        bungee.setItalic(nms.isItalicRaw());

        bungee.setHoverEvent(toBungee(nms.getHoverableRaw()));
        bungee.setClickEvent(toBungee(nms.getClickableRaw()));
        bungee.setInsertion(nms.getInsertionRaw());
    }

    public static BaseComponent toBungee(IChatBaseComponent nms) {
        if(nms == null) return new TextComponent();
        
        final BaseComponent bungee;
        if(nms instanceof ChatComponentText) {
            bungee = new TextComponent(((ChatComponentText) nms).g());
        } else if(nms instanceof ChatMessage) {
            final ChatMessage nmsMessage = (ChatMessage) nms;
            final BaseComponent[] with = new BaseComponent[nmsMessage.j().length];
            for(int i = 0; i < nmsMessage.j().length; i++) {
                final Object o = nmsMessage.j()[i];
                with[i] = o instanceof IChatBaseComponent ? toBungee((IChatBaseComponent) o)
                                                          : new TextComponent(String.valueOf(o));
            }
            bungee = new TranslatableComponent(nmsMessage.i(), with);
        } else if(nms instanceof ChatComponentSelector) {
            bungee = new SelectorComponent(((ChatComponentSelector) nms).g());
        } else if(nms instanceof ChatComponentScore) {
            bungee = new ScoreComponent(((ChatComponentScore) nms).g(), ((ChatComponentScore) nms).h());
        } else {
            throw new IllegalArgumentException("Don't know how to convert a " + nms.getClass().getName());
        }

        toBungeeModifier(bungee, nms.getChatModifier());

        if(!nms.a().isEmpty()) {
            List<BaseComponent> extras = new ArrayList<BaseComponent>(nms.a().size());
            for(IChatBaseComponent c : nms.a()) {
                extras.add(toBungee(c));
            }
            bungee.setExtra(extras);
        }

        return bungee;
    }
}
