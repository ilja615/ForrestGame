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

package com.github.ilja615.forrestgame.tiles.items;

import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.gui.renderer.TextureRenderer;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Pair;

import java.util.ArrayList;

public class TreeItem implements Item
{
    private Texture texture;

    public TreeItem(final Texture t)
    {
        this.texture = t;
    }

    @Override
    public Texture getCurrentTexture()
    {
        return this.texture;
    }

    @Override
    public boolean isObstacle()
    {
        return true;
    }

    @Override
    public boolean onPlayerAttemptingWalk(Entity player, Coordinate coordinate)
    {
        return false;
    }

    @Override
    public ArrayList<Pair<Coordinate, Texture>> whichLayer()
    {
        return TextureRenderer.LAYER_FRONT;
    }
}
