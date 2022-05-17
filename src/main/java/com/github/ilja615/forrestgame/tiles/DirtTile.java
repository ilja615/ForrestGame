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
    public final EnumMap<Direction.Diagonal, Texture> QUADRANT_TEXTURES = new EnumMap<>(Direction.Diagonal.class);

    public DirtTile(final Texture texture)
    {
        super(texture);
    }

    public void adaptQuadrantTexturesList(final World world, final Coordinate coordinate)
    {
        Arrays.stream(Direction.Diagonal.values())
                .iterator()
                .forEachRemaining(diagonal -> QUADRANT_TEXTURES.put(diagonal, getGoodTexture(diagonal, coordinate, world)));
    }

    @Override
    public EnumMap<Direction.Diagonal, Texture> getQuadrantTextures()
    {
        return QUADRANT_TEXTURES;
    }

    private Texture getGoodTexture(final Direction.Diagonal diagonalDirection, final Coordinate coordinate, final World world)
    {
        final Coordinate firstCoordinate = coordinate.move(diagonalDirection.getHorizontalDirection());
        final Coordinate otherCoordinate = coordinate.move(diagonalDirection.getVerticalDirection());

        final Tile firstNeighbourTile = world.isWithinWorld(firstCoordinate)
                ? world.getTileAt(firstCoordinate)
                : world.airTile;
        final Tile otherNeighbourTile = world.isWithinWorld(otherCoordinate)
                ? world.getTileAt(otherCoordinate)
                : world.airTile;

        if (firstNeighbourTile instanceof AirTile && otherNeighbourTile instanceof AirTile)
        {
            return Textures.AIR_PIECE;
        } else if (firstNeighbourTile instanceof AirTile)
        {
            return diagonalDirection.getVerticalDirection() == Direction.UP
                    ? Textures.DIRT_STRAIGHT_PIECE
                    : Textures.DIRT_STRAIGHT_PIECE_MIRRORED;
        } else if (otherNeighbourTile instanceof AirTile)
        {
            return diagonalDirection.getHorizontalDirection() == Direction.RIGHT
                    ? Textures.DIRT_STRAIGHT_VERTICAL_PIECE
                    : Textures.DIRT_STRAIGHT_VERTICAL_PIECE_MIRRORED;
        } else if (!(firstNeighbourTile instanceof DirtTile) && !(otherNeighbourTile instanceof DirtTile))
        {
            return switch (diagonalDirection)
                    {
                        case UP_AND_LEFT -> Textures.DIRT_OUTER_CORNER_PIECE_HVM;
                        case UP_AND_RIGHT -> Textures.DIRT_OUTER_CORNER_PIECE_VM;
                        case DOWN_AND_LEFT -> Textures.DIRT_OUTER_CORNER_PIECE_HM;
                        case DOWN_AND_RIGHT -> Textures.DIRT_OUTER_CORNER_PIECE;
                    };
        } else if (!(firstNeighbourTile instanceof DirtTile))
        {
            return diagonalDirection.getHorizontalDirection() == Direction.RIGHT
                    ? Textures.DIRT_STRAIGHT_VERTICAL_PIECE
                    : Textures.DIRT_STRAIGHT_VERTICAL_PIECE_MIRRORED;
        } else if (!(otherNeighbourTile instanceof DirtTile))
        {
            return diagonalDirection.getVerticalDirection() == Direction.UP
                    ? Textures.DIRT_STRAIGHT_PIECE
                    : Textures.DIRT_STRAIGHT_PIECE_MIRRORED;
        } else
        {
            final Coordinate thirdPos = coordinate.move(diagonalDirection);
            final Tile thirdNeighbourTile = world.isWithinWorld(thirdPos)
                    ? world.getTileAt(thirdPos)
                    : world.airTile;

            if (thirdNeighbourTile instanceof DirtTile)
            {
                return Textures.DIRT_FULL_PIECE;
            } else
            {
                return switch (diagonalDirection)
                        {
                            case UP_AND_LEFT -> Textures.DIRT_INNER_CORNER_PIECE_HVM;
                            case UP_AND_RIGHT -> Textures.DIRT_INNER_CORNER_PIECE_VM;
                            case DOWN_AND_LEFT -> Textures.DIRT_INNER_CORNER_PIECE_HM;
                            case DOWN_AND_RIGHT -> Textures.DIRT_INNER_CORNER_PIECE;
                        };
            }
        }
    }
}
