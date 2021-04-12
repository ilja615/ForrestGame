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
import com.github.ilja615.forrestgame.entity.Player;
import com.github.ilja615.forrestgame.entity.StatTracker.Stat;
import com.github.ilja615.forrestgame.gui.texture.PngTexture;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.util.Coordinate;

public class BushTile implements Tile
{
    private static final Texture TEXTURE_0 = new PngTexture("textures/bush0");
    private static final Texture TEXTURE_1 = new PngTexture("textures/bush1");
    private static final Texture TEXTURE_2 = new PngTexture("textures/bush2");
    private int stage = 2;

    @Override
    public Texture getTexture()
    {
        return switch (stage)
                {
                    default -> TEXTURE_0;
                    case 1 -> TEXTURE_1;
                    case 2 -> TEXTURE_2;
                };
    }

    @Override
    public boolean onPlayerAttemptingWalk(final Entity player, final Coordinate coordinate)
    {
        if (stage > 0)
        {
            stage -= 1;

            player.setMobile(false);

            ((Player) player).wait += 120;
            ((Player) player).currentDoingAction = Player.Action.SLASHING;

            player.getStatTracker().decrement(Stat.HUNGER);
            player.getWorld().onEnemyTurn();

            return false;
        } else
        {
            return true;
        }
    }
}
