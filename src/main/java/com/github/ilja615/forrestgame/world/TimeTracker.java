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

package com.github.ilja615.forrestgame.world;

import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;

import java.util.Locale;

public class TimeTracker
{
    public int waitTicks = 0;
    private int currentTime = 1;

    public int getCurrentTime()
    {
        return currentTime;
    }

    public String getCurrentTimeString()
    {
        return "day " + getAmountSurvivedDays(currentTime) + " - " + getPeriodFromTime(currentTime).toString().toLowerCase(Locale.ROOT);
    }

    public String getCurrentDayString()
    {
        return "day " + getAmountSurvivedDays(currentTime);
    }

    public void incrementCurrentTime()
    {
        this.currentTime++;
    }

    public Period getPeriodFromTime(int currentTime)
    {
        return switch (currentTime % 6)
                {
                    case 0 -> Period.SUNRISE;
                    case 1 -> Period.MORNING;
                    case 2 -> Period.AFTERNOON;
                    case 3 -> Period.SUNSET;
                    case 4 -> Period.EVENING;
                    case 5 -> Period.NIGHT;
                    default -> throw new IllegalStateException("Unexpected value: " + currentTime % 6);
                };
    }

    public int getAmountSurvivedDays(int currentTime)
    {
        return (int) Math.floor(currentTime / 6.0d) + 1;
    }

    public float getRedComponent()
    {
        return getPeriodFromTime(getCurrentTime()).red;
    }

    public float getGreenComponent()
    {
        return getPeriodFromTime(getCurrentTime()).green;
    }

    public float getBlueComponent()
    {
        return getPeriodFromTime(getCurrentTime()).blue;
    }

    public enum Period
    {
        SUNRISE(Textures.SUNRISE, true, 0.5f),
        MORNING(Textures.MORNING, true),
        AFTERNOON(Textures.AFTERNOON, true),
        SUNSET(Textures.SUNSET, false, 0.6f, 0.5f, 0.45f),
        EVENING(Textures.EVENING, false, 0.35f, 0.35f, 0.35f),
        NIGHT(Textures.NIGHT, false, 0.2f, 0.2f, 0.3f);

        public final float red;
        public final float green;
        public final float blue;
        private final boolean isDayTime;
        private final Texture texture;

        Period(Texture texture, boolean isDayTime, float red, float green, float blue)
        {
            this.texture = texture;
            this.isDayTime = isDayTime;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        Period(Texture texture, boolean isDayTime) // Defaults lightness of 1.0f for R, G and B.
        {
            this(texture, isDayTime, 1.0f, 1.0f, 1.0f);
        }

        Period(Texture texture, boolean isDayTime, float lightness)
        {
            this(texture, isDayTime, lightness, lightness, lightness);
        }

        public Texture getTexture()
        {
            return this.texture;
        }
    }
}
