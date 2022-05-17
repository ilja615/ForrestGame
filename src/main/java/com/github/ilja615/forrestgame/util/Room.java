/*
 * Copyright (c) 2022 the ForrestGame contributors.
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

package com.github.ilja615.forrestgame.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Room extends ArrayList<Coordinate>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Room.class);
    private final int startX;
    private final int startY;
    private final int width;
    private final int height;

    public Room(final int startX, final int startY, final int width, final int height)
    {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        for (int i = 0; i <= width; i++)
        {
            for (int j = 0; j <= height; j++)
            {
                this.add(new Coordinate(startX + i, startY + j));
            }
        }
    }

    public Pair<Coordinate, Direction> randomOfWall()
    {
        final Coordinate coordinate = wallsExcludingCorners().get(ThreadLocalRandom.current().nextInt(wallsExcludingCorners().size()));
        Direction direction = null; // default but shouldn't be needed

        if (coordinate.x() == startX - 1) direction = Direction.LEFT;
        if (coordinate.x() == startX + width + 1) direction = Direction.RIGHT;
        if (coordinate.y() == startY - 1) direction = Direction.DOWN;
        if (coordinate.y() == startY + height + 1) direction = Direction.UP;
        if (direction == null)
        {
            LOGGER.debug("The direction was null.");
            direction = Direction.RIGHT;
        }

        return new Pair<>(coordinate, direction);
    }

    public ArrayList<Coordinate> walls()
    {
        return new Room(this.startX - 1, this.startY - 1, this.width + 2, this.height + 2)
                .stream()
                .filter(c -> c.x() == startX - 1 || c.y() == startY - 1 || c.x() == startX + width + 1 || c.y() == startY + height + 1)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Coordinate> wallsExcludingCorners()
    {
        return this.walls()
                .stream()
                .filter(c -> !((c.x() == startX - 1 || c.x() == startX + width + 1) && (c.y() == startY - 1 || c.y() == startY + height + 1)))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
