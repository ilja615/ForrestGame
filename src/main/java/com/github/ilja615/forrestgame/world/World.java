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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;

public class World implements Tickable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(World.class);
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
    public AirTile airTile;
    private int currentX = 1; private int currentY = 1;
    private ArrayList<Room> rooms = new ArrayList<>();
    private final int ROOM_MIN_SIZE = 2;
    private final int ROOM_MAX_SIZE = 4;
    public Room finalRoom = null;

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
        WORLD_HEIGHT = 40;
        WORLD_WIDTH = 40;

        this.tiles = new Tile[WORLD_WIDTH * WORLD_HEIGHT];
        currentX = 1; currentY = 1;
        rooms.clear();
        finalRoom = null;

        if (!(WORLD_WIDTH >= 4 && WORLD_HEIGHT >= 4))
        {
            LOGGER.error("The board size is too small, it has to be minimal 4x4.");
            this.onBoardFailureToCreate();
        }

        // Fill the whole board with walls, after which rooms will get carved out.
        for (int x = 0; x < WORLD_WIDTH; x++)
        {
            for (int y = 0; y < WORLD_HEIGHT; y++)
            {
                tiles[x + (y * WORLD_WIDTH)] = new WallTile(Textures.WALL_SINGLE);
            }
        }

        Room room = makeRoom(Direction.RIGHT); // Make initial room
        if (room == null)
        {
            onBoardFailureToCreate();
            return;
        }
        int placedRooms = 1;

        while (placedRooms < ThreadLocalRandom.current().nextInt(5) + 2) // Make more rooms and paths
        {
            Pair<ArrayList<Coordinate>, Direction> path = makePath(room);
            if (path.getFirstThing().isEmpty() || path.getSecondThing() == null)
            {
                onBoardFailureToCreate();
                return;
            }
            room = makeRoom(path.getSecondThing());
            if (room == null)
            {
                onBoardFailureToCreate();
                return;
            }
            placedRooms++;
            rooms.add(room);
        }
        finalRoom = room;

        for (int b = 0; b < ThreadLocalRandom.current().nextInt(8) + 2; b++) // Get random rooms and make side-branches of them.
        {
            Pair<ArrayList<Coordinate>, Direction> path = makePath(rooms.get(ThreadLocalRandom.current().nextInt(rooms.size())));
            if (!path.getFirstThing().isEmpty() && path.getSecondThing() != null)
            {
                room = makeRoom(path.getSecondThing());
                if (room == null)
                {
                    onBoardFailureToCreate();
                    return;
                }
                placedRooms++;
                rooms.add(room);
            }
        }


        // Adds bush and wall obstacles scattered around the world
        placeRockClusters();
        placeSimpleObstacles();
        // Adds creatures
        this.entities.clear();
        placeEntities();
        // Adds start and end
        startCoordinate = placeStart();
        final Coordinate end = placeEndSign();

        // A final check if there exists a valid path , if not , the entire world must be re-created
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

    private Room makeRoom(Direction dir)
    {
        Room room = null;
        int roomTries = 0;
        boolean successfullRoom = false;
        while (!successfullRoom && roomTries < 20)
        {
            System.out.println(roomTries);
            roomTries++;
            int roomSizeX = ThreadLocalRandom.current().nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE) + ROOM_MIN_SIZE;
            int roomSizeY = ThreadLocalRandom.current().nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE) + ROOM_MIN_SIZE;
            int roomStarterPosX = dir == Direction.LEFT ? currentX - roomSizeX : currentX; // Shifting room if necessary
            int roomStarterPosY = dir == Direction.DOWN ? currentY - roomSizeY : currentY; // Shifting room if necessary
            if (dir.isVertical())
                roomStarterPosX -= ThreadLocalRandom.current().nextInt(roomSizeX); // Extra shifting room, for more variation
            if (dir.isHorizontal())
                roomStarterPosY -= ThreadLocalRandom.current().nextInt(roomSizeY); // Extra shifting room, for more variation
            room = new Room(roomStarterPosX, roomStarterPosY, roomSizeX, roomSizeY);
            successfullRoom = true;
            for (Coordinate coord : room)
            {
                if (!isWithinWorld(coord) || getTileAt(coord) instanceof FloorTile) // the room may not overlap another room
                {
                    successfullRoom = false;
                }
            }
            if (successfullRoom)
            {
                for (Coordinate coord : room)
                {
                    Texture texture = ThreadLocalRandom.current().nextBoolean() ? Textures.GRASS_0 : Textures.GROUND_ALTERNATIVES[ThreadLocalRandom.current().nextInt(Textures.GROUND_ALTERNATIVES.length)];
                    setTileAt(coord, new FloorTile(texture));
                }
            }
        }
        if (!successfullRoom)
            room = null;
        return room;
    }

    private Pair<ArrayList<Coordinate>,Direction> makePath(Room fromRoom)
    {
        ArrayList<Coordinate> path = new ArrayList<>();
        int pathTries = 0;
        boolean successfulPath = false;
        Direction directionOfTheSuccessfulPath = null;
        while (!successfulPath && pathTries < 20)
        {
            pathTries++;
            path.clear();
            Pair<Coordinate, Direction> continuation = fromRoom.randomOfWall();
            currentX = continuation.getFirstThing().getX();
            currentY = continuation.getFirstThing().getY();
            int pathwayLength = ThreadLocalRandom.current().nextInt(4) + 3;
            successfulPath = true;
            for (int i = 1; i < pathwayLength; i++)
            {
                path.add(new Coordinate(currentX, currentY));
                currentX += continuation.getSecondThing().getX();
                currentY += continuation.getSecondThing().getY();
                if (!isWithinWorld(currentX, currentY) || getTileAt(currentX, currentY) instanceof FloorTile) // the room may not overlap another room
                {
                    successfulPath = false;
                }
            }
            if (successfulPath)
            {
                directionOfTheSuccessfulPath = continuation.getSecondThing();
                for (Coordinate c : path)
                {
                    Texture texture = ThreadLocalRandom.current().nextBoolean() ? Textures.GRASS_0 : Textures.GROUND_ALTERNATIVES[ThreadLocalRandom.current().nextInt(Textures.GROUND_ALTERNATIVES.length)];
                    setTileAt(c, new FloorTile(texture));
                }
            }
        }
        return new Pair<>(path,directionOfTheSuccessfulPath);
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
                        tiles[x + (y * WORLD_WIDTH)] = new WallTile(Textures.WALL_SINGLE);
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
                        case 4 -> tiles[x + (y * WORLD_WIDTH)] = new WallTile(Textures.WALL_SINGLE);
                        case 6 -> tiles[x + (y * WORLD_WIDTH)].setItem(new MushroomItem(Textures.MUSHROOM[ThreadLocalRandom.current().nextInt(Textures.MUSHROOM.length)]));
                        case 7 -> tiles[x + (y * WORLD_WIDTH)].setItem(new TreeItem(Textures.TREE));
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
        Coordinate coordinate = null;
        ArrayList<Coordinate> potential = finalRoom.wallsExcludingCorners();

        for (int i = potential.size() - 1; i >= 1; i--) // Shuffle potential places with Fisher-Yates shuffle to randomize.
        {
            // swapping current index value and random index value
            Collections.swap(potential, i, ThreadLocalRandom.current().nextInt(i + 1));
        }

        for (Coordinate c : potential)
        {
            if (isWithinWorld(c) && getTileAt(c) instanceof WallTile)
                if (amountFloorNeighbour(c) == 1)
                {
                    coordinate = c;
                    setTileAt(coordinate, new FloorTile(Textures.GRASS_0));
                    getTileAt(coordinate).setItem(new SignItem(Textures.SIGN));
                    LOGGER.info("placed end at: {}", coordinate);
                    break;
                }
        }

        if (coordinate == null)
        {
            LOGGER.error("Failed to create end sign");
            this.onBoardFailureToCreate();
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
                    Tile tile = getTileAt(x, y);
                    tile.postGenerationEvent(this, mutableCoordinate);
                    if (tile instanceof ConnectedTextureTile)
                        ((ConnectedTextureTile) getTileAt(x, y)).adaptQuadrantTexturesList(this, new Coordinate(x, y));
                }
            }
        }

        player.setMobile(true);
    }

    public void frame()
    {
        glUniform1f(glGetUniformLocation(this.shader.program, "redComponent"), this.timeTracker.getRedComponent());
        glUniform1f(glGetUniformLocation(this.shader.program, "greenComponent"), this.timeTracker.getGreenComponent());
        glUniform1f(glGetUniformLocation(this.shader.program, "blueComponent"), this.timeTracker.getBlueComponent());

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

    @Override
    public void tick()
    {
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
                nextEnemyTurn(); // If there were any enemies and they waited enough, it is now their turn.
        }

        if (KeyInput.isKeyDown(game, GLFW.GLFW_KEY_R))
        {
            player.setCoordinate(startCoordinate);
        }
    }

    public void onEnemyTurn()
    {
        player.setMobile(false);
        this_turn_entity_stack = getEntitiesWithinView();

        if (this_turn_entity_stack.size() > 0)
        {
            enemyTurnWait = 4; // next one
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
                enemyTurnWait = 2; // next one
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

    public boolean isWithinWorld(int x, int y)
    {
        return isWithinWorld(new Coordinate(x, y));
    }

    public Tile getTileAt(int x, int y)
    {
        return this.tiles[x + y * WORLD_WIDTH];
    }

    public void setTileAt(int x, int y, Tile tile)
    {
        if (isWithinWorld(x,y))
            if (!(x == 0 || y == 0 || x == WORLD_WIDTH - 1 || y == WORLD_HEIGHT - 1) || tile instanceof WallTile) // Can not place tile at edge except wall.
                this.tiles[x + y * WORLD_WIDTH] = tile;
    }

    public Tile getTileAt(Coordinate coordinate)
    {
        return this.getTileAt(coordinate.getX(), coordinate.getY());
    }

    public void setTileAt(Coordinate coordinate, Tile tile)
    {
        this.setTileAt(coordinate.getX(), coordinate.getY(), tile);
    }

    public Entity getEntityAt(Coordinate coordinate)
    {
        for (Entity e : getEntities())
        {
            if (e.getCoordinate().equals(coordinate)) return e;
        }

        return null;
    }

    public int amountFloorNeighbour(Coordinate coordinate)
    {
        int amount = 0;
        for (Direction d : Direction.values())
        {
            if (isWithinWorld(coordinate.move(d)) && getTileAt(coordinate.move(d)) instanceof FloorTile)
                amount++;
        }
        return amount;
    }
}