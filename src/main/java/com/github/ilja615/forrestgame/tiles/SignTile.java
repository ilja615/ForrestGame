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

package com.github.ilja615.forrestgame.tiles;

import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.world.World;

public class SignTile extends Tile
{
    public SignTile(final Texture texture)
    {
        super(texture);
    }

    @Override
    public boolean onPlayerAttemptingWalk(final Entity player, final Coordinate coordinate)
    {
        // The code for moving to the next stage
        World world = player.getWorld();
        world.getTimeTracker().incrementCurrentTime();
        world.getTextureRenderer().setDisabled();
        world.getTimeTracker().waitTicks = 1000;
        world.generate();

        return false;
    }
}
