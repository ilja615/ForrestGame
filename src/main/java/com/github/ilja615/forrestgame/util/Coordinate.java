/*
 * Copyright (c) 2021 ilja615.
 *
 * This file is part of Forrest Game.
 *
 * Forrest Game is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Forrest Game is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Forrest Game.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.ilja615.forrestgame.util;

import java.util.Objects;

/**
 * A pair of a x coordinate and a y coordinate.
 * Contains utilities to make movement easier.
 */
public class Coordinate
{
    private final int x;
    private final int y;

    public Coordinate(final int x, final int y)
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

    public Coordinate up()
    {
        return up(1);
    }

    public Coordinate up(final int amount)
    {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than 0!");

        return new Coordinate(this.x, this.y + amount);
    }

    public Coordinate down()
    {
        return down(1);
    }

    public Coordinate down(final int amount)
    {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than 0!");

        return new Coordinate(this.x, this.y - amount);
    }

    public Coordinate left()
    {
        return left(1);
    }

    public Coordinate left(final int amount)
    {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than 0!");

        return new Coordinate(this.x - amount, this.y);
    }

    public Coordinate right()
    {
        return right(1);
    }

    public Coordinate right(final int amount)
    {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than 0!");

        return new Coordinate(this.x + amount, this.y);
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
    public int hashCode()
    {
        return Objects.hash(x, y);
    }

    @Override
    public String toString()
    {
        return x + "," + y;
    }
}
