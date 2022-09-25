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

/**
 * A (x, y) coordinate pair.
 * Contains utilities to make movement easier.
 */
public record Coordinate(int x, int y)
{
    private int validateMovement(final int amount)
    {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than 0!");
        else return amount;
    }

    public Coordinate relativeMove(final int xAmount, final int yAmount)
    {
        return new Coordinate(this.x + xAmount, this.y + yAmount);
    }

    public Coordinate up()
    {
        return up(1);
    }

    public Coordinate up(final int amount)
    {
        return relativeMove(0, +validateMovement(amount));
    }

    public Coordinate down()
    {
        return down(1);
    }

    public Coordinate down(final int amount)
    {
        return relativeMove(0, -validateMovement(amount));
    }

    public Coordinate left()
    {
        return left(1);
    }

    public Coordinate left(final int amount)
    {
        return relativeMove(-validateMovement(amount), 0);
    }

    public Coordinate right()
    {
        return right(1);
    }

    public Coordinate right(final int amount)
    {
        return relativeMove(+validateMovement(amount), 0);
    }

    public Coordinate transpose(final Direction direction)
    {
        return transpose(direction, 1);
    }

    public Coordinate transpose(final Direction direction, final int amount)
    {
        return switch (direction)
                {
                    case UP -> up(amount);
                    case DOWN -> down(amount);
                    case LEFT -> left(amount);
                    case RIGHT -> right(amount);
                };
    }

    public Coordinate transpose(final Direction.Diagonal diagonalDirection)
    {
        return transpose(diagonalDirection, 1);
    }

    public Coordinate transpose(final Direction.Diagonal diagonalDirection, final int amount)
    {
        return switch (diagonalDirection)
                {
                    case UP_AND_LEFT -> up(amount).left(amount);
                    case UP_AND_RIGHT -> up(amount).right(amount);
                    case DOWN_AND_LEFT -> down(amount).left(amount);
                    case DOWN_AND_RIGHT -> down(amount).right(amount);
                };
    }

    @Override
    public boolean equals(final Object object)
    {
        if (this == object) return true;
        if (object == null || this.getClass() != object.getClass()) return false;
        final Coordinate otherCoordinate = (Coordinate) object;

        return this.x == otherCoordinate.x && this.y == otherCoordinate.y;
    }

    @Override
    public String toString()
    {
        return "(" + x + "," + y + ")";
    }
}
