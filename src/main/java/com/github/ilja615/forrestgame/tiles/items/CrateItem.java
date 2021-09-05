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
import com.github.ilja615.forrestgame.entity.Player;
import com.github.ilja615.forrestgame.entity.StatTracker.Stat;
import com.github.ilja615.forrestgame.gui.particle.Particle;
import com.github.ilja615.forrestgame.gui.renderer.TextureRenderer;
import com.github.ilja615.forrestgame.gui.texture.PngTexture;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;
import com.github.ilja615.forrestgame.tiles.FloorTile;
import com.github.ilja615.forrestgame.tiles.Tile;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Pair;
import com.github.ilja615.forrestgame.world.World;

import java.util.ArrayList;

public class CrateItem implements Item
{
    private Texture texture;

    public CrateItem(final Texture t) { this.texture = t; }

    @Override
    public Texture getCurrentTexture()
    {
        return this.texture;
    }

    @Override
    public boolean isObstacle()
    {
        return false;
    }

    @Override
    public boolean onPlayerAttemptingWalk(final Entity player, final Coordinate coordinate)
    {
        Coordinate playerOldCoordinate = player.getCoordinate();
        Coordinate coordinateThatCratePushedTo = coordinate.relative(coordinate.getX() - playerOldCoordinate.getX(),  coordinate.getY() - playerOldCoordinate.getY());
        Tile destination = player.getWorld().getTiles()[coordinateThatCratePushedTo.getX() + (coordinateThatCratePushedTo.getY() * World.WORLD_WIDTH)];
        if (!destination.hasItem() && !destination.isObstacle())
        {
            player.getWorld().getTiles()[coordinateThatCratePushedTo.getX() + (coordinateThatCratePushedTo.getY() * World.WORLD_WIDTH)].setItem(this);
            player.getWorld().getTiles()[coordinate.getX() + (coordinate.getY() * World.WORLD_WIDTH)] = new FloorTile(Textures.GRASS_0);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<Pair<Coordinate, Texture>> whichLayer()
    {
        return TextureRenderer.LAYER_FRONT;
    }
}
