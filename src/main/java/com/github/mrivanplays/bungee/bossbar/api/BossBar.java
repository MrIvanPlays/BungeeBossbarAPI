/*
 * Copyright 2019 Ivan Pekov (MrIvanPlays)

 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
package com.github.mrivanplays.bungee.bossbar.api;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

/**
 * Represents a boss bar
 */
public class BossBar {

    private BaseComponent[] title;
    private BossBar.Color color;
    private BossBar.Style style;
    private final Set<Flag> flags;
    private float health;
    private final UUID uuid;
    private boolean visible;
    private final List<ProxiedPlayer> players;

    /**
     * Creates a new boss bar
     *
     * @param title boss bar title
     * @param color boss bar color
     * @param style boss bar style
     * @param health boss bar health. Should be a number between 0.1 and 1
     */
    public BossBar(
            BaseComponent[] title,
            BossBar.Color color,
            BossBar.Style style,
            float health
    ) {
        this.title = title;
        this.color = color;
        this.style = style;
        Preconditions.checkArgument(health < 1 || health > 0, "Health must be between 0.1 and 1");
        this.health = health;
        this.flags = EnumSet.noneOf(BossBar.Flag.class);
        this.uuid = UUID.randomUUID();
        this.players = new ArrayList<>();
        this.visible = true;
    }

    /**
     * Creates a new boss bar
     *
     * @param title boss bar title
     * @param color boss bar color
     * @param style boss bar style
     */
    public BossBar(
            BaseComponent[] title,
            BossBar.Color color,
            BossBar.Style style
    ) {
        this(title, color, style, 1);
    }

    /**
     * Creates a new boss bar
     *
     * @param title boss bar title
     */
    public BossBar(BaseComponent[] title) {
        this(title, Color.PINK, Style.SOLID);
    }

    /**
     * Returns a unmodifiable list, containing all of the players, added to this boss bar
     *
     * @return players, if no one added empty list
     */
    public Collection<ProxiedPlayer> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Adds all players to the boss bar
     *
     * @param players the players you wish to add
     * @see #addPlayer(ProxiedPlayer)
     */
    public void addPlayers(Iterable<ProxiedPlayer> players) {
        Preconditions.checkNotNull(players, "players");
        for (ProxiedPlayer player : players) {
            addPlayer(player);
        }
    }

    /**
     * Adds a player to the boss bar. This makes the player see the boss bar if it is visible.
     *
     * @param player the player you wish to add
     */
    public void addPlayer(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "player");
        if (!players.contains(player)) {
            players.add(player);
        }
        if (player.isConnected() && visible) {
            sendPacket(player, addPacket());
        }
    }

    /**
     * Removes all specified players from the boss bar
     *
     * @param players the players you wish to remove
     * @see #removePlayer(ProxiedPlayer)
     */
    public void removePlayers(Iterable<ProxiedPlayer> players) {
        Preconditions.checkNotNull(players, "players");
        for (ProxiedPlayer player : players) {
            removePlayer(player);
        }
    }

    /**
     * Removes all added players from the boss bar
     *
     * @see #removePlayers(Iterable)
     */
    public void removeAllPlayers() {
        removePlayers(players);
    }

    /**
     * Removes a player from the boss bar. This makes the player not see the boss bar no matter if it
     * is visible or not.
     *
     * @param player the player you wish to remove
     */
    public void removePlayer(ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "player");
        players.remove(player);
        if (player.isConnected() && visible) {
            sendPacket(player, removePacket());
        }
    }

    /**
     * Gets the set title of the boss bar
     *
     * @return title
     */
    public BaseComponent[] getTitle() {
        return title;
    }

    /**
     * Sets a (new) boss bar title (name)
     *
     * @param title the title you wish to set
     */
    public void setTitle(BaseComponent... title) {
        this.title = Preconditions.checkNotNull(title, "title");
        if (visible) {
            net.md_5.bungee.protocol.packet.BossBar packet = new net.md_5.bungee.protocol.packet.BossBar(
                    uuid, 3);
            packet.setTitle(ComponentSerializer.toString(title));
            sendToAffected(packet);
        }
    }

    /**
     * Gets the health of the boss bar. The health is a number between 0 and 1
     *
     * @return health
     */
    public float getHealth() {
        return health;
    }

    /**
     * Sets the health of the boss bar. The health is being represented as a number between 0 and 1.
     * The minimum is 0.1 and the maximum is 1
     *
     * @param health the health you wish to set.
     */
    public void setHealth(float health) {
        Preconditions.checkArgument(health < 1 || health > 0, "Health must be between 0.1 and 1");
        this.health = health;
        if (visible) {
            net.md_5.bungee.protocol.packet.BossBar packet = new net.md_5.bungee.protocol.packet.BossBar(
                    uuid, 2);
            packet.setHealth(health);
            sendToAffected(packet);
        }
    }

    /**
     * Gets the color of the boss bar
     *
     * @return color
     */
    public BossBar.Color getColor() {
        return color;
    }

    /**
     * Sets a (new) color of the boss bar
     *
     * @param color the color you wish to set
     */
    public void setColor(BossBar.Color color) {
        this.color = Preconditions.checkNotNull(color, "color");
        if (visible) {
            setDivisions(color, style);
        }
    }

    /**
     * Gets the style of the boss bar
     *
     * @return style
     */
    public BossBar.Style getStyle() {
        return style;
    }

    /**
     * Sets a (new) boss bar style (overlay)
     *
     * @param style the style you wish to set
     */
    public void setStyle(BossBar.Style style) {
        this.style = Preconditions.checkNotNull(style, "style");
        if (visible) {
            setDivisions(color, style);
        }
    }

    /**
     * Returns whenever this boss bar is being visible.
     *
     * @return <code>true</code> if visible, otherwise
     * <code>false</code>
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets this boss bar's visible state
     *
     * @param visible value
     */
    public void setVisible(boolean visible) {
        boolean previous = this.visible;
        if (previous && !visible) {
            sendToAffected(removePacket());
        } else if (!previous && visible) {
            sendToAffected(addPacket());
        }
        this.visible = visible;
    }

    /**
     * Returns a unmodifiable set, containing all of the flags, added to this boss bar
     *
     * @return flags, if no one added empty set
     */
    public Collection<BossBar.Flag> getFlags() {
        return Collections.unmodifiableSet(flags);
    }

    /**
     * Adds flag(s) to the boss bar
     *
     * @param flags the flag(s) you wish to add
     */
    public void addFlags(BossBar.Flag... flags) {
        addFlags(Arrays.asList(flags));
    }

    /**
     * Adds flags to the boss bar
     *
     * @param flags the flags you wish to add
     */
    public void addFlags(Collection<BossBar.Flag> flags) {
        if (this.flags.addAll(flags) && visible) {
            sendToAffected(updateFlags());
        }
    }

    /**
     * Removes a flag from the boss bar
     *
     * @param flag the flag you wish to remove
     */
    public void removeFlag(BossBar.Flag flag) {
        Preconditions.checkNotNull(flag, "flag");
        if (this.flags.remove(flag) && visible) {
            sendToAffected(updateFlags());
        }
    }

    /**
     * Removes the specified flag(s) from the boss bar
     *
     * @param flags the flag(s) you wish to remove
     */
    public void removeFlags(BossBar.Flag... flags) {
        removeFlags(Arrays.asList(flags));
    }

    /**
     * Removes the specified flags from the boss bar
     *
     * @param flags the flags you wish to remove
     */
    public void removeFlags(Collection<BossBar.Flag> flags) {
        if (this.flags.removeAll(flags) && visible) {
            sendToAffected(updateFlags());
        }
    }

    @Override
    public String toString() {
        StringBuilder titleBuilder = new StringBuilder();
        for (BaseComponent component : title) {
            titleBuilder.append(component.toString()).append(", ");
        }
        return "BossBar(" +
                "title=" + titleBuilder.toString() +
                ", color=" + color +
                ", style=" + style +
                ", flags=" + flags +
                ", health=" + health +
                ", visible=" + visible +
                ", players=" + players +
                ')';
    }

    /**
     * Represents a color of {@link BossBar}
     */
    public static enum Color {
        PINK,
        BLUE,
        RED,
        GREEN,
        YELLOW,
        PURPLE,
        WHITE;
    }

    /**
     * Represents a style of {@link BossBar}
     */
    public static enum Style {
        SOLID,
        SEGMENTED_6,
        SEGMENTED_10,
        SEGMENTED_12,
        SEGMENTED_20;
    }

    /**
     * Represents a flag of {@link BossBar}
     */
    public static enum Flag {
        DARKEN_SCREEN,
        PLAY_BOSS_MUSIC,
        CREATE_WORLD_FOG;
    }

    /**
     * Creates a new boss bar builder
     *
     * @return builder
     */
    public static BossBar.Builder builder() {
        return new BossBar.Builder();
    }

    /**
     * Represents a boss bar builder
     */
    public static final class Builder {

        private BaseComponent[] title;
        private BossBar.Color color;
        private BossBar.Style style;
        private Set<BossBar.Flag> flags;
        private float health;
        private List<ProxiedPlayer> players;
        private boolean visible;

        public Builder() {
            this.title = new ComponentBuilder("Title not specified").color(ChatColor.YELLOW).create();
            this.color = Color.PINK;
            this.style = Style.SOLID;
            this.flags = EnumSet.noneOf(BossBar.Flag.class);
            this.health = 1;
            this.players = new ArrayList<>();
            this.visible = true;
        }

        public Builder title(BaseComponent... title) {
            this.title = title;
            return this;
        }

        public Builder player(ProxiedPlayer player) {
            players.add(player);
            return this;
        }

        public Builder health(float health) {
            this.health = health;
            return this;
        }

        public Builder flags(BossBar.Flag... flags) {
            this.flags.addAll(Arrays.asList(flags));
            return this;
        }

        public Builder color(BossBar.Color color) {
            this.color = color;
            return this;
        }

        public Builder style(BossBar.Style style) {
            this.style = style;
            return this;
        }

        public Builder visible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public BossBar build() {
            BossBar bossBar = new BossBar(title, color, style, health);
            bossBar.addFlags(flags);
            bossBar.setVisible(visible);
            bossBar.addPlayers(players);
            return bossBar;
        }
    }

    //

    private byte serializeFlags() {
        byte flagMask = 0x0;
        if (flags.contains(Flag.DARKEN_SCREEN)) {
            flagMask |= 0x1;
        }
        if (flags.contains(Flag.PLAY_BOSS_MUSIC)) {
            flagMask |= 0x2;
        }
        if (flags.contains(Flag.CREATE_WORLD_FOG)) {
            flagMask |= 0x4;
        }
        return flagMask;
    }

    private void setDivisions(
            BossBar.Color color,
            BossBar.Style style
    ) {
        net.md_5.bungee.protocol.packet.BossBar packet = new net.md_5.bungee.protocol.packet.BossBar(
                uuid, 4);
        packet.setColor(color.ordinal());
        packet.setDivision(style.ordinal());
        sendToAffected(packet);
    }

    private net.md_5.bungee.protocol.packet.BossBar updateFlags() {
        net.md_5.bungee.protocol.packet.BossBar packet = new net.md_5.bungee.protocol.packet.BossBar(
                uuid, 5);
        packet.setFlags(serializeFlags());
        return packet;
    }

    private net.md_5.bungee.protocol.packet.BossBar addPacket() {
        net.md_5.bungee.protocol.packet.BossBar packet = new net.md_5.bungee.protocol.packet.BossBar(
                uuid, 0);
        packet.setTitle(ComponentSerializer.toString(title));
        packet.setColor(color.ordinal());
        packet.setDivision(style.ordinal());
        packet.setHealth(health);
        packet.setFlags(serializeFlags());
        return packet;
    }

    private void sendToAffected(DefinedPacket packet) {
        for (ProxiedPlayer player : players) {
            if (player.isConnected() && visible) {
                sendPacket(player, packet);
            }
        }
    }

    private void sendPacket(
            ProxiedPlayer player,
            DefinedPacket packet
    ) {
        if (player.getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_9) {
            player.unsafe().sendPacket(packet);
        }
    }

    private net.md_5.bungee.protocol.packet.BossBar removePacket() {
        return new net.md_5.bungee.protocol.packet.BossBar(uuid, 1);
    }

    //
}
