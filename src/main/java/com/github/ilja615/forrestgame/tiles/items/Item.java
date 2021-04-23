package com.github.ilja615.forrestgame.tiles.items;

import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Pair;

import java.util.ArrayList;

/**
 * What is Item? It is basically similar to Tile, except Tile is for the first layer and Item is for the second layer.
 * So it is stuff that sits on top of the ground, including for example Trees and Bushes.
 * But now it's in a whole separate layer.
 */
public interface Item
{
    Texture getCurrentTexture();

    /**
     * Used for movement checking.
     *
     * @return if this tile is an obstacle
     */
    boolean isObstacle();

    /**
     * Fires when the player attempts to walk on this item.
     *
     * @param player     the player that attempted to walk
     * @param coordinate the coordinate of the tile
     * @return if the player can walk on the tile
     */
    boolean onPlayerAttemptingWalk(final Entity player, final Coordinate coordinate);

    ArrayList<Pair<Coordinate, Texture>> whichLayer();
}
