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
import com.github.ilja615.forrestgame.entity.related.StatTracker;
import com.github.ilja615.forrestgame.gui.particle.Particle;
import com.github.ilja615.forrestgame.gui.renderer.TextureRenderer;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Pair;

import java.util.Map;

public class BerryBushItem implements Item
{
    private int stage = 3;

    @Override
    public Texture getCurrentTexture()
    {
        return switch (stage)
                {
                    default -> Textures.BUSH_0;
                    case 1 -> Textures.BUSH_1;
                    case 2 -> Textures.BUSH_2;
                    case 3 -> Textures.BUSH_BERRY;
                };
    }

    @Override
    public boolean isObstacle(final Entity incomingEntity)
    {
        return !(incomingEntity instanceof Player || this.stage == 0);
    }

    @Override
    public boolean onPlayerAttemptingWalk(final Entity entity, final Coordinate coordinate)
    {
        if (stage > 0)
        {
            stage -= 1;

            entity.setMobile(false);
            if (entity instanceof Player)
            {
                ((Player) entity).wait += 5;
                ((Player) entity).currentDoingAction = Player.Action.SLASHING;
            }
            if (stage < 2)
            {
                // Chop the bush
                entity.getStatTracker().decrement(StatTracker.Stat.HUNGER);
            } else {
                // Eat the berry
                entity.getStatTracker().increment(StatTracker.Stat.HEALTH);
            }
            entity.getWorld().getParticles().add(new Particle(coordinate, 1, 1, entity.getWorld(), Textures.CHOP_PARTICLE));
            entity.getWorld().onEntityTurn();

            return false;
        } else
        {
            return true;
        }
    }

    @Override
    public Map<Pair<Coordinate, Pair<Float, Float>>, Texture> whichLayer(final TextureRenderer tr)
    {
        return tr.LAYER_BACK;
    }
}
