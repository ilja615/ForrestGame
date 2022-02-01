package com.github.ilja615.forrestgame.entity;

import com.github.ilja615.forrestgame.gui.particle.Particle;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.ShortPathFinder;
import com.github.ilja615.forrestgame.world.World;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.stream.Collectors;

public class Scamperer implements Entity
{
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
        world.onEnemyTurnCalled();
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
        final List<Coordinate> path = pathFinder.findPath(this.world, this.coordinate, this.world.getPlayer().getCoordinate());

        if (!path.isEmpty())
        {
            Coordinate newPos = path.get(1);
            if (world.isWithinWorld(newPos) && !world.getTileAt(newPos).isObstacle() && world.getEntityAt(newPos) != null)
            {
                this.coordinate = newPos;
            }
        }
    }
}
