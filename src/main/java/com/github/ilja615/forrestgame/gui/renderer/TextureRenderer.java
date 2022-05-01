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
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Direction;
import com.github.ilja615.forrestgame.util.Pair;
import com.github.ilja615.forrestgame.world.World;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class TextureRenderer
{
    public final ArrayList<Pair<Coordinate, Texture>> LAYER_BACK = new ArrayList<>(); // For things between the floor and the entities
    public final ArrayList<Pair<Coordinate, Texture>> LAYER_MIDDLE = new ArrayList<>(); // For entities
    public final ArrayList<Pair<Coordinate, Texture>> LAYER_FRONT = new ArrayList<>(); // For foreground things
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

    public void clearLists()
    {
        // Clear the lists
        LAYER_BACK.clear();
        LAYER_MIDDLE.clear();
        LAYER_FRONT.clear();
    }

    public void renderBoard()
    {
        // Add things to the list that need to be rendered
        LAYER_MIDDLE.add(new Pair<>(world.getPlayer().getCoordinate(), world.getPlayer().getCurrentTexture()));

        // Renders all the tiles on the board. Tiles are rendered on "layer 0" so that is even behind layer back.
        for (int y = world.getPlayer().getCoordinate().getY() + 4; y >= world.getPlayer().getCoordinate().getY() - 6; y--)
        {
            for (int x = world.getPlayer().getCoordinate().getX() - 6; x <= world.getPlayer().getCoordinate().getX() + 6; x++)
            {
                if (x >= 0 && x < world.WORLD_WIDTH && y >= 0 && y < world.WORLD_HEIGHT)
                {
                    final Tile tile = world.getTiles()[x + (y * world.WORLD_WIDTH)];

                    if (tile instanceof ConnectedTextureTile connectedTextureTile && connectedTextureTile.shouldShowConnectedTextures())
                    {
                        renderConnectedTextureTile(x, y);
                    }
                    else
                    {
                        final Texture texture = tile.getTexture();
                        renderTexture(texture, x, y);
                    }

                    if (tile.hasItem())
                        tile.getItem().whichLayer(this).add(new Pair<>(new Coordinate(x, y), tile.getItem().getCurrentTexture()));
                }
            }
        }
        LAYER_BACK.forEach(coordinateTexturePair -> renderTexture(coordinateTexturePair.getSecondThing(), coordinateTexturePair.getFirstThing().getX(), coordinateTexturePair.getFirstThing().getY()));
        LAYER_MIDDLE.forEach(coordinateTexturePair -> renderTexture(coordinateTexturePair.getSecondThing(), coordinateTexturePair.getFirstThing().getX(), coordinateTexturePair.getFirstThing().getY()));
        LAYER_FRONT.forEach(coordinateTexturePair -> renderTexture(coordinateTexturePair.getSecondThing(), coordinateTexturePair.getFirstThing().getX(), coordinateTexturePair.getFirstThing().getY()));

        world.getParticles().forEach(particle -> renderTexture(particle.getCurrentTexture(), particle.getCoordinate().getX(), particle.getCoordinate().getY()));
    }

    public void renderTexture(final Texture texture, int x, int y)
    {
        if (texture.isPlayerTexture())
        {
            renderPlayer(this.world.getPlayer());
            return;
        }
        texture.bind();

        x += world.WORLD_WIDTH / 2 - world.getPlayer().getCoordinate().getX();
        y += world.WORLD_HEIGHT / 2 - world.getPlayer().getCoordinate().getY();

        float worldStarterX = (-0.0833f * world.WORLD_WIDTH);
        float worldStarterY = (-0.0833f * world.WORLD_HEIGHT);

        float extraY = (texture.isTall()) ? 0.167f : 0.0f;
        boolean hm = (texture.isHorizontallyMirrored());
        boolean vm = (texture.isVerticallyMirrored());

        glBegin(GL_QUADS);
        glTexCoord2f(hm ? 1 : 0, vm ? 1 : 0);
        glVertex2f(-0.0834f + worldStarterX + ((float) x + partialX) / 6.0f, 0.25f + worldStarterY + extraY + ((float) y + partialY) / 6.0f);
        glTexCoord2f(hm ? 0 : 1, vm ? 1 : 0);
        glVertex2f(0.0834f + worldStarterX + +((float) x + partialX) / 6.0f, 0.25f + worldStarterY + extraY + ((float) y + partialY) / 6.0f);
        glTexCoord2f(hm ? 0 : 1, vm ? 0 : 1);
        glVertex2f(0.0834f + worldStarterX + ((float) x + partialX) / 6.0f, 0.083f + worldStarterY + ((float) y + partialY) / 6.0f);
        glTexCoord2f(hm ? 1 : 0, vm ? 0 : 1);
        glVertex2f(-0.084f + worldStarterX + ((float) x + partialX) / 6.0f, 0.083f + worldStarterY + ((float) y + partialY) / 6.0f);
        glEnd();
    }

    public void renderPlayer(Entity player)
    {
        Texture texture = player.getCurrentTexture();
        texture.bind();

        float extraY = (texture.isTall()) ? 0.167f : 0.0f;
        boolean hm = (texture.isHorizontallyMirrored());
        boolean vm = (texture.isVerticallyMirrored());

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

    public void renderConnectedTextureTile(int x, int y)
    {
        float worldStarterX = (-0.0833f * world.WORLD_WIDTH);
        float worldStarterY = (-0.0833f * world.WORLD_HEIGHT);
        final int finalX = x + world.WORLD_WIDTH / 2 - world.getPlayer().getCoordinate().getX();
        final int finalY = y + world.WORLD_HEIGHT / 2 - world.getPlayer().getCoordinate().getY();
        ((ConnectedTextureTile) world.getTileAt(x, y)).getQuadrantTextures().forEach((secondary, texture) ->
        {
            texture.bind();

            boolean hm = texture.isHorizontallyMirrored();
            boolean vm = texture.isVerticallyMirrored();

            float u = secondary.getVertical() == Direction.UP ? 0.0834f : 0.0f;
            float r = secondary.getHorizontal() == Direction.RIGHT ? 0.0834f : 0.0f;

            glBegin(GL_QUADS);
            glTexCoord2f(hm ? 1 : 0, vm ? 1 : 0);
            glVertex2f(  -0.0834f + r + worldStarterX + ((float)finalX + partialX) / 6.0f, 0.167f + u + worldStarterY + ((float)finalY + partialY) / 6.0f);
            glTexCoord2f(hm ? 0 : 1, vm ? 1 : 0);
            glVertex2f(0.0f + r + worldStarterX + + ((float)finalX + partialX) / 6.0f, 0.167f + u + worldStarterY + ((float)finalY + partialY) / 6.0f);
            glTexCoord2f(hm ? 0 : 1, vm ? 0 : 1);
            glVertex2f(0.0f + r + worldStarterX + ((float)finalX + partialX) / 6.0f, 0.083f + u + worldStarterY + ((float)finalY + partialY) / 6.0f);
            glTexCoord2f(hm ? 1 : 0, vm ? 0 : 1);
            glVertex2f(-0.084f + r + worldStarterX + ((float)finalX + partialX) / 6.0f, 0.083f + u + worldStarterY + ((float)finalY + partialY) / 6.0f);
            glEnd();
        });
    }
}
