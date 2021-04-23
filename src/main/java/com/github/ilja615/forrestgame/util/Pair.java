package com.github.ilja615.forrestgame.util;

public class Pair<F, S>
{
    private F firstThing;
    private S secondThing;

    public Pair(final F firstThing, final S secondThing)
    {
        this.firstThing = firstThing;
        this.secondThing = secondThing;
    }

    public F getFirstThing()
    {
        return firstThing;
    }

    public void setFirstThing(F firstThing)
    {
        this.firstThing = firstThing;
    }

    public S getSecondThing()
    {
        return secondThing;
    }

    public void setSecondThing(S secondThing)
    {
        this.secondThing = secondThing;
    }
}
