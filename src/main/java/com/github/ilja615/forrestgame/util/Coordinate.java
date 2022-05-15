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

    public Coordinate up()
    {
        return up(1);
    }

    public Coordinate up(final int amount)
    {
        return new Coordinate(this.x, this.y + validateMovement(amount));
    }

    public Coordinate down()
    {
        return down(1);
    }

    public Coordinate down(final int amount)
    {
        return new Coordinate(this.x, this.y - validateMovement(amount));
    }

    public Coordinate left()
    {
        return left(1);
    }

    public Coordinate left(final int amount)
    {
        return new Coordinate(this.x - validateMovement(amount), this.y);
    }

    public Coordinate right()
    {
        return right(1);
    }

    public Coordinate right(final int amount)
    {
        return new Coordinate(this.x + validateMovement(amount), this.y);
    }

    public Coordinate relative(final int deltaX, final int deltaY)
    {
        return new Coordinate(this.x + deltaX, this.y + deltaY);
    }

    public Coordinate move(final Direction direction, final int amount)
    {
        return new Coordinate(this.x + direction.getX() * amount, this.y + direction.getY() * amount);
    }

    public Coordinate move(final Direction.Secondary secondaryDirection, final int amount)
    {
        return new Coordinate(this.x + secondaryDirection.getX() * amount, this.y + secondaryDirection.getY() * amount);
    }

    public Coordinate move(final Direction direction)
    {
        return move(direction, 1);
    }

    public Coordinate move(final Direction.Secondary secondaryDirection)
    {
        return move(secondaryDirection, 1);
    }

    @Override
    public boolean equals(final Object object)
    {
        if (this == object) return true;
        if (object == null || this.getClass() != object.getClass()) return false;
        final Coordinate other = (Coordinate) object;

        return this.x == other.x && this.y == other.y;
    }

    @Override
    public String toString()
    {
        return "(" + x + "," + y + ")";
    }
}
