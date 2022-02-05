/*
 * Copyright (c) 2021-2022 the ForrestGame contributors.
 *
 * This file is part of ForrestGame.
 *
 * ForrestGame is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ForrestGame is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ForrestGame.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.ilja615.forrestgame.entity;

import com.github.ilja615.forrestgame.gui.renderer.TextureRenderer;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.utility.Coordinate;
import com.github.ilja615.forrestgame.utility.Tickable;
import com.github.ilja615.forrestgame.world.World;

import java.util.Map;

public interface Entity extends Tickable
{
    World getWorld();

    /**
     * Gets the current coordinate of this entity.
     *
     * @return the current coordinate
     */
    Coordinate getCoordinate();

    /**
     * Sets the coordinate of this entity.
     *
     * @param coordinate the coordinate this entity will be in
     */
    void setCoordinate(final Coordinate coordinate);

    StatTracker getStatTracker();

    /**
     * Sets if this entity can move
     *
     * @param mobile if the entity can move
     */
    void setMobile(final boolean mobile);

    Texture getCurrentTexture();

    // Entities use middle layer by default
    default Map<Coordinate, Texture> whichLayer(TextureRenderer tr)
    {
        return tr.LAYER_MIDDLE;
    }

    /**
     * Fires when the player attempts to walk into this entity.
     *
     * @param player     the player that attempted to walk
     * @param coordinate the coordinate of the entity
     * @return if the player can walk into the space of the entity
     */
    boolean onPlayerAttemptingWalk(final Entity player, final Coordinate coordinate);

    void die(StatTracker.Stat deathCausingStat);

    void automaticallyMove();
}