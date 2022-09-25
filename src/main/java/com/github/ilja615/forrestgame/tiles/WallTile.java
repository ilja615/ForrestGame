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
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;
import com.github.ilja615.forrestgame.tiles.items.Item;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Direction;
import com.github.ilja615.forrestgame.world.World;

import java.util.Arrays;
import java.util.EnumMap;

public class WallTile extends Tile implements ConnectedTextureTile
{
    public final EnumMap<Direction.Diagonal, Texture> QUADRANT_TEXTURES = new EnumMap<>(Direction.Diagonal.class);
    private boolean isSingular = false;

    public WallTile(final Texture texture)
    {
        super(texture);
    }

    @Override
    public void postGenerationEvent(final World world, final Coordinate thisPos)
    {
        super.postGenerationEvent(world, thisPos);
        final Tile upTile = world.isWithinWorld(thisPos.up()) ? world.getTileAt(thisPos.up()) : world.airTile;
        final Tile downTile = world.isWithinWorld(thisPos.down()) ? world.getTileAt(thisPos.down()) : world.airTile;
        final Tile leftTile = world.isWithinWorld(thisPos.left()) ? world.getTileAt(thisPos.left()) : world.airTile;
        final Tile rightTile = world.isWithinWorld(thisPos.right()) ? world.getTileAt(thisPos.right()) : world.airTile;
        if (!(upTile instanceof WallTile) && !(downTile instanceof WallTile) && !(leftTile instanceof WallTile) && !(rightTile instanceof WallTile))
        {
            isSingular = true;
        }
    }

    @Override
    public Texture getTexture()
    {
        return this.texture;
    }

    @Override
    public EnumMap<Direction.Diagonal, Texture> getQuadrantTextures()
    {
        return QUADRANT_TEXTURES;
    }

    @Override
    public boolean isObstacle(final Entity incomingEntity)
    {
        return true;
    }

    @Override
    public boolean shouldShowConnectedTextures()
    {
        return !this.isSingular;
    }

    @Override
    public void adaptQuadrantTexturesList(final World world, final Coordinate thisPos)
    {
        Arrays.stream(Direction.Diagonal.values())
                .iterator()
                .forEachRemaining(diagonal -> QUADRANT_TEXTURES.put(diagonal, getGoodTexture(diagonal, thisPos, world)));
    }

    private Texture getGoodTexture(final Direction.Diagonal diagonal, final Coordinate thisPos, final World world)
    {
        final Coordinate firstPos = thisPos.transpose(diagonal.getHorizontalDirection(), 1);
        final Coordinate otherPos = thisPos.transpose(diagonal.getVerticalDirection(), 1);

        final Tile firstNeighbourTile = world.isWithinWorld(firstPos) ? world.getTileAt(firstPos) : world.airTile;
        final Tile otherNeighbourTile = world.isWithinWorld(otherPos) ? world.getTileAt(otherPos) : world.airTile;

        if (firstNeighbourTile instanceof AirTile && !(otherNeighbourTile instanceof AirTile || otherNeighbourTile instanceof WallTile))
        {
            return diagonal.getVerticalDirection() == Direction.UP ? Textures.WALL_STRAIGHT_PIECE : Textures.WALL_STRAIGHT_PIECE_MIRRORED;
        }

        if (!(firstNeighbourTile instanceof AirTile || firstNeighbourTile instanceof WallTile) && otherNeighbourTile instanceof AirTile)
        {
            return diagonal.getHorizontalDirection() == Direction.RIGHT ? Textures.WALL_STRAIGHT_VERTICAL_PIECE : Textures.WALL_STRAIGHT_VERTICAL_PIECE_MIRRORED;
        }

        if (firstNeighbourTile instanceof AirTile || otherNeighbourTile instanceof AirTile)
        {
            return Textures.AIR_PIECE;
        }

        if (!(firstNeighbourTile instanceof AirTile || firstNeighbourTile instanceof WallTile) && !(otherNeighbourTile instanceof AirTile || otherNeighbourTile instanceof WallTile))
        {
            if (diagonal.getHorizontalDirection() == Direction.RIGHT)
            {
                if (diagonal.getVerticalDirection() == Direction.UP)
                {
                    return Textures.WALL_OUTER_CORNER_PIECE_VM;
                } else
                {
                    return Textures.WALL_OUTER_CORNER_PIECE;
                }
            } else
            {
                if (diagonal.getVerticalDirection() == Direction.UP)
                {
                    return Textures.WALL_OUTER_CORNER_PIECE_HVM;
                } else
                {
                    return Textures.WALL_OUTER_CORNER_PIECE_HM;
                }
            }
        }

        if (!(firstNeighbourTile instanceof AirTile || firstNeighbourTile instanceof WallTile) && otherNeighbourTile instanceof WallTile)
        {
            return diagonal.getHorizontalDirection() == Direction.RIGHT ? Textures.WALL_STRAIGHT_VERTICAL_PIECE : Textures.WALL_STRAIGHT_VERTICAL_PIECE_MIRRORED;
        }

        if (firstNeighbourTile instanceof WallTile && !(otherNeighbourTile instanceof AirTile || otherNeighbourTile instanceof WallTile))
        {
            return diagonal.getVerticalDirection() == Direction.UP ? Textures.WALL_STRAIGHT_PIECE : Textures.WALL_STRAIGHT_PIECE_MIRRORED;
        }

        if (firstNeighbourTile instanceof WallTile && otherNeighbourTile instanceof WallTile)
        {
            final Coordinate thirdPos = thisPos.transpose(diagonal);
            final Tile thirdNeighbourTile = world.isWithinWorld(thirdPos) ? world.getTileAt(thirdPos) : world.airTile;

            if (thirdNeighbourTile instanceof AirTile || thirdNeighbourTile instanceof WallTile) return Textures.AIR_PIECE;

            if (diagonal.getHorizontalDirection() == Direction.RIGHT)
            {
                if (diagonal.getVerticalDirection() == Direction.UP)
                {
                    return Textures.WALL_INNER_CORNER_PIECE_VM;
                } else
                {
                    return Textures.WALL_INNER_CORNER_PIECE;
                }
            } else
            {
                if (diagonal.getVerticalDirection() == Direction.UP)
                {
                    return Textures.WALL_INNER_CORNER_PIECE_HVM;
                } else
                {
                    return Textures.WALL_INNER_CORNER_PIECE_HM;
                }
            }
        }

        return Textures.AIR_PIECE;
    }

    @Override
    public void setItem(final Item item)
    {
        // Can not set the item
    }
}
