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

package com.github.ilja615.forrestgame.entity.related;

import com.github.ilja615.forrestgame.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EffectTracker extends AbstractMap<Effect, Integer>
{
    private final Map<Effect, Integer> delegate;
    private final Entity entity;

    public EffectTracker(final Entity entity, final Map<Effect, Integer> stats)
    {
        this.delegate = new HashMap<>(stats);
        this.entity = entity;
    }

    @Override
    public Integer get(final Object key)
    {
        return delegate.get(key);
    }

    @NotNull
    @Override
    public Set<Entry<Effect, Integer>> entrySet()
    {
        return null;
    }
}
