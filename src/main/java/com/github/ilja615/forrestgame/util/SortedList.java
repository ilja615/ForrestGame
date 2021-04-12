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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link ArrayList<T>} which sorts itself every addition.
 *
 * @param <T> the type of the object to be stored
 */
public class SortedList<T extends Comparable<T>> extends AbstractList<T>
{
    private final List<T> delegate = new ArrayList<>();

    @Override
    public boolean add(final T t)
    {
        delegate.add(t);

        Collections.sort(delegate);

        return true;
    }

    @Override
    public T get(final int index)
    {
        return delegate.get(index);
    }

    @Override
    public T remove(final int index)
    {
        return delegate.remove(index);
    }

    @Override
    public void clear()
    {
        delegate.clear();
    }

    @Override
    public int size()
    {
        return delegate.size();
    }
}
