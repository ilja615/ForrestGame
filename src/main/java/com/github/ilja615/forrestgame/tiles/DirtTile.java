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
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Direction;
import com.github.ilja615.forrestgame.world.World;

import java.util.Arrays;
import java.util.EnumMap;

public class DirtTile extends FloorTile implements ConnectedTextureTile
{
    public EnumMap<Direction.Secondary, Texture> QUADRANT_TEXTURES = new EnumMap<>(Direction.Secondary.class);

    public DirtTile(final Texture texture)
    {
        super(texture);
    }

    public void adaptQuadrantTexturesList(World world, Coordinate thisPos)
    {
        Arrays.stream(Direction.Secondary.values()).iterator().forEachRemaining(secondary ->
        {
            QUADRANT_TEXTURES.put(secondary, getGoodTexture(secondary, thisPos, world));
        });
    }

    @Override
    public EnumMap<Direction.Secondary, Texture> getQuadrantTextures()
    {
        return QUADRANT_TEXTURES;
    }

    private Texture getGoodTexture(Direction.Secondary secondary, Coordinate thisPos, World world)
    {
        Coordinate firstPos = thisPos.move(secondary.getHorizontal(), 1);
        Coordinate otherPos = thisPos.move(secondary.getVertical(), 1);

        Tile firstNeighbourTile = world.isWithinWorld(firstPos) ? world.getTileAt(firstPos) : world.airTile;
        Tile otherNeighbourTile = world.isWithinWorld(otherPos) ? world.getTileAt(otherPos) : world.airTile;

        if (firstNeighbourTile instanceof AirTile && otherNeighbourTile instanceof AirTile) return Textures.AIR_PIECE;
        if (firstNeighbourTile instanceof AirTile)
        {
            return secondary.getVertical() == Direction.UP
                    ? Textures.DIRT_STRAIGHT_PIECE
                    : Textures.DIRT_STRAIGHT_PIECE_MIRRORED;
        }
        if (otherNeighbourTile instanceof AirTile)
        {
            return secondary.getHorizontal() == Direction.RIGHT
                    ? Textures.DIRT_STRAIGHT_VERTICAL_PIECE
                    : Textures.DIRT_STRAIGHT_VERTICAL_PIECE_MIRRORED;
        }

        if (!(firstNeighbourTile instanceof DirtTile) && !(otherNeighbourTile instanceof DirtTile))
        {
            if (secondary.getHorizontal() == Direction.RIGHT)
            {
                if (secondary.getVertical() == Direction.UP)
                {
                    return Textures.DIRT_OUTER_CORNER_PIECE_VM;
                } else
                {
                    return Textures.DIRT_OUTER_CORNER_PIECE;
                }
            } else
            {
                if (secondary.getVertical() == Direction.UP)
                {
                    return Textures.DIRT_OUTER_CORNER_PIECE_HVM;
                } else
                {
                    return Textures.DIRT_OUTER_CORNER_PIECE_HM;
                }
            }
        }

        if (!(firstNeighbourTile instanceof DirtTile))
        {
            return secondary.getHorizontal() == Direction.RIGHT
                    ? Textures.DIRT_STRAIGHT_VERTICAL_PIECE
                    : Textures.DIRT_STRAIGHT_VERTICAL_PIECE_MIRRORED;
        }

        if (!(otherNeighbourTile instanceof DirtTile))
        {
            return secondary.getVertical() == Direction.UP
                    ? Textures.DIRT_STRAIGHT_PIECE
                    : Textures.DIRT_STRAIGHT_PIECE_MIRRORED;
        }

        Coordinate thirdPos = thisPos.move(secondary, 1);
        Tile thirdNeighbourTile = world.isWithinWorld(thirdPos) ? world.getTileAt(thirdPos) : world.airTile;

        if (thirdNeighbourTile instanceof DirtTile) return Textures.DIRT_FULL_PIECE;

        if (secondary.getHorizontal() == Direction.RIGHT)
        {
            if (secondary.getVertical() == Direction.UP)
            {
                return Textures.DIRT_INNER_CORNER_PIECE_VM;
            } else
            {
                return Textures.DIRT_INNER_CORNER_PIECE;
            }
        } else
        {
            if (secondary.getVertical() == Direction.UP)
            {
                return Textures.DIRT_INNER_CORNER_PIECE_HVM;
            } else
            {
                return Textures.DIRT_INNER_CORNER_PIECE_HM;
            }
        }
    }
}
