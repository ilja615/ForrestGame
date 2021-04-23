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

package com.github.ilja615.forrestgame.world;

import java.util.Locale;

public class TimeTracker
{
    private int currentTime = 1;
    public int waitTicks = 0;

    public int getCurrentTime() { return currentTime; }

    public String getCurrentTimeString()
    {
        return "day " + getAmountSurvivedDays(currentTime) + " - " + getPeriodFromTime(currentTime).toString().toLowerCase(Locale.ROOT);
    }

    public String getCurrentDayString()
    {
        return "day " + getAmountSurvivedDays(currentTime);
    }

    public void incrementCurrentTime() { this.currentTime++; }

    public static enum Period
    {
        SUNRISE(true),
        MORNING(true),
        AFTERNOON(true),
        SUNSET(false),
        EVENING(false),
        NIGHT(false);

        private boolean isDayTime;

        Period(boolean isDayTime)
        {
            this.isDayTime = isDayTime;
        }
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

    public float getDayLight()
    {
        return switch (getCurrentTime() % 6)
                {
                    case 0, 3 -> 0.5f;
                    case 1, 2 -> 1.0f;
                    case 4, 5 -> 0.2f;
                    default -> throw new IllegalStateException("Unexpected value: " + currentTime % 6);
                };
    }
}
