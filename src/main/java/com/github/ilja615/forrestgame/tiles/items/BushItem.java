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

package com.github.ilja615.forrestgame.tiles.items;

import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.entity.Player;
import com.github.ilja615.forrestgame.entity.related.StatTracker.Stat;
import com.github.ilja615.forrestgame.gui.particle.Particle;
import com.github.ilja615.forrestgame.gui.renderer.TextureRenderer;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Pair;
import com.google.common.collect.Multimap;

import java.util.Map;

public class BushItem implements Item
{
    private int stage = 2;

    @Override
    public Texture getCurrentTexture()
    {
        return switch (stage)
                {
                    default -> Textures.BUSH_0;
                    case 1 -> Textures.BUSH_1;
                    case 2 -> Textures.BUSH_2;
                };
    }

    @Override
    public boolean isObstacle(final Entity incomingEntity)
    {
        return !(incomingEntity instanceof Player || this.stage == 0);
    }

    @Override
    public boolean onPlayerAttemptingWalk(final Entity player, final Coordinate coordinate)
    {
        if (stage > 0)
        {
            stage -= 1;

            player.setMobile(false);
            if (player instanceof Player)
            {
                ((Player) player).wait += 5;
                ((Player) player).currentDoingAction = Player.Action.SLASHING;
            }
            player.getStatTracker().decrement(Stat.HUNGER);
            player.getWorld().onEntityTurn();
            player.getWorld().getParticles().add(new Particle(coordinate, 1, 1, player.getWorld(), Textures.CHOP_PARTICLE));

            return false;
        } else
        {
            return true;
        }
    }

    @Override
    public Multimap<Pair<Coordinate, Pair<Float, Float>>, Object> whichLayer(final TextureRenderer tr)
    {
        return tr.LAYER_BACK;
    }
}
