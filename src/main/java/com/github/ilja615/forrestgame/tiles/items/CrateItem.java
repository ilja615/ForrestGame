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

package com.github.ilja615.forrestgame.tiles.items;

import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.entity.Player;
import com.github.ilja615.forrestgame.gui.renderer.TextureRenderer;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;
import com.github.ilja615.forrestgame.tiles.FloorTile;
import com.github.ilja615.forrestgame.tiles.Tile;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Pair;

import java.util.ArrayList;

public class CrateItem implements Item
{
    private final Texture texture;

    public CrateItem(final Texture t)
    {
        this.texture = t;
    }

    @Override
    public Texture getCurrentTexture()
    {
        return this.texture;
    }

    @Override
    public boolean isObstacle(Entity incomingEntity)
    {
        return !(incomingEntity instanceof Player);
    }

    @Override
    public boolean onPlayerAttemptingWalk(final Entity player, final Coordinate coordinate)
    {
        Coordinate playerOldCoordinate = player.getCoordinate();
        Coordinate coordinateThatCratePushedTo = coordinate.relative(coordinate.getX() - playerOldCoordinate.getX(), coordinate.getY() - playerOldCoordinate.getY());
        Tile destination = player.getWorld().getTiles()[coordinateThatCratePushedTo.getX() + (coordinateThatCratePushedTo.getY() * player.getWorld().WORLD_WIDTH)];
        if (!destination.hasItem() && !destination.isObstacle(player) && player.getWorld().getEntityAt(coordinateThatCratePushedTo) == null)
        {
            player.getWorld().getTiles()[coordinateThatCratePushedTo.getX() + (coordinateThatCratePushedTo.getY() * player.getWorld().WORLD_WIDTH)].setItem(this);
            player.getWorld().getTiles()[coordinate.getX() + (coordinate.getY() * player.getWorld().WORLD_WIDTH)] = new FloorTile(Textures.GRASS_0);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<Pair<Coordinate, Texture>> whichLayer(TextureRenderer tr)
    {
        return tr.LAYER_FRONT;
    }
}
