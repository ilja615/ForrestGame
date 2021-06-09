/*
 * Copyright (c) 2021 ilja615.
 *
 * This file is part of Forrest Game.
 *
 * Forrest Game is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Forrest Game is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Forrest Game.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.ilja615.forrestgame.world;

import com.github.ilja615.forrestgame.Game;
import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.entity.Player;
import com.github.ilja615.forrestgame.entity.StatTracker.Stat;
import com.github.ilja615.forrestgame.gui.particle.Particle;
import com.github.ilja615.forrestgame.gui.renderer.TextRenderer;
import com.github.ilja615.forrestgame.gui.renderer.TextureRenderer;
import com.github.ilja615.forrestgame.gui.renderer.UiRenderer;
import com.github.ilja615.forrestgame.gui.shader.Shader;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;
import com.github.ilja615.forrestgame.tiles.*;
import com.github.ilja615.forrestgame.tiles.items.BushItem;
import com.github.ilja615.forrestgame.tiles.items.MushroomItem;
import com.github.ilja615.forrestgame.tiles.items.SignItem;
import com.github.ilja615.forrestgame.tiles.items.TreeItem;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.KeyInput;
import com.github.ilja615.forrestgame.util.ShortPathFinder;
import com.google.common.collect.Lists;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;

public class PlayerWorld implements World
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerWorld.class);
    private final Tile[] tiles = new Tile[WORLD_WIDTH * WORLD_HEIGHT];
    private final ArrayList<Particle> particles = new ArrayList<>();
    private final Game game;
    private final Entity player;
    private final TextRenderer textRenderer = new TextRenderer();
    private final UiRenderer uiRenderer = new UiRenderer();
    private final TextureRenderer textureRenderer;
    private Coordinate startCoordinate;
    // private final List<Coordinate> path = new ArrayList<>();
    private final TimeTracker timeTracker;
    private final Shader shader;

    public PlayerWorld(final Game game, final Shader shader)
    {
        this.game = game;
        this.player = new Player(this, new Coordinate(0, 0));
        this.timeTracker = new TimeTracker();
        this.textureRenderer = new TextureRenderer(this);
        this.shader = shader;
        this.generate();
    }

    @Override
    public Game getGame()
    {
        return game;
    }

    @Override
    public Tile[] getTiles()
    {
        return tiles;
    }

    @Override
    public Entity getPlayer()
    {
        return player;
    }

    @Override
    public TimeTracker getTimeTracker()
    {
        return timeTracker;
    }

    @Override
    public TextureRenderer getTextureRenderer()
    {
        return textureRenderer;
    }

    @Override
    public TextRenderer getTextRenderer()
    {
        return textRenderer;
    }

    @Override
    public ArrayList<Particle> getParticles()
    {
        return particles;
    }

    @Override
    public void generate()
    {
        if (!(WORLD_WIDTH > 4 && WORLD_HEIGHT > 4))
        {
            LOGGER.error("The board size is too small, it has to be minimal 4x4.");
            this.onBoardFailureToCreate();
        }

        // Make the square world
        for (int x = 0; x < WORLD_WIDTH; x++)
        {
            for (int y = 0; y < WORLD_HEIGHT; y++)
            {
                if (x == 0 || y == 0 || x == WORLD_WIDTH - 1 || y == WORLD_HEIGHT - 1)
                {
                    tiles[x + (y * WORLD_WIDTH)] = new RockTile(Textures.WALL);
                } else
                {
                    Texture texture = ThreadLocalRandom.current().nextBoolean() ? Textures.GROUND_0 : Textures.GROUND_ALTERNATIVES[ThreadLocalRandom.current().nextInt(Textures.GROUND_ALTERNATIVES.length)];
                    tiles[x + (y * WORLD_WIDTH)] = new FloorTile(texture);
                }
            }
        }

        // TODO : Make a new carver system

        // Adds bush and rock obstacles scattered around the world
        placeSimpleObstacles();

        // Adds start and end
        startCoordinate = placeStart();
        final Coordinate end = placeEndSign();

        // A check if there exists a valid path , if not , the entire world must be re-created
        // idk if theres a use for path yet - xf8b
        final ShortPathFinder pathFinder = new ShortPathFinder();
        final List<Coordinate> path = pathFinder.findPath(this, startCoordinate, end);

        if (path.isEmpty())
        {
            LOGGER.error("No valid path was found!");
            this.onBoardFailureToCreate();
        } else
        {
            LOGGER.info("Found path {}", path.stream()
                    .map(Coordinate::toString)
                    .collect(Collectors.joining(" -> ")));
        }
    }
    public void placeSimpleObstacles()
    {
        // Note this for loop is a bit smaller because it skips the very outer edge
        for (int x = 1; x < WORLD_WIDTH - 1; x++)
        {
            for (int y = 1; y < WORLD_HEIGHT - 1; y++)
            {
                if (tiles[x + (y * WORLD_WIDTH)] instanceof FloorTile)
                {
                    final int random = ThreadLocalRandom.current().nextInt(24);

                    switch (random)
                    {
                        case 0, 1, 2, 3 -> tiles[x + (y * WORLD_WIDTH)].setItem(new BushItem());
                        case 4, 5 -> tiles[x + (y * WORLD_WIDTH)] = new RockTile(Textures.WALL);
                        case 6 -> tiles[x + (y * WORLD_WIDTH)].setItem(new MushroomItem(Textures.MUSHROOM));
                        case 7 -> tiles[x + (y * WORLD_WIDTH)].setItem(new TreeItem(Textures.TREE));
                    }
                }
            }
        }
    }

    public Coordinate placeEndSign()
    {
        Coordinate coordinate;
        if (ThreadLocalRandom.current().nextBoolean())
        {
            int pos = WORLD_WIDTH * WORLD_HEIGHT - 1;
            coordinate = new Coordinate(0, 0);
            while (true)
            {
                if (pos > 0)
                {
                    if (!(tiles[pos - 1] instanceof FloorTile))
                    {
                        pos -= WORLD_WIDTH;
                    } else
                    {
                        tiles[pos] = new FloorTile(Textures.GROUND_0);
                        tiles[pos].setItem(new SignItem(Textures.SIGN));
                        coordinate = new Coordinate(WORLD_WIDTH - 1, (pos + 1) / WORLD_WIDTH - 1);
                        LOGGER.info("placed end sign at: {}", coordinate);
                        break;
                    }
                } else
                {
                    LOGGER.error("Failed to create end sign");
                    this.onBoardFailureToCreate();
                    break;
                }
            }
        } else {
            int pos = WORLD_WIDTH - 1;
            coordinate = new Coordinate(0, 0);
            while (true)
            {
                if (pos < WORLD_WIDTH * WORLD_HEIGHT - 1)
                {
                    if (!(tiles[pos - 1] instanceof FloorTile))
                    {
                        pos += WORLD_WIDTH;
                    } else
                    {
                        tiles[pos] = new FloorTile(Textures.GROUND_0);
                        tiles[pos].setItem(new SignItem(Textures.SIGN));
                        coordinate = new Coordinate(WORLD_WIDTH - 1, (pos + 1) / WORLD_WIDTH - 1);
                        LOGGER.info("placed end sign at: {}", coordinate);
                        break;
                    }
                } else
                {
                    LOGGER.error("Failed to create end sign");
                    this.onBoardFailureToCreate();
                    break;
                }
            }
        }

        return coordinate;
    }

    public Coordinate placeStart()
    {
        int pos = WORLD_WIDTH;
        Coordinate coordinate = new Coordinate(0, 0);

        while (true)
        {
            if (pos < tiles.length)
            {
                if (!(tiles[pos + 1] instanceof FloorTile))
                {
                    pos += WORLD_WIDTH;
                } else
                {
                    tiles[pos] = new FloorTile(Textures.GROUND_0);
                    coordinate = new Coordinate(0, (pos + 1) / WORLD_WIDTH);
                    LOGGER.info("placed start at: {}", coordinate);
                    player.setCoordinate(coordinate);
                    break;
                }
            } else
            {
                LOGGER.error("Failed to create player start pos");
                this.onBoardFailureToCreate();
                break;
            }
        }

        return coordinate;
    }

    private void onBoardFailureToCreate()
    {
        LOGGER.error("Failed board detected, retrying... (Don't worry, it can happen sometimes)");
        this.generate();
    }

    @Override
    public void tick()
    {
        glUniform1f(glGetUniformLocation(this.shader.program,"redComponent"), this.timeTracker.getRedComponent());
        glUniform1f(glGetUniformLocation(this.shader.program,"greenComponent"), this.timeTracker.getGreenComponent());
        glUniform1f(glGetUniformLocation(this.shader.program,"blueComponent"), this.timeTracker.getBlueComponent());

        // Tick all objects
        player.tick();
        particles.forEach(Particle::tick);

        // Clear the particles that should be removed
        particles.removeIf(Particle::isExpired);

        if (timeTracker.waitTicks > 0) timeTracker.waitTicks--;
        if (timeTracker.waitTicks == 0) textureRenderer.setEnabled();

        if (KeyInput.isKeyDown(game, GLFW.GLFW_KEY_R))
        {
            player.setCoordinate(startCoordinate);
        }

        if (textureRenderer.isEnabled())
        {

            textureRenderer.renderBoard();
            textureRenderer.renderViewport();

            uiRenderer.renderEnergy(player);
            uiRenderer.renderHealth(player);
            textRenderer.drawString(this.getTimeTracker().getCurrentDayString(), -0.96f, 0.93f, 0.5f);
            uiRenderer.renderTimeIcon(this.getTimeTracker().getPeriodFromTime(this.timeTracker.getCurrentTime()));
        } else {
            String s = this.getTimeTracker().getCurrentTimeString();
            float size = 20.0f/(s.length()+2);
            textRenderer.drawString(s, -1f, -0.05f * size, size);
        }
    }

    @Override
    public void onEnemyTurn()
    {
        player.setMobile(true);
    }
}