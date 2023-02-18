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
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;

import java.util.Arrays;

public class EffectTracker
{
    private final Entity entity;
    public Effect confusion = Effect.CONFUSION;

    public EffectTracker(final Entity entity)
    {
        this.entity = entity;
    }

    public void decrementAll() { Arrays.stream(getAllEffects()).forEach(Effect::decrement); }

    public Effect[] getAllEffects()
    {
        return Effect.values();
    }

    public static Texture getTextureFromEffect(Effect effect)
    {
        return switch (effect)
        {
            case CONFUSION -> Textures.CONFUSED;
        };
    }

    public enum Effect
    {
        CONFUSION;

        private int turnsLeft = 0;

        Effect() {

        }

        public int getTurnsLeft()
        {
            return turnsLeft;
        }

        public void setTurnsLeft(int amount)
        {
            this.turnsLeft = amount;
        }

        public void decrement()
        {
             if (this.isActive()) this.turnsLeft -= 1;
        }

        public boolean isActive()
        {
            return this.turnsLeft > 0;
        }
    }
}
