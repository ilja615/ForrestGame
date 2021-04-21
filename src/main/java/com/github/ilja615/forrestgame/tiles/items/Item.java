package com.github.ilja615.forrestgame.tiles.items;

import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.util.Coordinate;

/**
 * What is Item? It is basically similar to Tile, except Tile is for the first layer and Item is for the second layer.
 * So it is stuff that sits on top of the ground, including for example Trees and Bushes.
 * But now it's in a whole separate layer.
 */
public class Item
{
    private final Texture texture;

    public Item(final Texture texture)
    {
        this.texture = texture;
    }

    public Texture getTexture()
    {
        return texture;
    }

    /**
     * Used for movement checking.
     *
     * @return if this tile is an obstacle
     */
    public boolean isObstacle()
    {
        return false;
    }

    /**
     * Fires when the player attempts to walk on this item.
     *
     * @param player     the player that attempted to walk
     * @param coordinate the coordinate of the tile
     * @return if the player can walk on the tile
     */
    public boolean onPlayerAttemptingWalk(final Entity player, final Coordinate coordinate)
    {
        return true;
    }
}
