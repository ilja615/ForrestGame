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

package com.github.ilja615.forrestgame.utility;

public enum Direction
{
    LEFT, RIGHT, DOWN, UP;

    public enum Secondary
    {
        BOTTOM_LEFT(LEFT, UP),
        BOTTOM_RIGHT(RIGHT, UP),
        TOP_LEFT(LEFT, UP),
        TOP_RIGHT(RIGHT, UP);

        private final Direction horizontal;
        private final Direction vertical;

        Secondary(Direction horizontal, Direction vertical)
        {
            this.horizontal = horizontal;
            this.vertical = vertical;
        }

        public Direction getVertical()
        {
            return vertical;
        }

        public Direction getHorizontal()
        {
            return horizontal;
        }
    }
}