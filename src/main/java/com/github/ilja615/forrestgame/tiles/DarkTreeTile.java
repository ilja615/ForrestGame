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

import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.gui.renderer.TextureRenderer;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Direction;
import com.github.ilja615.forrestgame.util.Pair;
import com.github.ilja615.forrestgame.world.World;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.concurrent.ThreadLocalRandom;

public class DarkTreeTile extends Tile implements ConnectedTextureTile
{
    public final EnumMap<Direction.Diagonal, Texture> QUADRANT_TEXTURES = new EnumMap<>(Direction.Diagonal.class);

    @Override
    public boolean canHaveItem()
    {
        return false;
    }

    @Override
    public boolean isObstacle(final Entity incomingEntity)
    {
        return true;
    }

    public DarkTreeTile(final Texture texture)
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
        final Coordinate firstCoordinate = coordinate.transpose(diagonalDirection.getHorizontalDirection());
        final Coordinate otherCoordinate = coordinate.transpose(diagonalDirection.getVerticalDirection());

        final Tile firstNeighbourTile = world.isWithinWorld(firstCoordinate)
                ? world.getTileAt(firstCoordinate)
                : world.airTile;
        final Tile otherNeighbourTile = world.isWithinWorld(otherCoordinate)
                ? world.getTileAt(otherCoordinate)
                : world.airTile;

        if (!(isWall(firstNeighbourTile)) && !(isWall(otherNeighbourTile)))
        {
            return switch (diagonalDirection)
                    {
                        case UP_AND_LEFT -> Textures.DARK_TREE_TOP_OUTER_CORNER_LEFT_PIECE;
                        case UP_AND_RIGHT -> Textures.DARK_TREE_TOP_OUTER_CORNER_RIGHT_PIECE;
                        case DOWN_AND_LEFT -> Textures.DARK_TREE_BOTTOM_OUTER_CORNER_LEFT_PIECE;
                        case DOWN_AND_RIGHT -> Textures.DARK_TREE_BOTTOM_OUTER_CORNER_RIGHT_PIECE;
                    };
        } else if (!(isWall(firstNeighbourTile)))
        {
            final Coordinate belowFirstCoordinate = firstCoordinate.down();

            final Tile belowFirstNeighbourTile = world.isWithinWorld(belowFirstCoordinate)
                    ? world.getTileAt(belowFirstCoordinate)
                    : world.airTile;
            if (isWall(belowFirstNeighbourTile) && diagonalDirection.getVerticalDirection() == Direction.DOWN)
            {
                return diagonalDirection.getHorizontalDirection() == Direction.RIGHT
                        ? Textures.DARK_TREE_TOP_INNER_CORNER_RIGHT_PIECE
                        : Textures.DARK_TREE_TOP_INNER_CORNER_LEFT_PIECE;
            } else
            {
                return diagonalDirection.getHorizontalDirection() == Direction.RIGHT
                        ? Textures.DARK_TREE_STRAIGHT_VERTICAL_RIGHT_PIECE
                        : Textures.DARK_TREE_STRAIGHT_VERTICAL_LEFT_PIECE;
            }
        } else if (!(isWall(otherNeighbourTile)))
        {
            return diagonalDirection.getVerticalDirection() == Direction.UP
                    ? Textures.DARK_TREE_TOP_STRAIGHT_PIECE[ThreadLocalRandom.current().nextInt(Textures.DARK_TREE_TOP_STRAIGHT_PIECE.length)]
                    : (diagonalDirection.getHorizontalDirection() == Direction.RIGHT
                    ? Textures.DARK_TREE_BOTTOM_STRAIGHT_RIGHT_PIECE
                    : Textures.DARK_TREE_BOTTOM_STRAIGHT_LEFT_PIECE);
        } else
        {
            final Coordinate thirdPos = coordinate.transpose(diagonalDirection);
            final Tile thirdNeighbourTile = world.isWithinWorld(thirdPos)
                    ? world.getTileAt(thirdPos)
                    : world.airTile;

            if (isWall(thirdNeighbourTile))
            {
                return Textures.DARK_TREE_FULL_PIECE;
            } else {
                return switch (diagonalDirection)
                        {
                            case UP_AND_LEFT, UP_AND_RIGHT -> Textures.DARK_TREE_FULL_PIECE;
                            case DOWN_AND_LEFT -> Textures.DARK_TREE_BOTTOM_INNER_CORNER_LEFT_PIECE;
                            case DOWN_AND_RIGHT -> Textures.DARK_TREE_BOTTOM_INNER_CORNER_RIGHT_PIECE;
                        };
            }
        }
    }

    private boolean isWall(Tile tile)
    {
        return (tile instanceof DarkTreeTile || tile instanceof AirTile);
    }

    @Override
    public Multimap<Pair<Coordinate, Pair<Float, Float>>, Object> whichLayer(final TextureRenderer tr)
    {
        return tr.LAYER_FRONT;
    }
}
