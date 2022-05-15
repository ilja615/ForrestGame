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
    private final Coordinate coordinate;
    private final Texture[] TEXTURES;
    private final int amountLifeCycles;
    private final World world;
    private int currentFrame = 0;
    private int animationTimer = 0;
    private int completedCycles = 0;
    private boolean expired = false;
    private final int ticksPerFrame;

    public Particle(Coordinate coord, int amountCycles, int tpf, World world, Texture[] textures)
    {
        this.coordinate = coord;
        this.amountLifeCycles = amountCycles;
        this.world = world;
        this.TEXTURES = textures;
        this.ticksPerFrame = tpf;
    }

    public Texture getCurrentTexture()
    {
        return TEXTURES[currentFrame % TEXTURES.length];
    }

    public int getCurrentFrame()
    {
        return this.currentFrame;
    }

    public void setCurrentFrame(int currentFrame)
    {
        this.currentFrame = currentFrame;
    }

    public Coordinate getCoordinate()
    {
        return this.coordinate;
    }

    public boolean isExpired()
    {
        return this.expired;
    }

    @Override
    public void tick()
    {
        this.animationTimer += 1;
        if (this.animationTimer > this.ticksPerFrame)
        {
            this.currentFrame++;
            this.animationTimer = 0;

            if (this.currentFrame >= this.TEXTURES.length)
            {
                if (++this.completedCycles >= this.amountLifeCycles)
                    this.expired = true;
                else
                    this.currentFrame = 0;
            }
        }
    }
}
