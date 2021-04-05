package com.github.ilja615.forrestgame.world;

import java.util.Locale;

public class TimeTracker
{
    private int currentTime = 0;

    public int getCurrentTime()
    {
        return currentTime;
    }

    public String getCurrentTimeString()
    {
        return "day " + getAmountSurvivedDays(currentTime) + " " + getPeriodFromTime(currentTime).toString().toLowerCase(Locale.ROOT);
    }

    public void incrementCurrentTime()
    {
        this.currentTime++;
    }

    public Period getPeriodFromTime(final int currentTime)
    {
        return switch (currentTime % 8)
                {
                    case 0 -> Period.SUNRISE;
                    case 1 -> Period.MORNING;
                    case 2 -> Period.NOON;
                    case 3 -> Period.AFTERNOON;
                    case 4 -> Period.SUNSET;
                    case 5 -> Period.EVENING;
                    case 6 -> Period.NIGHT;
                    case 7 -> Period.MIDNIGHT;
                    default -> throw new IllegalStateException("Unexpected value: " + currentTime % 8);
                };
    }

    public int getAmountSurvivedDays(final int currentTime)
    {
        return (int) Math.floor(currentTime / 8.0d) + 1;
    }

    public enum Period
    {
        SUNRISE(true),
        MORNING(true),
        NOON(true),
        AFTERNOON(true),
        SUNSET(false),
        EVENING(false),
        NIGHT(false),
        MIDNIGHT(false);

        private final boolean isDayTime;

        Period(final boolean isDayTime)
        {
            this.isDayTime = isDayTime;
        }
    }
}
