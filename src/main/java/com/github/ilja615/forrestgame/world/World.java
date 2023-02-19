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
import com.github.ilja615.forrestgame.entity.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
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
    private final int ROOM_MIN_SIZE = 2; // excludes wall
    private final int ROOM_MAX_SIZE = 6; // excludes wall
    private final ArrayList<Room> rooms = new ArrayList<>();
    private final ArrayList<Coordinate> noObstacleZone = new ArrayList<>();
    public int WORLD_WIDTH;
    public int WORLD_HEIGHT;
    public Room finalRoom = null;
    private Tile[] tiles;
    private Coordinate startCoordinate;
    private Coordinate endCoordinate;
    private int entityTurnWait;
    private ArrayList<Entity> this_turn_entity_stack = new ArrayList<>();
    private int currentX = 1;
    private int currentY = 1;
    private boolean entityTurnAlmostFinished = false;
    private float fade = 1.0f;

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
                tiles[x + (y * WORLD_WIDTH)] = new DarkTreeTile(Textures.AIR);
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
            // TODO : rename to corridor to avoid confusing with the dirt path or with pathfinding path
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
            }
        }

        // Adds bush and wall obstacles scattered around the world
        placeWater();
        placeRockClusters();
        placeSimpleObstacles();
        // Adds creatures
        this.entities.clear();
        placeEntities();
        // Adds start and end
        startCoordinate = placeStart();
        endCoordinate = placeEndSign();

        // A final check if there exists a valid path , if not , the entire world must be re-created
        final ShortPathfinder pathFinder = new ShortPathfinder();
        final List<Coordinate> path = pathFinder.findPath(this, startCoordinate, endCoordinate, player);

        if (path.isEmpty())
        {
            LOGGER.error("No valid path was found!");
            this.onBoardFailureToCreate();
        } else
        {
            LOGGER.info("Found path {}", path.stream()
                    .map(Coordinate::toString)
                    .collect(Collectors.joining(" -> ")));

            // Make the path with dirt tiles
            if (timeTracker.getCurrentTime() <= 4)
            {
                int endOfPath = timeTracker.getCurrentTime() == 4 ? (path.size()/2) : path.size() - 1;
                for (final Coordinate coordinate : path.subList(0, endOfPath))
                {
                    if (isWithinWorld(coordinate) && (timeTracker.getCurrentTime() <= 2 || ThreadLocalRandom.current().nextInt(2) == 0))
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
        }

        // TODO : make it so that generating any inaccessiblePlaces is omitted when generating any obstacle
        ArrayList<Coordinate> inaccessiblePlaces = findInaccessiblePlaces();
        System.out.println("inaccessiblePlaces: "+inaccessiblePlaces);

        // Will do other stuff at the very end
        postGeneration();
    }

    private Room makeRoom(final Direction direction)
    {
        final int random = ThreadLocalRandom.current().nextInt(12);
        switch (random) {
            case 0: return makeMushroomRoom(direction);
            case 1, 2, 3: return makeWaterRoom(direction);
        }
        Room room = null;
        int roomTries = 0;
        boolean successfulRoom = false;

        while (!successfulRoom && roomTries < 20)
        {
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
                    if (ThreadLocalRandom.current().nextBoolean())
                        getTileAt(coordinate).setItem(new MushroomEdibleItem(Textures.MUSHROOM_EDIBLE[ThreadLocalRandom.current().nextInt(Textures.MUSHROOM_EDIBLE.length)]));
                    else
                        getTileAt(coordinate).setItem(new ConfushroomItem(Textures.MUSHROOM_CONFUSING[ThreadLocalRandom.current().nextInt(Textures.MUSHROOM_CONFUSING.length)]));
                    noObstacleZone.add(coordinate);
                }
            }
        }

        if (!successfulRoom) room = null;

        return room;
    }

    private Room makeWaterRoom(final Direction dir)
    {
        Room room = null;
        int roomTries = 0;
        boolean successfulRoom = false;

        while (!successfulRoom && roomTries < 20)
        {
            roomTries++;
            final int roomSizeX = ThreadLocalRandom.current().nextInt(3) + 2;
            final int roomSizeY = ThreadLocalRandom.current().nextInt(3) + 2;
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
                    if ((coordinate.x() == roomStarterPosX || coordinate.x() == roomStarterPosX + roomSizeX) && (coordinate.y() == roomStarterPosY || coordinate.y() == roomStarterPosY + roomSizeY))
                    {
                        final Texture texture = ThreadLocalRandom.current().nextBoolean()
                                ? Textures.GRASS_0
                                : Textures.GROUND_ALTERNATIVES[ThreadLocalRandom.current().nextInt(Textures.GROUND_ALTERNATIVES.length)];
                        setTileAt(coordinate, new FloorTile(texture));
                    }
                    else setTileAt(coordinate, new WaterTile(Textures.AIR));
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
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(5) + 10; i++)
        {
            // Note this for loop is a bit smaller because it skips the very outer edge
            final int startX = 1 + ThreadLocalRandom.current().nextInt(WORLD_WIDTH - 1);
            final int startY = 1 + ThreadLocalRandom.current().nextInt(WORLD_HEIGHT - 1);

            for (int x = startX; x < startX + ThreadLocalRandom.current().nextInt(3) + 2; x++)
            {
                for (int y = startY; y < startY + ThreadLocalRandom.current().nextInt(3) + 2; y++)
                {
                    final Coordinate c = new Coordinate(x, y);

                    if (isWithinWorld(c) && getTileAt(c) instanceof FloorTile)
                    {
                        if (!noObstacleZone.contains(c))
                        {
                            tiles[x + (y * WORLD_WIDTH)] = new WallTile(Textures.WALL_SINGLE);
                        }
                    }
                }
            }
        }
    }

    public void placeWater()
    {
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(5) + 10; i++)
        {
            // Note this for loop is a bit smaller because it skips the very outer edge
            final int startX = 1 + ThreadLocalRandom.current().nextInt(WORLD_WIDTH - 1);
            final int startY = 1 + ThreadLocalRandom.current().nextInt(WORLD_HEIGHT - 1);

            for (int x = startX; x < startX + ThreadLocalRandom.current().nextInt(3) + 2; x++)
            {
                for (int y = startY; y < startY + ThreadLocalRandom.current().nextInt(3) + 2; y++)
                {
                    final Coordinate c = new Coordinate(x, y);

                    if (isWithinWorld(c) && getTileAt(c) instanceof FloorTile)
                    {
                        if (!noObstacleZone.contains(c))
                        {
                            tiles[x + (y * WORLD_WIDTH)] = new WaterTile(Textures.AIR);
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
                        switch (random)
                        {
                            case 6, 7 -> tiles[x + (y * WORLD_WIDTH)].setItem(new MushroomEdibleItem(Textures.MUSHROOM_EDIBLE[ThreadLocalRandom.current().nextInt(Textures.MUSHROOM_EDIBLE.length)]));
                            case 8 -> tiles[x + (y * WORLD_WIDTH)].setItem(new ConfushroomItem(Textures.MUSHROOM_CONFUSING[ThreadLocalRandom.current().nextInt(Textures.MUSHROOM_CONFUSING.length)]));
                        }
                    } else
                    {
                        switch (random)
                        {
                            case 0, 1, 2, 3 -> tiles[x + (y * WORLD_WIDTH)].setItem(new BushItem());
                            case 4 -> tiles[x + (y * WORLD_WIDTH)] = new WallTile(Textures.WALL_SINGLE);
                            case 6, 7 -> tiles[x + (y * WORLD_WIDTH)].setItem(new MushroomEdibleItem(Textures.MUSHROOM_EDIBLE[ThreadLocalRandom.current().nextInt(Textures.MUSHROOM_EDIBLE.length)]));
                            case 8 -> tiles[x + (y * WORLD_WIDTH)].setItem(new TreeItem(Textures.TREE));
                            case 9 -> tiles[x + (y * WORLD_WIDTH)].setItem(new BerryBushItem());
                            case 10 -> tiles[x + (y * WORLD_WIDTH)].setItem(new ConfushroomItem(Textures.MUSHROOM_CONFUSING[ThreadLocalRandom.current().nextInt(Textures.MUSHROOM_CONFUSING.length)]));
                        }
                    }
                }
                if (getTileAt(c) instanceof WaterTile)
                {
                    if (getTileAt(c.up()) instanceof WaterTile && getTileAt(c.down()) instanceof WaterTile && getTileAt(c.left()) instanceof WaterTile && getTileAt(c.right()) instanceof WaterTile)
                    {
                        final int random = ThreadLocalRandom.current().nextInt(14);
                        switch (random)
                        {
                            case 0, 1, 2 -> tiles[x + (y * WORLD_WIDTH)].setItem(new LilyPadItem(Textures.LILY_PAD[0]));
                            case 3 -> tiles[x + (y * WORLD_WIDTH)].setItem(new LilyPadItem(Textures.LILY_PAD[1]));
                            case 4 -> tiles[x + (y * WORLD_WIDTH)].setItem(new LilyPadItem(Textures.LILY_PAD[2]));
                            case 5, 6, 7 -> tiles[x + (y * WORLD_WIDTH)].setItem(new ReedsItem(Textures.REEDS));
                        }
                        if (random <= 7)
                        {
                            final Tile tile = tiles[x + (y * WORLD_WIDTH)];
                            ((WaterTile) tiles[x + (y * WORLD_WIDTH)]).QUADRANT_TEXTURES.forEach((diagonal, texture) ->
                            {
                                if (Arrays.stream(Textures.WATER_FULL_PIECE).toList().contains(texture))
                                {
                                    ((WaterTile) tile).QUADRANT_TEXTURES.remove(diagonal);
                                    ((WaterTile) tile).QUADRANT_TEXTURES.put(diagonal, Textures.WATER_FULL_PIECE_NORMAL);
                                }
                            });
                        }
                    } else {
                        final int random = ThreadLocalRandom.current().nextInt(3);
                        if (random == 0)
                        {
                            final Tile tile = tiles[x + (y * WORLD_WIDTH)];
                            tile.setItem(new ReedsItem(Textures.REEDS));
                            ((WaterTile) tiles[x + (y * WORLD_WIDTH)]).QUADRANT_TEXTURES.forEach((diagonal, texture) ->
                            {
                                if (Arrays.stream(Textures.WATER_FULL_PIECE).toList().contains(texture))
                                {
                                    ((WaterTile) tile).QUADRANT_TEXTURES.remove(diagonal);
                                    ((WaterTile) tile).QUADRANT_TEXTURES.put(diagonal, Textures.WATER_FULL_PIECE_NORMAL);
                                }
                            });
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
                    if (timeTracker.getPeriodFromTime(timeTracker.getCurrentTime()).getIsDaytime())
                    {
                        final int random = ThreadLocalRandom.current().nextInt(24);
                        if (random == 0) entities.add(new Tangeling(this, new Coordinate(x, y)));
//                    if (random == 1) entities.add(new Skeleton(this, new Coordinate(x, y)));
//                    if (random == 2) entities.add(new Ghost(this, new Coordinate(x, y)));
                    }
                }
            }
        }
    }

    // TODO : sometimes multiple end signs are placed...

    public Coordinate placeEndSign()
    {
        final ArrayList<Coordinate> potential = finalRoom.wallsExcludingCorners();

        for (int i = potential.size() - 1; i >= 1; i--) // Shuffle potential places with Fisher-Yates shuffle to randomize.
        {
            // swapping current index value and random index value
            Collections.swap(potential, i, ThreadLocalRandom.current().nextInt(i + 1));
        }

        for (final Coordinate c : potential)
        {
            if (isWithinWorld(c) && getTileAt(c) instanceof DarkTreeTile)
            {
                if (amountFloorNeighbour(c).size() == 1)
                {
                    endCoordinate = c;
                    LOGGER.info("Placed end at: {}", endCoordinate);
                    break;
                }
            }
        }

        // TODO : make that it checks for if its within world
        if (endCoordinate == null)
        {
            LOGGER.error("Failed to create end sign");
            this.onBoardFailureToCreate();
        } else {
            setTileAt(endCoordinate, new FloorTile(Textures.GRASS_0));
            getTileAt(endCoordinate).setItem(new SignItem
                    (
                            // Applies the texture with the arrow pointing opposite to direction of the floor-neighbour
                            switch (amountFloorNeighbour(endCoordinate).get(0))
                            {
                                case RIGHT -> Textures.SIGN_LEFT;
                                case LEFT -> Textures.SIGN_RIGHT;
                                case DOWN -> Textures.SIGN_UP;
                                case UP -> Textures.SIGN_DOWN;
                            }
                    ));
        }
        return endCoordinate;
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

        glUniform1f(glGetUniformLocation(this.shader.program, "fade"), 1);

        if (!textureRenderer.isEnabled())
        {
            final String s = this.getTimeTracker().getCurrentTimeString();
            final float size = 20.0f / (s.length() + 2);
            textRenderer.drawString(s, -1f, -0.05f * size, size);
        }

        glUniform1f(glGetUniformLocation(this.shader.program, "fade"), fade);

        if (textureRenderer.isEnabled() || fade > 0.0f)
        {
            // Board
            textureRenderer.clearLayers();
            textureRenderer.renderBoard(); // Tiles and items get added to the lists in here and the rendering gets called.

            // UI
            uiRenderer.renderViewport();
            uiRenderer.renderEnergy(player);
            uiRenderer.renderHealth(player);
            uiRenderer.renderEffects(player);
            textRenderer.drawString(this.getTimeTracker().getCurrentDayString(), -0.96f, 0.93f, 0.5f);
            uiRenderer.renderTimeIcon(this.getTimeTracker().getPeriodFromTime(this.timeTracker.getCurrentTime()));
        }
    }

    @Override
    public void tick()
    {
        // Tick all objects if the game is not frozen
        if (timeTracker.waitTicks == 0)
        {
            player.tick();
            entities.forEach(Entity::tick);
        }

        // Handle particle updates
        particles.forEach(Particle::tick);
        particles.removeIf(Particle::isExpired);

        if (timeTracker.waitTicks >= 15) fade = 1.0f - ((20 - timeTracker.waitTicks) / 5.0f);
        if (timeTracker.waitTicks == 10) this.generate();
        if (timeTracker.waitTicks <= 5) fade = 1.0f - (timeTracker.waitTicks / 5.0f);
        if (timeTracker.waitTicks > 0) timeTracker.waitTicks--;
        if (timeTracker.waitTicks == 0) textureRenderer.setEnabled();

        if (entityTurnWait > 0)
        {
            entityTurnWait--;
            if (entityTurnWait == 0)
                nextEntityTurn(); // If there were any enemies and they waited enough, it is now their turn.
        }

        if (entityTurnAlmostFinished)
        {
            boolean ready = true;
            for (Entity e : entities)
            {
                if (e.getScheduledCoordinate() != null)
                    if (!e.getScheduledCoordinate().equals(e.getCoordinate())) ready = false;
            }
            // Checked if there are any enemies whose coordinate is not yet equal to their scheduled coordinate.
            if (ready)
            {
                // The enemy/entity their turn is now over!
                entityTurnAlmostFinished = false;
                onPlayerTurn();
            }
        }
    }

    public void onEntityTurn()
    {
        entityTurnAlmostFinished = false;
        player.setMobile(false);
        this_turn_entity_stack = getEntitiesWithinViewThatWillWalk();

        if (this_turn_entity_stack.size() > 0)
        {
            entityTurnWait = 4; // next one
        } else
        {
            onPlayerTurn();
            // There were no enemies
        }
    }

    private void nextEntityTurn()
    {
        if (this_turn_entity_stack.size() > 0)
        {
            this_turn_entity_stack.get(0).automaticallyMove();
            this_turn_entity_stack.remove(0);
            entityTurnWait = 2; // next one
        } else {
            entityTurnAlmostFinished = true;
            // No enemies left
        }
    }

    public void onPlayerTurn()
    {
        player.setMobile(true);
        player.getEffectTracker().decrementAll();
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

    public ArrayList<Direction> amountFloorNeighbour(final Coordinate coordinate)
    {
        ArrayList<Direction> output = new ArrayList<>();

        for (final Direction d : Direction.values())
        {
            if (isWithinWorld(coordinate.transpose(d)) && getTileAt(coordinate.transpose(d)) instanceof FloorTile)
                output.add(d);
        }

        return output;
    }

    public boolean checkEntitySchedules(Coordinate coord) {
        boolean flag = true;
        for (Entity e : entities)
        {
            if (e.getScheduledCoordinate() != null)
                if (e.getScheduledCoordinate().equals(coord)) flag = false;
        }
//        if (player.getScheduledCoordinate() != null)
//            if (player.getScheduledCoordinate().equals(player.getCoordinate())) flag = false;
        return flag;
    }

    private ArrayList<Coordinate> findInaccessiblePlaces()
    {
        List<Coordinate> tilesLeftToCheck = new ArrayList<>();
        for (int x = 0; x < WORLD_WIDTH; x++)
            for (int y = 0; y < WORLD_HEIGHT; y++)
                tilesLeftToCheck.add(new Coordinate(x, y));

        ArrayList<Coordinate> inaccessiblePlaces = new ArrayList<>();

        // Try for every tile on the field.
        while (tilesLeftToCheck.size() != 0)
        {
            Coordinate nextCoord = tilesLeftToCheck.get(0);
            if (getTileAt(nextCoord).isObstacle(player) || nextCoord.equals(startCoordinate))
            {
                // Obstacles should not be considered as "inaccessible places", because it is intended to not be able to venture onto those tiles.
                // The start coordinate should also be left out of the consideration as the path to it would always have length of 0.
                tilesLeftToCheck.remove(nextCoord);
            } else {
                final ShortPathfinder pathFinder = new ShortPathfinder();
                final List<Coordinate> path = pathFinder.findPath(this, startCoordinate, nextCoord, player);

                if (path.isEmpty())
                {
                    // If no path to a certain tile is found, this tile is tagged as inaccessible.
                    tilesLeftToCheck.remove(nextCoord);
                    inaccessiblePlaces.add(nextCoord);
                } else {
                    // The tile was accessible.
                    tilesLeftToCheck.removeIf(path::contains);
                }
            }
        }
        return inaccessiblePlaces;
    }
}