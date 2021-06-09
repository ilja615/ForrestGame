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

package com.github.ilja615.forrestgame.world;

import com.github.ilja615.forrestgame.Game;
import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.gui.particle.Particle;
import com.github.ilja615.forrestgame.gui.renderer.TextRenderer;
import com.github.ilja615.forrestgame.gui.renderer.TextureRenderer;
import com.github.ilja615.forrestgame.tiles.Tile;
import com.github.ilja615.forrestgame.tiles.items.BushItem;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Tickable;

import java.util.ArrayList;

public interface World extends Tickable
{
    int WORLD_WIDTH = 24;
    int WORLD_HEIGHT = 20;

    Game getGame();

    /**
     * Gets the tiles that this world has.
     *
     * @return the tiles of this world
     */
    Tile[] getTiles();

    Entity getPlayer();

    TextureRenderer getTextureRenderer();

    TextRenderer getTextRenderer();

    TimeTracker getTimeTracker();

    ArrayList<Particle> getParticles();

    /**
     * Generates the world and fills {@link World#getTiles}
     */
    void generate();

    /**
     * Fires when it is the enemy's turn.
     */
    void onEnemyTurn();

    /**
     * Checks if the coordinate is a valid position to walk in.
     *
     * @param coordinate the coordinate to check
     * @return if the coordinate is a valid position to walk in
     */
    default boolean isValidLocation(final Coordinate coordinate)
    {
        if (this.getTiles()[coordinate.getX() + (coordinate.getY() * WORLD_WIDTH)].isObstacle() && !(this.getTiles()[coordinate.getX() + (coordinate.getY() * WORLD_WIDTH)].getItem() instanceof BushItem)) return false;
        else return coordinate.getX() >= 0
                && coordinate.getY() >= 0
                && coordinate.getX() < WORLD_WIDTH
                && coordinate.getY() < WORLD_HEIGHT;
    }
}
