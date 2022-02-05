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
import com.github.ilja615.forrestgame.gui.renderer.TextureRenderer;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;
import com.github.ilja615.forrestgame.tiles.FloorTile;
import com.github.ilja615.forrestgame.tiles.Tile;
import com.github.ilja615.forrestgame.utility.Coordinate;

import java.util.Map;

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
    public boolean isObstacle()
    {
        return false;
    }

    @Override
    public boolean onPlayerAttemptingWalk(final Entity player, final Coordinate coordinate)
    {
        final Coordinate playerOldCoordinate = player.getCoordinate();
        final Coordinate coordinateThatCratePushedTo = coordinate.add(coordinate.x() - playerOldCoordinate.x(), coordinate.y() - playerOldCoordinate.y());
        final Tile destination = player.getWorld().getTiles()[coordinateThatCratePushedTo.x() + (coordinateThatCratePushedTo.y() * player.getWorld().WORLD_WIDTH)];
        if (!destination.hasItem() && destination.isNotObstacle() && player.getWorld().getEntityAt(coordinateThatCratePushedTo) == null)
        {
            player.getWorld().getTiles()[coordinateThatCratePushedTo.x() + (coordinateThatCratePushedTo.y() * player.getWorld().WORLD_WIDTH)].setItem(this);
            player.getWorld().getTiles()[coordinate.x() + (coordinate.y() * player.getWorld().WORLD_WIDTH)] = new FloorTile(Textures.GRASS_0);
            return true;
        }
        return false;
    }

    @Override
    public Map<Coordinate, Texture> whichLayer(final TextureRenderer tr)
    {
        return tr.LAYER_FRONT;
    }
}
