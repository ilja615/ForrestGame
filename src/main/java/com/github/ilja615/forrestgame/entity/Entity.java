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

package com.github.ilja615.forrestgame.entity;

import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Tickable;
import com.github.ilja615.forrestgame.world.World;

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
}