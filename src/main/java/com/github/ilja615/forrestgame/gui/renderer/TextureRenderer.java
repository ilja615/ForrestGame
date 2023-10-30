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

package com.github.ilja615.forrestgame.gui.renderer;

import com.github.ilja615.forrestgame.entity.Player;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.tiles.ConnectedTextureTile;
import com.github.ilja615.forrestgame.tiles.Tile;
import com.github.ilja615.forrestgame.tiles.items.Item;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Direction;
import com.github.ilja615.forrestgame.util.Pair;
import com.github.ilja615.forrestgame.world.World;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class TextureRenderer
{
    public final Multimap<Pair<Coordinate, Pair<Float, Float>>, Object> LAYER_BACK = MultimapBuilder.treeKeys().arrayListValues().build(); // For things between the floor and the entities
    public final Multimap<Pair<Coordinate, Pair<Float, Float>>, Object> LAYER_FRONT = MultimapBuilder.treeKeys().arrayListValues().build(); // For foreground things
    private final World world;
    private float partialX = 0f;
    private float partialY = 0f;
    private static float TEXTURE_SIZE = 1/13f*2f;
    private static float HALF_TEXTURE_SIZE = TEXTURE_SIZE/2f;
    // Whether the texture reindeer should be enabled on not
    private boolean enabled = true;

    public TextureRenderer(final World world)
    {
        this.world = world;
    }

    public float getPartialX()
    {
        return partialX;
    }

    public void setPartialX(final float partialX)
    {
        this.partialX = partialX;
    }

    public float getPartialY()
    {
        return partialY;
    }

    public void setPartialY(final float partialY)
    {
        this.partialY = partialY;
    }

    // The stuff for enabled
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled()
    {
        this.enabled = true;
    }

    public void setDisabled()
    {
        this.enabled = false;
    }

    public void clearLayers()
    {
        LAYER_BACK.clear();
        LAYER_FRONT.clear();
    }

    public void renderBoard()
    {
        // Sort through all the tiles within view distance and put them in their layer.
        for (int y = world.getPlayer().getCoordinate().y() - 6; y <= world.getPlayer().getCoordinate().y() + 6; y++)
        {
            for (int x = world.getPlayer().getCoordinate().x() - 7; x <= world.getPlayer().getCoordinate().x() + 7; x++)
            {
                if (x >= 0 && x < world.WORLD_WIDTH && y >= 0 && y < world.WORLD_HEIGHT)
                {
                    final Tile tile = world.getTiles()[x + (y * world.WORLD_WIDTH)];
                    tile.whichLayer(this)
                            .put(new Pair<>(new Coordinate(x, y), new Pair<>(0f,0f)), tile);

                    if (tile.hasItem())
                    {
                        tile.getItem().whichLayer(this)
                                .put(new Pair<>(new Coordinate(x, y), new Pair<>(0f,0f)), tile.getItem());
                    }
                }
            }
        }

        // Add entities and player.
        world.getEntities().forEach(entity -> entity.whichLayer(this).put(new Pair<>(entity.getCoordinate(), new Pair<>(entity.partialX(), entity.partialY())), entity.getCurrentTexture()));
        LAYER_FRONT.put(new Pair<>(world.getPlayer().getCoordinate(), new Pair<>(0f, 0f)), world.getPlayer());

        // For every layer, render every texture.
        renderAtLayer(LAYER_BACK);
        renderAtLayer(LAYER_FRONT);

        world.getParticles().forEach(particle -> renderTexture(particle.getCurrentTexture(), new Pair<Coordinate, Pair<Float, Float>>(particle.getCoordinate(), new Pair(particle.partialX, particle.partialY))));
    }

    public void renderAtLayer(Multimap<Pair<Coordinate, Pair<Float, Float>>, Object> layer)
    {
        for (Map.Entry<Pair<Coordinate, Pair<Float, Float>>, Object> entry : layer.entries())
        {
            if (entry.getValue() instanceof Texture texture)
                renderTexture(texture, entry.getKey());
            if (entry.getValue() instanceof Tile tile)
            {
                if (tile instanceof ConnectedTextureTile)
                    renderConnectedTextureTile(entry.getKey().first().x(), entry.getKey().first().y());
                else
                    renderTexture(tile.getTexture(), entry.getKey());
            }
            if (entry.getValue() instanceof Item item)
                renderTexture(item.getCurrentTexture(), entry.getKey());
            if (entry.getValue() instanceof Player player)
                renderTexture(player.getCurrentTexture(), entry.getKey(), true);
        }
    }

    public void renderTexture(final Texture texture, Pair<Coordinate, Pair<Float, Float>> coordinateWithPartials)
    {
        renderTexture(texture, coordinateWithPartials, false);
    }

    public void renderTexture(final Texture texture, Pair<Coordinate, Pair<Float, Float>> coordinateWithPartials, boolean isPlayer)
    {
        texture.bind();

        float offsetX = (coordinateWithPartials.first().x() - world.getPlayer().getCoordinate().x() + coordinateWithPartials.second().first())*TEXTURE_SIZE;
        float offsetY = (coordinateWithPartials.first().y() - world.getPlayer().getCoordinate().y() + coordinateWithPartials.second().second())*TEXTURE_SIZE;

        if (!isPlayer)
        {
            offsetX += partialX*TEXTURE_SIZE;
            offsetY += partialY*TEXTURE_SIZE;
        }

        final float extraY = (texture.isTall()) ? TEXTURE_SIZE : 0.0f;
        final boolean hm = (texture.isHorizontallyMirrored());
        final boolean vm = (texture.isVerticallyMirrored());

        glBegin(GL_QUADS);
        glTexCoord2f(hm ? 1 : 0, vm ? 1 : 0);
        glVertex2f(-HALF_TEXTURE_SIZE+offsetX, HALF_TEXTURE_SIZE+offsetY+extraY); //Top left
        glTexCoord2f(hm ? 0 : 1, vm ? 1 : 0);
        glVertex2f(HALF_TEXTURE_SIZE+offsetX, HALF_TEXTURE_SIZE+offsetY+extraY); //Top right
        glTexCoord2f(hm ? 0 : 1, vm ? 0 : 1);
        glVertex2f(HALF_TEXTURE_SIZE+offsetX, -HALF_TEXTURE_SIZE+offsetY); //Bottom right
        glTexCoord2f(hm ? 1 : 0, vm ? 0 : 1);
        glVertex2f(-HALF_TEXTURE_SIZE+offsetX, -HALF_TEXTURE_SIZE+offsetY); //Bottom left
        glEnd();
    }

    public void renderConnectedTextureTile(final int x, final int y)
    {
        ((ConnectedTextureTile) world.getTileAt(x, y)).getQuadrantTextures().forEach((secondary, texture) ->
        {
            texture.bind();

            float offsetX = (x - world.getPlayer().getCoordinate().x() + partialX)*TEXTURE_SIZE;
            float offsetY = (y - world.getPlayer().getCoordinate().y() + partialY)*TEXTURE_SIZE;

            final boolean hm = texture.isHorizontallyMirrored();
            final boolean vm = texture.isVerticallyMirrored();

            final float u = secondary.getVerticalDirection() == Direction.UP ? HALF_TEXTURE_SIZE : 0.0f;
            final float r = secondary.getHorizontalDirection() == Direction.RIGHT ? HALF_TEXTURE_SIZE : 0.0f;
            final float extraY = (texture.isTall()) ? HALF_TEXTURE_SIZE : 0.0f;

            glBegin(GL_QUADS);
            glTexCoord2f(hm ? 1 : 0, vm ? 1 : 0);
            glVertex2f(-HALF_TEXTURE_SIZE+offsetX+r, offsetY+u+extraY); //Top left
            glTexCoord2f(hm ? 0 : 1, vm ? 1 : 0);
            glVertex2f(offsetX+r, offsetY+u+extraY); //Top right
            glTexCoord2f(hm ? 0 : 1, vm ? 0 : 1);
            glVertex2f(offsetX+r, -HALF_TEXTURE_SIZE+offsetY+u); //Bottom right
            glTexCoord2f(hm ? 1 : 0, vm ? 0 : 1);
            glVertex2f(-HALF_TEXTURE_SIZE+offsetX+r, -HALF_TEXTURE_SIZE+offsetY+u); //Bottom left
            glEnd();
        });
    }
}
