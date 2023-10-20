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

import com.github.ilja615.forrestgame.entity.Entity;
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

import static org.lwjgl.opengl.GL11.*;

public class TextureRenderer
{
    public final Multimap<Pair<Coordinate, Pair<Float, Float>>, Object> LAYER_BACK = MultimapBuilder.treeKeys().arrayListValues().build(); // For things between the floor and the entities
    public final Multimap<Pair<Coordinate, Pair<Float, Float>>, Object> LAYER_MIDDLE = MultimapBuilder.treeKeys().arrayListValues().build(); // For entities
    public final Multimap<Pair<Coordinate, Pair<Float, Float>>, Object> LAYER_FRONT = MultimapBuilder.treeKeys().arrayListValues().build(); // For foreground things
    private final World world;
    private float partialX = 0f;
    private float partialY = 0f;
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
        LAYER_MIDDLE.clear();
        LAYER_FRONT.clear();
    }

    public void renderBoard()
    {
        // Sort through all the tiles within view distance and put them in their layer.
        for (int y = world.getPlayer().getCoordinate().y() + 4; y >= world.getPlayer().getCoordinate().y() - 6; y--)
        {
            for (int x = world.getPlayer().getCoordinate().x() - 6; x <= world.getPlayer().getCoordinate().x() + 6; x++)
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
        LAYER_MIDDLE.put(new Pair<>(world.getPlayer().getCoordinate(), new Pair<>(0f, 0f)), world.getPlayer().getCurrentTexture());

        // For every layer, render every texture.
        renderAtLayer(LAYER_BACK);
        renderAtLayer(LAYER_MIDDLE);
        renderAtLayer(LAYER_FRONT);

        world.getParticles().forEach(particle -> renderTexture(particle.getCurrentTexture(), particle.getCoordinate().x(), particle.getCoordinate().y(), 0, 0));
    }

    public void renderAtLayer(Multimap<Pair<Coordinate, Pair<Float, Float>>, Object> layer)
    {
        layer.entries().stream().forEachOrdered(entry ->
        {
            if (entry.getValue() instanceof Texture texture)
            {
                renderTexture(texture, entry.getKey().first().x(), entry.getKey().first().y(), entry.getKey().second().first(), entry.getKey().second().second());
            }
            if (entry.getValue() instanceof Tile tile)
            {
                if (tile instanceof ConnectedTextureTile connectedTextureTile && connectedTextureTile.shouldShowConnectedTextures())
                {
                    renderConnectedTextureTile(entry.getKey().first().x(), entry.getKey().first().y());
                } else
                {
                    renderTexture(tile.getTexture(), entry.getKey().first().x(), entry.getKey().first().y(), 0, 0);
                }
            }
            if (entry.getValue() instanceof Item item)
            {
                renderTexture(item.getCurrentTexture(), entry.getKey().first().x(), entry.getKey().first().y(), entry.getKey().second().first(), entry.getKey().second().second());
            }
        });
    }

    public void renderTexture(final Texture texture, int x, int y, float px, float py)
    {
        if (texture.isPlayerTexture())
        {
            renderPlayer(this.world.getPlayer());
            return;
        }
        texture.bind();

        x += world.WORLD_WIDTH / 2 - world.getPlayer().getCoordinate().x();
        y += world.WORLD_HEIGHT / 2 - world.getPlayer().getCoordinate().y();

        final float worldStarterX = (-0.0833f * world.WORLD_WIDTH);
        final float worldStarterY = (-0.0833f * world.WORLD_HEIGHT);

        final float extraY = (texture.isTall()) ? 0.167f : 0.0f;
        final boolean hm = (texture.isHorizontallyMirrored());
        final boolean vm = (texture.isVerticallyMirrored());

        glBegin(GL_QUADS);
        glTexCoord2f(hm ? 1 : 0, vm ? 1 : 0);
        glVertex2f(-0.0834f + worldStarterX + ((float) x + partialX + px) / 6.0f, 0.25f + worldStarterY + extraY + ((float) y + partialY + py) / 6.0f);
        glTexCoord2f(hm ? 0 : 1, vm ? 1 : 0);
        glVertex2f(0.0834f + worldStarterX + ((float) x + partialX + px) / 6.0f, 0.25f + worldStarterY + extraY + ((float) y + partialY +py) / 6.0f);
        glTexCoord2f(hm ? 0 : 1, vm ? 0 : 1);
        glVertex2f(0.0834f + worldStarterX + ((float) x + partialX + px) / 6.0f, 0.083f + worldStarterY + ((float) y + partialY + py) / 6.0f);
        glTexCoord2f(hm ? 1 : 0, vm ? 0 : 1);
        glVertex2f(-0.084f + worldStarterX + ((float) x + partialX + px) / 6.0f, 0.083f + worldStarterY + ((float) y + partialY + py) / 6.0f);
        glEnd();
    }

    public void renderPlayer(final Entity player)
    {
        final Texture texture = player.getCurrentTexture();
        texture.bind();

        final float extraY = (texture.isTall()) ? 0.167f : 0.0f;
        final boolean hm = (texture.isHorizontallyMirrored());
        final boolean vm = (texture.isVerticallyMirrored());

        glTranslatef(0, 0.083f, 0);
        // glRotated(playerAngle,0,0,1);
        glBegin(GL_QUADS);
        glTexCoord2f(hm ? 1 : 0, vm ? 1 : 0);
        glVertex2f(-0.0834f, 0.167f + extraY);
        glTexCoord2f(hm ? 0 : 1, vm ? 1 : 0);
        glVertex2f(0.0834f, 0.167f + extraY);
        glTexCoord2f(hm ? 0 : 1, vm ? 0 : 1);
        glVertex2f(0.0834f, 0);
        glTexCoord2f(hm ? 1 : 0, vm ? 0 : 1);
        glVertex2f(-0.0834f, 0);
        glEnd();
        // glRotated(-playerAngle,0,0,1);
        glTranslatef(0, -0.083f, 0);
    }

    public void renderConnectedTextureTile(final int x, final int y)
    {
        final float worldStarterX = (-0.0833f * world.WORLD_WIDTH);
        final float worldStarterY = (-0.0833f * world.WORLD_HEIGHT);
        final int finalX = x + world.WORLD_WIDTH / 2 - world.getPlayer().getCoordinate().x();
        final int finalY = y + world.WORLD_HEIGHT / 2 - world.getPlayer().getCoordinate().y();
        ((ConnectedTextureTile) world.getTileAt(x, y)).getQuadrantTextures().forEach((secondary, texture) ->
        {
            texture.bind();

            final boolean hm = texture.isHorizontallyMirrored();
            final boolean vm = texture.isVerticallyMirrored();

            final float u = secondary.getVerticalDirection() == Direction.UP ? 0.0834f : 0.0f;
            final float r = secondary.getHorizontalDirection() == Direction.RIGHT ? 0.0834f : 0.0f;
            final float extraY = (texture.isTall()) ? 0.0834f : 0.0f;

            glBegin(GL_QUADS);
            glTexCoord2f(hm ? 1 : 0, vm ? 1 : 0);
            glVertex2f(-0.0834f + r + worldStarterX + ((float) finalX + partialX) / 6.0f, 0.167f + extraY + u + worldStarterY + ((float) finalY + partialY) / 6.0f);
            glTexCoord2f(hm ? 0 : 1, vm ? 1 : 0);
            glVertex2f(0.0f + r + worldStarterX + ((float) finalX + partialX) / 6.0f, 0.167f + extraY + u + worldStarterY + ((float) finalY + partialY) / 6.0f);
            glTexCoord2f(hm ? 0 : 1, vm ? 0 : 1);
            glVertex2f(0.0f + r + worldStarterX + ((float) finalX + partialX) / 6.0f, 0.083f + u + worldStarterY + ((float) finalY + partialY) / 6.0f);
            glTexCoord2f(hm ? 1 : 0, vm ? 0 : 1);
            glVertex2f(-0.084f + r + worldStarterX + ((float) finalX + partialX) / 6.0f, 0.083f + u + worldStarterY + ((float) finalY + partialY) / 6.0f);
            glEnd();
        });
    }
}
