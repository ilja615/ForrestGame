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
    private final List<T> list = new ArrayList<>();

    @Override
    public T get(final int index)
    {
        return list.get(index);
    }

    @Override
    public boolean add(final T t)
    {
        list.add(t);
        Collections.sort(list);

        return true;
    }

    @Override
    public T remove(final int index)
    {
        return list.remove(index);
    }

    @Override
    public void clear()
    {
        list.clear();
    }

    @Override
    public int size()
    {
        return list.size();
    }
}
