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

package com.github.ilja615.forrestgame.util;

public enum Direction
{
    UP(0,1), DOWN(0, -1),
    LEFT(-1, 0), RIGHT(1, 0);

    private final int x;
    private final int y;

    Direction(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public enum Secondary
    {
        TOP_RIGHT(UP, RIGHT),
        TOP_LEFT(UP, LEFT),
        BOTTOM_RIGHT(DOWN, RIGHT),
        BOTTOM_LEFT(DOWN, LEFT);

        private final Direction vertical;
        private final Direction horizontal;
        private final int x;
        private final int y;

        Secondary(Direction vertical, Direction horizontal)
        {
            this.vertical = vertical;
            this.horizontal = horizontal;
            this.y = this.vertical.y;
            this.x = this.horizontal.x;
        }

        public Direction getVertical()
        {
            return vertical;
        }

        public Direction getHorizontal()
        {
            return horizontal;
        }

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }
    }
}