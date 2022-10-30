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
    public boolean isObstacle(final Entity incomingEntity)
    {
        return !(incomingEntity instanceof Player);
    }

    @Override
    public boolean onPlayerAttemptingWalk(final Entity player, final Coordinate coordinate)
    {
        final Coordinate playerOldCoordinate = player.getCoordinate();
        final Coordinate coordinateThatCratePushedTo = coordinate.relativeMove(coordinate.x() - playerOldCoordinate.x(), coordinate.y() - playerOldCoordinate.y());
        final Tile destination = player.getWorld().getTiles()[coordinateThatCratePushedTo.x() + (coordinateThatCratePushedTo.y() * player.getWorld().WORLD_WIDTH)];

        if (!destination.hasItem() && !destination.isObstacle(player) && player.getWorld().getEntityAt(coordinateThatCratePushedTo) == null)
        {
            player.getWorld().getTiles()[coordinateThatCratePushedTo.x() + (coordinateThatCratePushedTo.y() * player.getWorld().WORLD_WIDTH)].setItem(this);
            player.getWorld().getTiles()[coordinate.x() + (coordinate.y() * player.getWorld().WORLD_WIDTH)] = new FloorTile(Textures.GRASS_0);
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public Map<Pair<Coordinate, Pair<Float, Float>>, Texture> whichLayer(final TextureRenderer tr)
    {
        return tr.LAYER_FRONT;
    }
}
