/*
 * Copyright (c) 2022 the ForrestGame contributors.
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

package com.github.ilja615.forrestgame.entity;

import com.github.ilja615.forrestgame.gui.particle.Particle;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.ShortPathFinder;
import com.github.ilja615.forrestgame.world.World;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class Scamperer implements Entity
{
    private static final Logger LOGGER = LoggerFactory.getLogger(World.class);
    private final World world;
    private final StatTracker statTracker;
    private Coordinate coordinate;
    private boolean mobile = true;

    public Scamperer(final World world, final Coordinate startPos)
    {
        this.world = world;
        this.coordinate = startPos;
        this.statTracker = new StatTracker(this, ImmutableMap.of(StatTracker.Stat.HEALTH, 3));
    }

    @Override
    public World getWorld()
    {
        return world;
    }

    @Override
    public Coordinate getCoordinate()
    {
        return coordinate;
    }

    @Override
    public void setCoordinate(Coordinate coordinate)
    {
        this.coordinate = coordinate;
    }

    @Override
    public StatTracker getStatTracker()
    {
        return statTracker;
    }

    @Override
    public void setMobile(final boolean mobile)
    {
        this.mobile = mobile;
    }

    @Override
    public Texture getCurrentTexture()
    {
        return Textures.SCAMPERER;
    }

    @Override
    public void tick()
    {

    }

    @Override
    public boolean onPlayerAttemptingWalk(Entity player, Coordinate coordinate)
    {
        this.statTracker.decrement(StatTracker.Stat.HEALTH);
        if (player instanceof Player)
        {
            ((Player) player).wait += 120;
            ((Player) player).currentDoingAction = Player.Action.SLASHING;
        }
        world.onEnemyTurn();
        player.getWorld().getParticles().add(new Particle(coordinate, 1, player.getWorld(), Textures.CHOP_PARTICLE));
        return false;
    }

    @Override
    public void die(StatTracker.Stat deathCausingStat)
    {
        world.getEntities().remove(this);
    }

    @Override
    public void automaticallyMove()
    {
        final ShortPathFinder pathFinder = new ShortPathFinder();
        final List<Coordinate> path = pathFinder.findPath(this.world, this.coordinate, this.world.getPlayer().getCoordinate(), this);

        LOGGER.info("Found path {}", path.stream()
                .map(Coordinate::toString)
                .collect(Collectors.joining(" -> ")));

        if (!path.isEmpty())
        {
            Coordinate newPos = path.get(1);
            if (world.isWithinWorld(newPos) && !world.getTileAt(newPos).isObstacle(this))
            {
                if (world.getPlayer().getCoordinate().equals(newPos)) {
                    // Attack the player
                    world.getPlayer().getStatTracker().decrement(StatTracker.Stat.HEALTH);
                    world.getParticles().add(new Particle(world.getPlayer().getCoordinate(), 1, world, Textures.CHOP_PARTICLE));
                } else {
                    if (world.getEntityAt(newPos) == null)
                        this.coordinate = newPos;
                }
            }
        }
    }
}
