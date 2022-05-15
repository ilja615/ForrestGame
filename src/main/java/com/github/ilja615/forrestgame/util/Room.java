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

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Room extends ArrayList<Coordinate>
{
    private final int startX;
    private final int startY;
    private final int width;
    private final int height;

    public Room(int startX, int startY, int width, int height)
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
        Coordinate c = wallsExcludingCorners().get(ThreadLocalRandom.current().nextInt(wallsExcludingCorners().size()));
        Direction d = null; // default but shouldn't be needed
        if (c.x() == startX - 1) d = Direction.LEFT;
        if (c.x() == startX + width + 1) d = Direction.RIGHT;
        if (c.y() == startY - 1) d = Direction.DOWN;
        if (c.y() == startY + height + 1) d = Direction.UP;
        if (d == null)
        {
            System.out.println("d is null error");
            d = Direction.RIGHT;
        }
        return new Pair<>(c, d);
    }

    public ArrayList<Coordinate> walls()
    {
        return new Room(this.startX - 1, this.startY - 1, this.width + 2, this.height + 2).stream().filter(c -> c.x() == startX - 1 || c.y() == startY - 1 || c.x() == startX + width + 1 || c.y() == startY + height + 1).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Coordinate> wallsExcludingCorners()
    {
        return this.walls().stream().filter(c -> !(
                (c.x() == startX - 1 || c.x() == startX + width + 1) && (c.y() == startY - 1 || c.y() == startY + height + 1)
        )).collect(Collectors.toCollection(ArrayList::new));
    }
}
