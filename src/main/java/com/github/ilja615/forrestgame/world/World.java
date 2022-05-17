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
    public final AirTile airTile;
    private final ArrayList<Particle> particles = new ArrayList<>();
    private final Game game;
    private final Entity player;
    private final ArrayList<Entity> entities = new ArrayList<>();
    private final TextRenderer textRenderer = new TextRenderer();
    private final UiRenderer uiRenderer = new UiRenderer();
    private final TextureRenderer textureRenderer;
    // private final List<Coordinate> path = new ArrayList<>();
    private final TimeTracker timeTracker;
    private final Shader shader;
    private final int ROOM_MIN_SIZE = 2;
    private final int ROOM_MAX_SIZE = 6;
    private final ArrayList<Room> rooms = new ArrayList<>();
    private final ArrayList<Coordinate> noObstacleZone = new ArrayList<>();
    public int WORLD_WIDTH;
    public int WORLD_HEIGHT;
    public Room finalRoom = null;
    private Tile[] tiles;
    private Coordinate startCoordinate;
    private int enemyTurnWait;
    private ArrayList<Entity> this_turn_entity_stack = new ArrayList<>();
    private int currentX = 1;
    private int currentY = 1;

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

    public boolean isWithinView(final Coordinate coordinate)
    {
        return coordinate.x() >= player.getCoordinate().x() - 5
                && coordinate.x() <= player.getCoordinate().x() + 5
                && coordinate.y() >= player.getCoordinate().y() - 4
                && coordinate.y() <= player.getCoordinate().y() + 3;
    }

    public ArrayList<Entity> getEntitiesWithinView()
    {
        return entities.stream().filter(entity -> isWithinView(entity.getCoordinate())).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Entity> getEntitiesWithinViewThatWillWalk()
    {
        return getEntitiesWithinView().stream().filter(Entity::willAutomaticallyMove).collect(Collectors.toCollection(ArrayList::new));
    }

    public void generate()
    {
        WORLD_HEIGHT = 40;
        WORLD_WIDTH = 40;

        this.tiles = new Tile[WORLD_WIDTH * WORLD_HEIGHT];
        currentX = 1;
        currentY = 1;
        rooms.clear();
        finalRoom = null;
        noObstacleZone.clear();

        if (!(WORLD_WIDTH >= 4 && WORLD_HEIGHT >= 4))
        {
            LOGGER.error("The board size is too small (it needs to be at least 4x4).");
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
            final Pair<ArrayList<Coordinate>, Direction> path = makePath(room);
            if (path.first().isEmpty() || path.second() == null)
            {
                onBoardFailureToCreate();
                return;
            }
            room = makeRoom(path.second());
            if (room == null)
            {
                onBoardFailureToCreate();
                return;
            }
            placedRooms++;
            rooms.add(room);
        }
        finalRoom = room;

        for (int b = 0; b < ThreadLocalRandom.current().nextInt(7) + 4; b++) // Get random rooms and make side-branches of them.
        {
            final Pair<ArrayList<Coordinate>, Direction> path = makePath(rooms.get(ThreadLocalRandom.current().nextInt(rooms.size())));
            if (!path.first().isEmpty() && path.second() != null)
            {
                room = makeRoom(path.second());
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
        final ShortPathfinder pathFinder = new ShortPathfinder();
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

            for (final Coordinate coordinate : path.subList(0, path.size() - 1))
            {
                if (isWithinWorld(coordinate))
                {
                    tiles[coordinate.x() + coordinate.y() * WORLD_WIDTH] = new DirtTile(Textures.AIR);

                    // Sometimes add an item
                    final int item = ThreadLocalRandom.current().nextInt(10);
                    if (item < Textures.PATH_DECORATION_ALTERNATIVES.length)
                    {
                        tiles[coordinate.x() + coordinate.y() * WORLD_WIDTH].setItem(new DecorationItem(Textures.PATH_DECORATION_ALTERNATIVES[item]));
                    }
                }
            }
        }

        // Will do other stuff at the very end
        postGeneration();
    }

    private Room makeRoom(final Direction direction)
    {
        final int random = ThreadLocalRandom.current().nextInt(4);
        if (random == 0) return makeMushroomRoom(direction);

        Room room = null;
        int roomTries = 0;
        boolean successfulRoom = false;

        while (!successfulRoom && roomTries < 20)
        {
            LOGGER.debug("Amount of room tries: {}.", roomTries);
            roomTries++;
            final int roomSizeX = ThreadLocalRandom.current().nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE) + ROOM_MIN_SIZE;
            final int roomSizeY = ThreadLocalRandom.current().nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE) + ROOM_MIN_SIZE;
            int roomStarterPosX = direction == Direction.LEFT ? currentX - roomSizeX : currentX; // Shifting room if necessary
            int roomStarterPosY = direction == Direction.DOWN ? currentY - roomSizeY : currentY; // Shifting room if necessary
            if (direction.isVertical())
                roomStarterPosX -= ThreadLocalRandom.current().nextInt(roomSizeX); // Extra shifting room, for more variation
            if (direction.isHorizontal())
                roomStarterPosY -= ThreadLocalRandom.current().nextInt(roomSizeY); // Extra shifting room, for more variation
            room = new Room(roomStarterPosX, roomStarterPosY, roomSizeX, roomSizeY);
            successfulRoom = true;

            for (final Coordinate coordinate : room)
            {
                if (!isWithinWorld(coordinate) || getTileAt(coordinate) instanceof FloorTile) // the room may not overlap another room
                {
                    successfulRoom = false;
                }
            }

            if (successfulRoom)
            {
                for (final Coordinate coord : room)
                {
                    final Texture texture = ThreadLocalRandom.current().nextBoolean()
                            ? Textures.GRASS_0
                            : Textures.GROUND_ALTERNATIVES[ThreadLocalRandom.current().nextInt(Textures.GROUND_ALTERNATIVES.length)];
                    setTileAt(coord, new FloorTile(texture));
                }
            }
        }

        if (!successfulRoom) room = null;

        return room;
    }

    private Room makeMushroomRoom(final Direction dir)
    {
        Room room = null;
        int roomTries = 0;
        boolean successfulRoom = false;

        while (!successfulRoom && roomTries < 20)
        {
            LOGGER.debug("Amount of room tries: {}.", roomTries);
            roomTries++;
            final int roomSizeX = ThreadLocalRandom.current().nextInt(3) + 2; // MushroomRoom is a bit smaller
            final int roomSizeY = ThreadLocalRandom.current().nextInt(3) + 2; // MushroomRoom is a bit smaller
            int roomStarterPosX = dir == Direction.LEFT ? currentX - roomSizeX : currentX; // Shifting room if necessary
            int roomStarterPosY = dir == Direction.DOWN ? currentY - roomSizeY : currentY; // Shifting room if necessary
            if (dir.isVertical())
                roomStarterPosX -= ThreadLocalRandom.current().nextInt(roomSizeX); // Extra shifting room, for more variation
            if (dir.isHorizontal())
                roomStarterPosY -= ThreadLocalRandom.current().nextInt(roomSizeY); // Extra shifting room, for more variation
            room = new Room(roomStarterPosX, roomStarterPosY, roomSizeX, roomSizeY);
            successfulRoom = true;

            for (final Coordinate coordinate : room)
            {
                if (!isWithinWorld(coordinate) || getTileAt(coordinate) instanceof FloorTile) // the room may not overlap another room
                {
                    successfulRoom = false;
                }
            }

            if (successfulRoom)
            {
                for (final Coordinate coordinate : room)
                {
                    final Texture texture = ThreadLocalRandom.current().nextBoolean()
                            ? Textures.GRASS_0
                            : Textures.GROUND_ALTERNATIVES[ThreadLocalRandom.current().nextInt(Textures.GROUND_ALTERNATIVES.length)];
                    setTileAt(coordinate, new FloorTile(texture));
                    getTileAt(coordinate).setItem(new MushroomItem(Textures.MUSHROOM[ThreadLocalRandom.current().nextInt(Textures.MUSHROOM.length)]));
                    noObstacleZone.add(coordinate);
                }
            }
        }

        if (!successfulRoom) room = null;

        return room;
    }

    private Pair<ArrayList<Coordinate>, Direction> makePath(final Room fromRoom)
    {
        final ArrayList<Coordinate> path = new ArrayList<>();
        int pathTries = 0;
        boolean successfulPath = false;
        Direction directionOfTheSuccessfulPath = null;

        while (!successfulPath && pathTries < 20)
        {
            pathTries++;
            path.clear();
            final Pair<Coordinate, Direction> continuation = fromRoom.randomOfWall();
            currentX = continuation.first().x();
            currentY = continuation.first().y();
            final int pathwayLength = ThreadLocalRandom.current().nextInt(4) + 3;
            successfulPath = true;
            for (int i = 1; i < pathwayLength; i++)
            {
                path.add(new Coordinate(currentX, currentY));
                currentX += continuation.second().getXMovement();
                currentY += continuation.second().getYMovement();

                if (!isWithinWorld(currentX, currentY) || getTileAt(currentX, currentY) instanceof FloorTile) // the room may not overlap another room
                {
                    successfulPath = false;
                }
            }
            if (successfulPath)
            {
                directionOfTheSuccessfulPath = continuation.second();

                for (final Coordinate c : path)
                {
                    final Texture texture = ThreadLocalRandom.current().nextBoolean()
                            ? Textures.GRASS_0
                            : Textures.GROUND_ALTERNATIVES[ThreadLocalRandom.current().nextInt(Textures.GROUND_ALTERNATIVES.length)];
                    setTileAt(c, new FloorTile(texture));
                    noObstacleZone.add(c);
                }
            }
        }

        return new Pair<>(path, directionOfTheSuccessfulPath);
    }

    public void placeRockClusters()
    {
        // Note this for loop is a bit smaller because it skips the very outer edge
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(5) + 2; i++)
        {
            final int startX = 1 + ThreadLocalRandom.current().nextInt(WORLD_WIDTH - 1);
            final int startY = 1 + ThreadLocalRandom.current().nextInt(WORLD_HEIGHT - 1);

            for (int x = startX; x < startX + ThreadLocalRandom.current().nextInt(3) + 2; x++)
            {
                for (int y = startY; y < startY + ThreadLocalRandom.current().nextInt(3) + 2; y++)
                {
                    final Coordinate c = new Coordinate(x, y);

                    if (isWithinWorld(c) && getTileAt(c) instanceof FloorTile)
                    {
                        if (noObstacleZone.contains(c))
                        {
                            tiles[x + (y * WORLD_WIDTH)] = new WallTile(Textures.WALL_SINGLE);
                        }
                    }
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
                final Coordinate c = new Coordinate(x, y);

                if (getTileAt(c) instanceof FloorTile)
                {
                    final int random = ThreadLocalRandom.current().nextInt(24);

                    if (noObstacleZone.contains(c))
                    {
                        if (random == 6 || random == 7)
                        {
                            tiles[x + (y * WORLD_WIDTH)].setItem(new MushroomItem(Textures.MUSHROOM[ThreadLocalRandom.current().nextInt(Textures.MUSHROOM.length)]));
                        }
                    } else
                    {
                        switch (random)
                        {
                            case 0, 1, 2, 3 -> tiles[x + (y * WORLD_WIDTH)].setItem(new BushItem());
                            case 4 -> tiles[x + (y * WORLD_WIDTH)] = new WallTile(Textures.WALL_SINGLE);
                            case 6, 7 ->
                                    tiles[x + (y * WORLD_WIDTH)].setItem(new MushroomItem(Textures.MUSHROOM[ThreadLocalRandom.current().nextInt(Textures.MUSHROOM.length)]));
                            case 8 -> tiles[x + (y * WORLD_WIDTH)].setItem(new TreeItem(Textures.TREE));
                        }
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
                    final int random = ThreadLocalRandom.current().nextInt(26 - 2 * (timeTracker.getCurrentTime() % 6));

                    if (random == 0) entities.add(new Scamperer(this, new Coordinate(x, y)));
                }
            }
        }
    }

    public Coordinate placeEndSign()
    {
        Coordinate coordinate = null;
        final ArrayList<Coordinate> potential = finalRoom.wallsExcludingCorners();

        for (int i = potential.size() - 1; i >= 1; i--) // Shuffle potential places with Fisher-Yates shuffle to randomize.
        {
            // swapping current index value and random index value
            Collections.swap(potential, i, ThreadLocalRandom.current().nextInt(i + 1));
        }

        for (final Coordinate c : potential)
        {
            if (isWithinWorld(c) && getTileAt(c) instanceof WallTile)
            {
                if (amountFloorNeighbour(c) == 1)
                {
                    coordinate = c;
                    setTileAt(coordinate, new FloorTile(Textures.GRASS_0));
                    getTileAt(coordinate).setItem(new SignItem(Textures.SIGN));
                    LOGGER.info("Placed end at: {}", coordinate);
                    break;
                }
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
                    LOGGER.info("Placed start at: {}", coordinate);
                    player.setCoordinate(coordinate);
                    break;
                }
            } else
            {
                LOGGER.error("Failed to create player start position.");
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
                final Coordinate mutableCoordinate = new Coordinate(x, y);

                if (isWithinWorld(mutableCoordinate))
                {
                    final Tile tile = getTileAt(x, y);
                    tile.postGenerationEvent(this, mutableCoordinate);

                    if (tile instanceof ConnectedTextureTile)
                    {
                        ((ConnectedTextureTile) getTileAt(x, y)).adaptQuadrantTexturesList(this, new Coordinate(x, y));
                    }
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
            textureRenderer.clearLayers();
            textureRenderer.LAYER_MIDDLE.put(player.getCoordinate(), player.getCurrentTexture());
            entities.forEach(entity -> entity.whichLayer(textureRenderer).put(entity.getCoordinate(), entity.getCurrentTexture()));
            textureRenderer.renderBoard(); // Tiles and items get added to the lists in here and the rendering gets called.

            // UI
            uiRenderer.renderViewport();
            uiRenderer.renderEnergy(player);
            uiRenderer.renderHealth(player);
            textRenderer.drawString(this.getTimeTracker().getCurrentDayString(), -0.96f, 0.93f, 0.5f);
            uiRenderer.renderTimeIcon(this.getTimeTracker().getPeriodFromTime(this.timeTracker.getCurrentTime()));
        } else
        {
            final String s = this.getTimeTracker().getCurrentTimeString();
            final float size = 20.0f / (s.length() + 2);
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
            enemyTurnWait--;
            if (enemyTurnWait == 0)
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
        this_turn_entity_stack = getEntitiesWithinViewThatWillWalk();

        if (this_turn_entity_stack.size() > 0)
        {
            enemyTurnWait = 4; // next one
        } else
        {
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

    public boolean isWithinWorld(final Coordinate coordinate)
    {
        final int i = coordinate.x() + coordinate.y() * WORLD_WIDTH;
        return i >= 0
                && i < tiles.length
                && coordinate.x() >= 0
                && coordinate.x() < WORLD_WIDTH
                && coordinate.y() >= 0
                && coordinate.y() < WORLD_HEIGHT;
    }

    public boolean isWithinWorld(final int x, final int y)
    {
        return isWithinWorld(new Coordinate(x, y));
    }

    public Tile getTileAt(final int x, final int y)
    {
        return this.tiles[x + y * WORLD_WIDTH];
    }

    public void setTileAt(final int x, final int y, final Tile tile)
    {
        if (isWithinWorld(x, y))
        {
            if (!(x == 0 || y == 0 || x == WORLD_WIDTH - 1 || y == WORLD_HEIGHT - 1) || tile instanceof WallTile) // Can not place tile at edge except wall.
            {
                this.tiles[x + y * WORLD_WIDTH] = tile;
            }
        }
    }

    public Tile getTileAt(final Coordinate coordinate)
    {
        return this.getTileAt(coordinate.x(), coordinate.y());
    }

    public void setTileAt(final Coordinate coordinate, final Tile tile)
    {
        this.setTileAt(coordinate.x(), coordinate.y(), tile);
    }

    public Entity getEntityAt(final Coordinate coordinate)
    {
        for (final Entity e : getEntities())
        {
            if (e.getCoordinate().equals(coordinate)) return e;
        }

        return null;
    }

    public int amountFloorNeighbour(final Coordinate coordinate)
    {
        int amount = 0;

        for (final Direction d : Direction.values())
        {
            if (isWithinWorld(coordinate.move(d)) && getTileAt(coordinate.move(d)) instanceof FloorTile) amount++;
        }

        return amount;
    }
}