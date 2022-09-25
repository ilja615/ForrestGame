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

package com.github.ilja615.forrestgame.gui.particle;

import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Tickable;
import com.github.ilja615.forrestgame.world.World;

public class Particle implements Tickable
{
    private Coordinate coordinate;
    private Texture[] textures;
    private int amountLifeCycles;
    private final World world;
    private int ticksPerFrame;
    private int currentFrame = 0;
    private int animationTimer = 0;
    private int completedCycles = 0;
    private boolean expired = false;

    public Particle(final Coordinate coordinate, final int amountCycles, final int ticksPerFrame, final World world, final Texture[] textures)
    {
        this.coordinate = coordinate;
        this.amountLifeCycles = amountCycles;
        this.world = world;
        this.textures = textures;
        this.ticksPerFrame = ticksPerFrame;
    }

    public Texture getCurrentTexture()
    {
        return textures[currentFrame % textures.length];
    }

    public int getCurrentFrame()
    {
        return this.currentFrame;
    }

    public void setCurrentFrame(final int currentFrame)
    {
        this.currentFrame = currentFrame;
    }

    public Coordinate getCoordinate()
    {
        return this.coordinate;
    }

    public void setCoordinate(Coordinate c) { this.coordinate = c; }

    public boolean isExpired()
    {
        return this.expired;
    }

    public int getAmountLifeCycles()
    {
        return amountLifeCycles;
    }

    public void setAmountLifeCycles(final int amountLifeCycles)
    {
        this.amountLifeCycles = amountLifeCycles;
    }

    public int getCompletedCycles()
    {
        return completedCycles;
    }

    public Texture[] getTextures()
    {
        return textures;
    }

    public void setTextures(final Texture[] textures)
    {
        this.textures = textures;
    }

    @Override
    public void tick()
    {
        this.animationTimer += 1;
        if (this.animationTimer > this.ticksPerFrame)
        {
            this.currentFrame++;
            this.animationTimer = 0;

            if (this.currentFrame >= this.textures.length)
            {
                if (++this.completedCycles >= this.amountLifeCycles) this.expired = true;
                else this.currentFrame = 0;
            }
        }
    }
}
