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

import java.util.Objects;

/**
 * A (x, y) coordinate pair.
 * Contains utilities to make movement easier.
 */
public record Coordinate(int x, int y)
{
    public Coordinate left(final int amount)
    {
        if (amount <= 0) throw new IllegalArgumentException("The amount must be greater than 0!");

        return this.add(-amount, 0);
    }

    public Coordinate right(final int amount)
    {
        if (amount <= 0) throw new IllegalArgumentException("The amount must be greater than 0!");

        return this.add(+amount, 0);
    }

    public Coordinate down(final int amount)
    {
        if (amount <= 0) throw new IllegalArgumentException("The amount must be greater than 0!");

        return this.add(0, -amount);
    }

    public Coordinate up(final int amount)
    {
        if (amount <= 0) throw new IllegalArgumentException("The amount must be greater than 0!");

        return this.add(0, +amount);
    }

    public Coordinate add(final int xAmount, final int yAmount)
    {
        return new Coordinate(this.x + xAmount, this.y + yAmount);
    }

    public Coordinate move(final Direction direction, final int amount)
    {
        return switch (direction)
                {
                    case LEFT -> this.left(amount);
                    case RIGHT -> this.right(amount);
                    case DOWN -> this.down(amount);
                    case UP -> this.up(amount);
                };
    }

    public Coordinate move(final Direction.Secondary secondaryDirection, final int amount)
    {
        return switch (secondaryDirection)
                {
                    case BOTTOM_LEFT -> this.left(amount).down(amount);
                    case BOTTOM_RIGHT -> this.right(amount).down(amount);
                    case TOP_LEFT -> this.left(amount).up(amount);
                    case TOP_RIGHT -> this.right(amount).up(amount);
                };
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
        return "(" + x + ", " + y + ")";
    }
}
