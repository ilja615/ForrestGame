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

package com.github.ilja615.forrestgame.world;

import com.github.ilja615.forrestgame.Game;
import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.entity.Player;
import com.github.ilja615.forrestgame.entity.Scamperer;
import com.github.ilja615.forrestgame.gui.particle.Particle;
import com.github.ilja615.forrestgame.gui.renderer.TextRenderer;
import com.github.ilja615.forrestgame.gui.renderer.TextureRenderer;
import com.github.ilja615.forrestgame.gui.renderer.UiRenderer;
import com.github.ilja615.forrestgame.gui.shader.Shader;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;
import com.github.ilja615.forrestgame.tiles.*;
import com.github.ilja615.forrestgame.tiles.items.*;
import com.github.ilja615.forrestgame.util.*;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;

public class World implements Tickable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(World.class);
    public final AirTile airTile;
    private final ArrayList<Particle> particles = new ArrayList<>();
    private final Game game;
    private final Entity player;
    private final ArrayList<Entity> entities = new ArrayList<>();
    private ArrayList<Entity> this_turn_entity_stack = new ArrayList<>();
    private final TextRenderer textRenderer = new TextRenderer();
    private final UiRenderer uiRenderer = new UiRenderer();
    private final TextureRenderer textureRenderer;
    // private final List<Coordinate> path = new ArrayList<>();
    private final TimeTracker timeTracker;
    private final Shader shader;
    public int WORLD_WIDTH;
    public int WORLD_HEIGHT;
    private Tile[] tiles;
    private Coordinate startCoordinate;
    private int enemyTurnWait;

    public World(final Game game, final Shader shader)
    {
        this.timeTracker = new TimeTracker();
        this.textureRenderer = new TextureRenderer(this);
        this.player = new Player(this, new Coordinate(0, 0));
        this.game = game;
        this.shader = shader;
        this.airTile = new AirTile(Textures.AIR);
        this.generate();
    }

    public Game getGame()
    {
        return game;
    }

    public Tile[] getTiles()
    {
        return tiles;
    }

    public Entity getPlayer()
    {
        return player;
    }

    public TimeTracker getTimeTracker()
    {
        return timeTracker;
    }

    public TextureRenderer getTextureRenderer()
    {
        return textureRenderer;
    }

    public TextRenderer getTextRenderer()
    {
        return textRenderer;
    }

    public ArrayList<Particle> getParticles()
    {
        return particles;
    }

    public ArrayList<Entity> getEntities()
    {
        return entities;
    }

    public boolean isWithinView(Coordinate coordinate)
    {
        return coordinate.getX() >= player.getCoordinate().getX() - 5 &&
                coordinate.getX() <= player.getCoordinate().getX() + 5 &&
                coordinate.getY() >= player.getCoordinate().getY() - 4 &&
                coordinate.getY() <= player.getCoordinate().getY() + 3;
    }

    public ArrayList<Entity> getEntitiesWithinView()
    {
        return entities.stream().filter
                (entity -> isWithinView(entity.getCoordinate())).collect(Collectors.toCollection(ArrayList::new));
    }

    public void generate()
    {
        WORLD_WIDTH = 6 + ThreadLocalRandom.current().nextInt(7) * 2;
        WORLD_HEIGHT = 6 + ThreadLocalRandom.current().nextInt(6) * 2;
        this.tiles = new Tile[WORLD_WIDTH * WORLD_HEIGHT];

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
                    tiles[x + (y * WORLD_WIDTH)] = new WallTile(Textures.AIR);
                } else
                {
                    Texture texture = ThreadLocalRandom.current().nextBoolean() ? Textures.GRASS_0 : Textures.GROUND_ALTERNATIVES[ThreadLocalRandom.current().nextInt(Textures.GROUND_ALTERNATIVES.length)];
                    tiles[x + (y * WORLD_WIDTH)] = new FloorTile(texture);
                }
            }
        }

        // TODO : Make a new carver system

        // Adds bush and wall obstacles scattered around the world
        placeRockClusters();
        placeSimpleObstacles();

        // Adds creatures
        this.entities.clear();
        placeEntities();

        // Adds start and end
        startCoordinate = placeStart();
        final Coordinate end = placeEndSign();

        // A check if there exists a valid path , if not , the entire world must be re-created
        // idk if theres a use for path yet - xf8b
        final ShortPathFinder pathFinder = new ShortPathFinder();
        final List<Coordinate> path = pathFinder.findPath(this, startCoordinate, end, player);

        if (path.isEmpty())
        {
            LOGGER.error("No valid path was found!");
            this.onBoardFailureToCreate();
        } else
        {
            LOGGER.info("Found path {}", path.stream()
                    .map(Coordinate::toString)
                    .collect(Collectors.joining(" -> ")));

            for (Coordinate coordinate : path.subList(0, path.size() - 1))
            {
                if (isWithinWorld(coordinate))
                {
                    tiles[coordinate.getX() + coordinate.getY()*WORLD_WIDTH] = new DirtTile(Textures.AIR);

                    // Sometimes add an item
                    int item = ThreadLocalRandom.current().nextInt(10);
                    if (item < Textures.PATH_DECORATION_ALTERNATIVES.length)
                    {
                        tiles[coordinate.getX() + coordinate.getY()*WORLD_WIDTH].setItem(new DecorationItem(Textures.PATH_DECORATION_ALTERNATIVES[item]));
                    }
                }
            }
        }

        // Will do other stuff at the very end
        postGeneration();
    }

    public void placeRockClusters()
    {
        // Note this for loop is a bit smaller because it skips the very outer edge
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(5) + 2; i++)
        {
            int startX = 1 + ThreadLocalRandom.current().nextInt(WORLD_WIDTH - 1);
            int startY = 1 + ThreadLocalRandom.current().nextInt(WORLD_HEIGHT - 1);
            for (int x = startX; x < startX + ThreadLocalRandom.current().nextInt(3) + 2; x++)
            {
                for (int y = startY; y < startY + ThreadLocalRandom.current().nextInt(3) + 2; y++)
                {
                    if (isWithinWorld(new Coordinate(x, y)) && getTileAt(x, y) instanceof FloorTile)
                        tiles[x + (y * WORLD_WIDTH)] = new WallTile(Textures.AIR);
                }
            }
        }
    }

    public void placeSimpleObstacles()
    {
        // Note this for loop is a bit smaller because it skips the very outer edge
        for (int x = 1; x < WORLD_WIDTH - 1; x++)
        {
            for (int y = 1; y < WORLD_HEIGHT - 1; y++)
            {
                if (getTileAt(x, y) instanceof FloorTile)
                {
                    final int random = ThreadLocalRandom.current().nextInt(24);

                    switch (random)
                    {
                        case 0, 1, 2, 3 -> tiles[x + (y * WORLD_WIDTH)].setItem(new BushItem());
                        case 4 -> tiles[x + (y * WORLD_WIDTH)] = new WallTile(Textures.AIR);
                        case 6 -> tiles[x + (y * WORLD_WIDTH)].setItem(new MushroomItem(Textures.MUSHROOM[ThreadLocalRandom.current().nextInt(Textures.MUSHROOM.length)]));
                        case 7 -> tiles[x + (y * WORLD_WIDTH)].setItem(new TreeItem(Textures.TREE));
                        case 8 -> tiles[x + (y * WORLD_WIDTH)].setItem(new CrateItem(Textures.CRATE));
                    }
                }
            }
        }
    }

    public void placeEntities()
    {
        // Note this for loop is a bit smaller because it skips the very outer edge
        for (int x = 1; x < WORLD_WIDTH - 1; x++)
        {
            for (int y = 1; y < WORLD_HEIGHT - 1; y++)
            {
                if (getTileAt(x, y) instanceof FloorTile && !getTileAt(x, y).hasItem())
                {
                    final int random = ThreadLocalRandom.current().nextInt(26 - 2*(timeTracker.getCurrentTime() % 6));

                    switch (random)
                    {
                        case 0 -> entities.add(new Scamperer(this, new Coordinate(x, y)));
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
                        tiles[pos] = new FloorTile(Textures.GRASS_0);
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
        } else
        {
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
                        tiles[pos] = new FloorTile(Textures.GRASS_0);
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
                    tiles[pos] = new FloorTile(Textures.GRASS_0);
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

    private void postGeneration()
    {
        // It should give all the walls and dirt their correct texture
        for (int x = 0; x < WORLD_WIDTH; x++)
        {
            for (int y = 0; y < WORLD_HEIGHT; y++)
            {
                Coordinate mutableCoordinate = new Coordinate(x,y);
                if (isWithinWorld(mutableCoordinate))
                {
                    if (getTileAt(x, y) instanceof ConnectedTextureTile)
                        ((ConnectedTextureTile) getTileAt(x, y)).adaptQuadrantTexturesList(this, new Coordinate(x, y));
                }
            }
        }
    }

    @Override
    public void tick()
    {
        glUniform1f(glGetUniformLocation(this.shader.program, "redComponent"), this.timeTracker.getRedComponent());
        glUniform1f(glGetUniformLocation(this.shader.program, "greenComponent"), this.timeTracker.getGreenComponent());
        glUniform1f(glGetUniformLocation(this.shader.program, "blueComponent"), this.timeTracker.getBlueComponent());

        // Tick all objects
        player.tick();
        particles.forEach(Particle::tick);
        entities.forEach(Entity::tick);

        // Clear the particles that should be removed
        particles.removeIf(Particle::isExpired);

        if (timeTracker.waitTicks > 0) timeTracker.waitTicks--;
        if (timeTracker.waitTicks == 0) textureRenderer.setEnabled();

        if (enemyTurnWait > 0)
        {
            enemyTurnWait--;if (enemyTurnWait == 0)
                onEnemyTurn(); // If there were any enemies and they waited enough, it is now their turn.
        }

        if (KeyInput.isKeyDown(game, GLFW.GLFW_KEY_R))
        {
            player.setCoordinate(startCoordinate);
        }

        if (textureRenderer.isEnabled())
        {
            // Board
            textureRenderer.clearLists();
            textureRenderer.LAYER_MIDDLE.add(new Pair<>(player.getCoordinate(), player.getCurrentTexture()));
            entities.forEach(entity -> entity.whichLayer(textureRenderer).add(new Pair<>(entity.getCoordinate(), entity.getCurrentTexture())));
            textureRenderer.renderBoard(); // Tiles and items get added to the lists in here and the rendering gets called.

            // UI
            uiRenderer.renderViewport();
            uiRenderer.renderEnergy(player);
            uiRenderer.renderHealth(player);
            textRenderer.drawString(this.getTimeTracker().getCurrentDayString(), -0.96f, 0.93f, 0.5f);
            uiRenderer.renderTimeIcon(this.getTimeTracker().getPeriodFromTime(this.timeTracker.getCurrentTime()));
        } else
        {
            String s = this.getTimeTracker().getCurrentTimeString();
            float size = 20.0f / (s.length() + 2);
            textRenderer.drawString(s, -1f, -0.05f * size, size);
        }
    }

    public void onEnemyTurn()
    {
        this_turn_entity_stack = getEntitiesWithinView();

        if (this_turn_entity_stack.size() > 0)
        {
            enemyTurnWait = 200;
        } else {
            player.setMobile(true);
            // There were no enemies
        }
    }

    private void nextEnemyTurn()
    {
        if (this_turn_entity_stack.size() > 0)
        {
            this_turn_entity_stack.get(0).automaticallyMove();
            this_turn_entity_stack.remove(0);
            if (this_turn_entity_stack.size() > 0)
            {
                enemyTurnWait = 100; // next one
            }
        }

        if (this_turn_entity_stack.isEmpty())
        {
            player.setMobile(true);
            // No enemies left
        }
    }

    public boolean isWithinWorld(Coordinate coordinate)
    {
        int i = coordinate.getX() + coordinate.getY() * WORLD_WIDTH;
        return i >= 0 && i < tiles.length && coordinate.getX() >= 0 && coordinate.getX() < WORLD_WIDTH && coordinate.getY() >= 0 && coordinate.getY() < WORLD_HEIGHT;
    }

    public Tile getTileAt(int x, int y)
    {
        return this.tiles[x + y * WORLD_WIDTH];
    }

    public Tile getTileAt(Coordinate coordinate)
    {
        return this.getTileAt(coordinate.getX(), coordinate.getY());
    }

    public Entity getEntityAt(Coordinate coordinate)
    {
        for (Entity e : getEntities())
        {
            if (e.getCoordinate().equals(coordinate)) return e;
        }

        return null;
    }
}