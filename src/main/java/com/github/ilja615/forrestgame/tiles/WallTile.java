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

import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;
import com.github.ilja615.forrestgame.utility.Coordinate;
import com.github.ilja615.forrestgame.utility.Direction;
import com.github.ilja615.forrestgame.world.World;

import java.util.Arrays;
import java.util.EnumMap;

public class WallTile extends Tile
{
    public EnumMap<Direction.Secondary, Texture> QUADRANT_TEXTURES = new EnumMap<>(Direction.Secondary.class);

    public WallTile(final Texture texture)
    {
        super(texture);
    }

    @Override
    public boolean isObstacle()
    {
        return false;
    }

    public void adaptQuadrantTexturesList(final World world, final Coordinate thisPos)
    {
        Arrays.stream(Direction.Secondary.values()).iterator().forEachRemaining(secondary ->
        {
            QUADRANT_TEXTURES.put(secondary, getGoodTexture(secondary, thisPos, world));
        });
        System.out.println(thisPos + " | " + QUADRANT_TEXTURES);
    }

    private Texture getGoodTexture(final Direction.Secondary secondary, final Coordinate thisPos, final World world)
    {
        final Coordinate firstPos = thisPos.move(secondary.getHorizontal(), 1);
        final Coordinate otherPos = thisPos.move(secondary.getVertical(), 1);

        final Tile firstNeighbourTile = world.isWithinWorld(firstPos) ? world.getTileAt(firstPos) : world.airTile;
        final Tile otherNeighbourTile = world.isWithinWorld(otherPos) ? world.getTileAt(otherPos) : world.airTile;

        if (firstNeighbourTile instanceof AirTile || otherNeighbourTile instanceof AirTile)
            return Textures.WALL_AIR_PIECE;

        if (firstNeighbourTile instanceof FloorTile && otherNeighbourTile instanceof FloorTile)
        {
            if (secondary.getHorizontal() == Direction.RIGHT)
            {
                if (secondary.getVertical() == Direction.UP)
                {
                    return Textures.WALL_OUTER_CORNER_PIECE_VM;
                } else
                {
                    return Textures.WALL_OUTER_CORNER_PIECE;
                }
            } else
            {
                if (secondary.getVertical() == Direction.UP)
                {
                    return Textures.WALL_OUTER_CORNER_PIECE_HVM;
                } else
                {
                    return Textures.WALL_OUTER_CORNER_PIECE_HM;
                }
            }
        }

        if (firstNeighbourTile instanceof FloorTile && otherNeighbourTile instanceof WallTile)
            return secondary.getHorizontal() == Direction.RIGHT ? Textures.WALL_STRAIGHT_VERTICAL_PIECE : Textures.WALL_STRAIGHT_VERTICAL_PIECE_MIRRORED;

        if (firstNeighbourTile instanceof WallTile && otherNeighbourTile instanceof FloorTile)
            return secondary.getVertical() == Direction.UP ? Textures.WALL_STRAIGHT_PIECE : Textures.WALL_STRAIGHT_PIECE_MIRRORED;

        if (firstNeighbourTile instanceof WallTile && otherNeighbourTile instanceof WallTile)
        {
            final Coordinate thirdPos = thisPos.move(secondary, 1);
            final Tile thirdNeighbourTile = world.isWithinWorld(thirdPos) ? world.getTileAt(thirdPos) : world.airTile;

            if (!(thirdNeighbourTile instanceof FloorTile))
                return Textures.WALL_AIR_PIECE;

            if (secondary.getHorizontal() == Direction.RIGHT)
            {
                if (secondary.getVertical() == Direction.UP)
                {
                    return Textures.WALL_INNER_CORNER_PIECE_VM;
                } else
                {
                    return Textures.WALL_INNER_CORNER_PIECE;
                }
            } else
            {
                if (secondary.getVertical() == Direction.UP)
                {
                    return Textures.WALL_INNER_CORNER_PIECE_HVM;
                } else
                {
                    return Textures.WALL_INNER_CORNER_PIECE_HM;
                }
            }
        }

        return Textures.WALL_AIR_PIECE;
    }
}
