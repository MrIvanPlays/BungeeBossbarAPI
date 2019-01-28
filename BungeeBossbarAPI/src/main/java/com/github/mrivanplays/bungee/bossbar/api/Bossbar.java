/*
 * *
 *  * Copyright 2018-2019 MrIvanPlays
 *  * <p>
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 *  * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *  * <p>
 *  * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *  * <p>
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *  * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.github.mrivanplays.bungee.bossbar.api;

import com.github.mrivanplays.bungee.bossbar.api.style.BarStyle;
import com.github.mrivanplays.bungee.bossbar.api.style.BarColor;
import com.github.mrivanplays.bungee.bossbar.api.BarTitle;
import com.github.mrivanplays.bungee.bossbar.util.*;
import com.github.mrivanplays.bungee.bossbar.exception.ProgressException;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Represents a new bossbar different than the {@link net.md_5.bungee.protocol.packet.BossBar}
 * provided one
 */
public interface Bossbar {
  /** Adds all online {@link ProxiedPlayer} to the bossbar */
  @FromVersion("1.3.0")
  void addOnlinePlayers();

  /**
   * Adds all online {@link ProxiedPlayer} to the bossbar after some time specified in the unit
   *
   * @param plugin plugin assigned to
   * @param delay delay in unit
   * @param unit delay's unit
   */
  @FromVersion("1.4.0")
  void addOnlinePlayersAfter(Plugin plugin, int delay, TimeUnit unit);

  /**
   * Adds a {@link ProxiedPlayer} to the bossbar
   *
   * @param player player added
   */
  @FromVersion("1.0.0")
  void addPlayer(ProxiedPlayer player);

  /**
   * Adds a {@link ProxiedPlayer} to the bossbar after some amount of time Useful if you use the
   * bossbar on join and send to the players a title
   *
   * @param player added player
   * @param plugin plugin assigned to
   * @param delay delay in the unit specified
   * @param unit delay's unit
   */
  @FromVersion("1.3.2")
  void addPlayerAfter(ProxiedPlayer player, Plugin plugin, int delay, TimeUnit unit);

  /**
   * Removes a {@link ProxiedPlayer} from the bossbar
   *
   * @param player removed player
   */
  @FromVersion("1.0.0")
  void removePlayer(ProxiedPlayer player);

  /**
   * Removes the {@link ProxiedPlayer} from the bossbar after some amount of time
   *
   * @param player removed player
   * @param plugin plugin assigned to
   * @param delay delay in the unit specified
   * @param unit delay's unit
   */
  @FromVersion("1.3.2")
  void removePlayerAfter(ProxiedPlayer player, Plugin plugin, int delay, TimeUnit unit);

  /**
   * Removes all players from the bossbar after some amount of time
   *
   * @param plugin plugin assigned to
   * @param delay delay in the unit specified
   * @param unit delay's unit
   */
  @FromVersion("1.4.0")
  void removeAllPlayersAfter(Plugin plugin, int delay, TimeUnit unit);

  /**
   * Gets all {@link ProxiedPlayer}s added into the bossbar
   *
   * @return all players in the bossbar
   */
  @FromVersion("1.0.0 (remove: 1.1.0; readd: 1.3.0)")
  List<ProxiedPlayer> getPlayers();

  /** Removes all players from the bossbar and clears {@link #getPlayers()} Removes the bossbar */
  @FromVersion("1.0.0")
  void removeAllPlayers();

  /**
   * Gets the {@link BarColor} of the bossbar
   *
   * @return color of the bossbar
   */
  @FromVersion("1.0.0")
  BarColor getColor();

  /**
   * Gets the {@link BarStyle} of the bossbar
   *
   * @return style of the bossbar
   */
  @FromVersion("1.0.0")
  BarStyle getStyle();

  /**
   * Sets a new {@link BarColor} of the bossbar
   *
   * @param color set color
   */
  @FromVersion("1.0.0")
  void setColor(BarColor color);

  /**
   * Sets a new {@link BarStyle} of the bossbar
   *
   * @param style set style
   */
  @FromVersion("1.0.0")
  void setStyle(BarStyle style);

  /**
   * Gets the bossbar title as {@link BarTitle}
   *
   * @return bossbar title
   */
  @FromVersion("1.3.0")
  BarTitle getTitle();

  /**
   * Gets the bossbar title as {@link String}. But I still suggest looking into
   *
   * @return bossbar title as string
   * @see #getTitle()
   */
  @FromVersion("1.0.0 (renamed from: getTitle(); remove 1.3.0; readd 1.3.1)")
  default String getTitleString() {
    return getTitle().asString();
  }

  /**
   * Sets the {@link BarTitle} as a string. But I still suggest looking into
   *
   * @param newTitle set title by string
   * @see #setTitle(BarTitle)
   */
  @FromVersion("1.0.0")
  default void setTitle(String newTitle) {
    setTitle(new BarTitle(newTitle));
  }

  /**
   * Sets a new bossbar {@link BarTitle}
   *
   * @param title new title
   */
  @FromVersion("1.3.0")
  void setTitle(BarTitle title);

  /**
   * Checks if the bossbar is visible or no
   *
   * @return true/false
   */
  @FromVersion("1.3.0")
  boolean isVisible();

  /**
   * Sets the bossbar visibility
   *
   * @param visible new visibility
   */
  @FromVersion("1.3.0")
  void setVisible(boolean visible);

  /**
   * Gets the progress of the bossbar
   *
   * @return bossbar progress
   */
  @FromVersion("1.0.0 (renamed from: getHealth() in 1.3.2)")
  float getProgress();

  /**
   * Sets a new bossbar progress
   *
   * @param newProgress new progress
   * @throws ProgressException if the progress is more than 1
   */
  @FromVersion("1.0.0 (renamed from: setHealth() in 1.3.2)")
  void setProgress(float newProgress);

  String toString();
}