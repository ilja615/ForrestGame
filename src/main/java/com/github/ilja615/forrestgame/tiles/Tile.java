/*
 * Copyright (c) 2021 xf8b, ilja615.
 *
 * This file is part of Forrest Game.
 *
 * Forrest Game is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Forrest Game is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Forrest Game.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.ilja615.forrestgame.tiles;

import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.util.Coordinate;

public interface Tile
{
    Texture getTexture();

    default boolean isNotFloor()
    {
        return true;
    }

    /**
     * Used for movement checking.
     *
     * @return if this tile is an obstacle
     */
    default boolean isObstacle()
    {
        return false;
    }

    /**
     * Fires when the player attempts to walk on this tile.
     *
     * @param player     the player that attempted to walk
     * @param coordinate the coordinate of the tile
     * @return if the player can walk on the tile
     */
    default boolean onPlayerAttemptingWalk(final Entity player, final Coordinate coordinate)
    {
        return true;
    }
}
