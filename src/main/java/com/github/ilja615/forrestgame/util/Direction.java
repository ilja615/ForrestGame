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
    UP(0, 1), DOWN(0, -1),
    LEFT(-1, 0), RIGHT(1, 0);

    private final int xMovement;
    private final int yMovement;

    Direction(final int xMovement, final int yMovement)
    {
        this.xMovement = xMovement;
        this.yMovement = yMovement;
    }

    public int getXMovement()
    {
        return xMovement;
    }

    public int getYMovement()
    {
        return yMovement;
    }

    public boolean isVertical()
    {
        return yMovement != 0;
    }

    public boolean isHorizontal()
    {
        return xMovement != 0;
    }

    public enum Diagonal
    {
        UP_AND_LEFT(UP, LEFT), UP_AND_RIGHT(UP, RIGHT),
        DOWN_AND_LEFT(DOWN, LEFT), DOWN_AND_RIGHT(DOWN, RIGHT);

        private final Direction verticalDirection;
        private final Direction horizontalDirection;

        Diagonal(final Direction verticalDirection, final Direction horizontalDirection)
        {
            this.verticalDirection = verticalDirection;
            this.horizontalDirection = horizontalDirection;
        }

        public Direction getVerticalDirection()
        {
            return verticalDirection;
        }

        public Direction getHorizontalDirection()
        {
            return horizontalDirection;
        }
    }
}