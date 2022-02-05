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

package com.github.ilja615.forrestgame.tiles;

import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.tiles.items.Item;
import com.github.ilja615.forrestgame.utility.Coordinate;

public class Tile
{
    private final Texture texture;
    private Item item = null;

    public Tile(final Texture texture)
    {
        this.texture = texture;
    }

    public Texture getTexture()
    {
        return texture;
    }

    /**
     * Used for movement checking.
     *
     * @return if this tile is an obstacle
     */
    public boolean isObstacle()
    {
        if (this.hasItem())
            return this.item.isObstacle();
        else return false;
    }

    /**
     * Fires when the player attempts to walk on this tile.
     *
     * @param player     the player that attempted to walk
     * @param coordinate the coordinate of the tile
     * @return if the player can walk on the tile
     */
    public boolean onPlayerAttemptingWalk(final Entity player, final Coordinate coordinate)
    {
        if (this.hasItem())
            return this.item.onPlayerAttemptingWalk(player, coordinate);
        else return true;
    }

    public boolean hasItem()
    {
        return item != null;
    }

    public Item getItem()
    {
        return item;
    }

    public void setItem(Item item)
    {
        this.item = item;
    }
}
