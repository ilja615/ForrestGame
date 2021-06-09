/*
 * Copyright (c) 2021 ilja615.
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

package com.github.ilja615.forrestgame.tiles.items;

import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Pair;
import java.util.ArrayList;

/**
 * What is Item? It is basically similar to Tile, except Tile is for the first layer and Item is for the second layer.
 * So it is stuff that sits on top of the ground, including for example Trees and Bushes.
 * But now it's in a whole separate layer.
 */
public interface Item
{
    Texture getCurrentTexture();

    /**
     * Used for movement checking.
     *
     * @return if this tile is an obstacle
     */
    boolean isObstacle();

    /**
     * Fires when the player attempts to walk on this item.
     *
     * @param player     the player that attempted to walk
     * @param coordinate the coordinate of the tile
     * @return if the player can walk on the tile
     */
    boolean onPlayerAttemptingWalk(final Entity player, final Coordinate coordinate);

    ArrayList<Pair<Coordinate, Texture>> whichLayer();
}
