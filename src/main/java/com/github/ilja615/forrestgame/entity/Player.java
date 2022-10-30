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

package com.github.ilja615.forrestgame.entity;

import com.github.ilja615.forrestgame.Game;
import com.github.ilja615.forrestgame.entity.related.EffectTracker;
import com.github.ilja615.forrestgame.entity.related.StatTracker;
import com.github.ilja615.forrestgame.entity.related.StatTracker.Stat;
import com.github.ilja615.forrestgame.gui.particle.Particle;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Direction;
import com.github.ilja615.forrestgame.world.World;
import com.google.common.collect.ImmutableMap;

import static com.github.ilja615.forrestgame.util.KeyInput.isKeyDown;
import static org.lwjgl.glfw.GLFW.*;

public class Player implements Entity
{
    private static final float SCROLL_SPEED = 0.2f;
    private static final int ANIMATION_FRAMES_TIME = 60;
    private final World world;
    private final StatTracker statTracker;
    private final EffectTracker effectTracker;
    public Direction facing = Direction.DOWN;
    public int wait = 0;
    public Action currentDoingAction = Action.NOTHING;
    private Coordinate coordinate;
    private Coordinate scheduledCoordinate;
    private boolean mobile = true;
    private float animationTimer = 0.0f;

    public Player(final World world, final Coordinate startPos)
    {
        this.world = world;
        this.coordinate = startPos;
        this.statTracker = new StatTracker(this, ImmutableMap.of(Stat.HEALTH, 10, Stat.HUNGER, 10));
        this.effectTracker = new EffectTracker(this);
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

    public Coordinate getScheduledCoordinate()
    {
        return scheduledCoordinate;
    }

    @Override
    public void setCoordinate(final Coordinate coordinate)
    {
        this.coordinate = coordinate;
    }

    @Override
    public StatTracker getStatTracker()
    {
        return statTracker;
    }

    @Override
    public EffectTracker getEffectTracker() { return effectTracker; }

    @Override
    public void setMobile(final boolean mobile)
    {
        this.mobile = mobile;
    }

    @Override
    public void tick()
    {
        if (wait > 0)
        {
            wait--;
            return;
        }

        float partialX = world.getTextureRenderer().getPartialX();
        float partialY = world.getTextureRenderer().getPartialY();

        if (partialX > 0 && partialX < 1)
        {
            partialX += SCROLL_SPEED;
        } else if (partialX < 0 && partialX > -1)
        {
            partialX -= SCROLL_SPEED;
        } else if (partialX <= -1 || partialX >= 1)
        {
            partialX = 0;
            if (this.scheduledCoordinate != null)
            {
                this.setCoordinate(this.scheduledCoordinate);
                world.onEntityTurn(); // Enemy turn when player stopped walking
            }
        }

        world.getTextureRenderer().setPartialX(partialX);

        if (partialY > 0 && partialY < 1)
        {
            partialY += SCROLL_SPEED;
        } else if (partialY < 0 && partialY > -1)
        {
            partialY -= SCROLL_SPEED;
        } else if (partialY <= -1 || partialY >= 1)
        {
            partialY = 0;
            if (this.scheduledCoordinate != null)
            {
                this.setCoordinate(this.scheduledCoordinate);
                world.onEntityTurn(); // Enemy turn when player stopped walking
            }
        }

        world.getTextureRenderer().setPartialY(partialY);

        if (effectTracker.confusion.isActive())
        {
            Particle already = null;
            for (Particle particle : this.getWorld().getParticles())
            {
                if (particle.getTextures() == Textures.CONFUSION_PARTICLE)
                {
                    already = particle;
                }
            }
            if (already == null)
            {
                // There was not yet a confusion particle
                this.getWorld().getParticles().add(new Particle(coordinate, 1, 1, this.getWorld(), Textures.CONFUSION_PARTICLE));
            } else {
                // The current particle its position should be updated
                already.setCoordinate(this.coordinate);
                // The current one should be extended if it is close to expiration
                if (already.getCompletedCycles() >= already.getAmountLifeCycles() - 1)
                    already.setAmountLifeCycles(already.getAmountLifeCycles() + 1);
            }
        }

        if (mobile && partialX == 0 && partialY == 0)
        {
            // The player is not doing anything currently and can now do something
            this.currentDoingAction = Action.NOTHING;

            final Game game = world.getGame();

            if (isKeyDown(game, GLFW_KEY_UP) || isKeyDown(game, GLFW_KEY_W))
            {
                move(Direction.UP);
            } else if (isKeyDown(game, GLFW_KEY_DOWN) || isKeyDown(game, GLFW_KEY_S))
            {
                move(Direction.DOWN);
            } else if (isKeyDown(game, GLFW_KEY_LEFT) || isKeyDown(game, GLFW_KEY_A))
            {
                move(Direction.LEFT);
            } else if (isKeyDown(game, GLFW_KEY_RIGHT) || isKeyDown(game, GLFW_KEY_D))
            {
                move(Direction.RIGHT);
            }
        }
    }

    private void move(Direction dir)
    {
        final Game game = world.getGame();

        if (this.effectTracker.confusion.isActive())
            dir = dir.getOpposite();

        if (!isKeyDown(game, GLFW_KEY_LEFT_SHIFT) && !isKeyDown(game, GLFW_KEY_RIGHT_SHIFT))
        {
            this.mobile = false;
            this.facing = dir;
            this.currentDoingAction = Action.WALKING;
            handleMotionTowards(coordinate.transpose(dir), dir);
        } else
        {
            this.facing = dir;
            waitMoment();
        }
    }

    private void waitMoment()
    {
        wait = 1;
    }

    private void handleMotionTowards(final Coordinate newPos, final Direction direction)
    {
        if (world.isWithinWorld(newPos))
        {
            if (!world.getTileAt(newPos).isObstacle(this))
            {
                if (world.getEntityAt(newPos) != null)
                {
                    if (!world.getEntityAt(newPos).onPlayerAttemptingWalk(this, newPos))
                        return; // The player was not able to walk into the entity
                }
                if (world.getTileAt(newPos).onPlayerAttemptingWalk(this, newPos))
                {
                    this.scheduledCoordinate = newPos;

                    switch (direction)
                    {
                        case UP -> world.getTextureRenderer().setPartialY(-SCROLL_SPEED);
                        case DOWN -> world.getTextureRenderer().setPartialY(SCROLL_SPEED);
                        case LEFT -> world.getTextureRenderer().setPartialX(SCROLL_SPEED);
                        case RIGHT -> world.getTextureRenderer().setPartialX(-SCROLL_SPEED);
                    }
                    return;
                }
            }
        }
        this.mobile = true;
    }

    @Override
    public Texture getCurrentTexture()
    {
        if (this.currentDoingAction == Action.NOTHING)
        {
            // Standing still
            return switch (this.facing)
                    {
                        case UP -> Textures.PLAYER_UP;
                        case DOWN -> Textures.PLAYER_DOWN;
                        case LEFT -> Textures.PLAYER_LEFT;
                        case RIGHT -> Textures.PLAYER_RIGHT;
                    };
        } else
        {
            // Walking
            return switch (this.facing)
                    {
                        case UP -> Textures.PLAYER_UP_WALK[getAnimationFrame(ANIMATION_FRAMES_TIME, 4)];
                        case DOWN -> Textures.PLAYER_DOWN_WALK[getAnimationFrame(ANIMATION_FRAMES_TIME, 4)];
                        case LEFT -> Textures.PLAYER_LEFT_WALK[getAnimationFrame(ANIMATION_FRAMES_TIME, 4)];
                        case RIGHT -> Textures.PLAYER_RIGHT_WALK[getAnimationFrame(ANIMATION_FRAMES_TIME, 4)];
                    };
        }
    }

    @Override
    public boolean onPlayerAttemptingWalk(final Entity player, final Coordinate coordinate)
    {
        return false;
    }

    @Override
    public void die(final Stat deathCausingStat)
    {
        switch (deathCausingStat)
        {
            case HEALTH -> world.getGame().end(Game.EndReason.DIED);
            case HUNGER -> world.getGame().end(Game.EndReason.STARVED);
        }
    }

    @Override
    public void automaticallyMove()
    {
        // it is not an automatical mover but a controlled mover
    }

    @Override
    public boolean willAutomaticallyMove()
    {
        // it is not an automatical mover but a controlled mover
        return false;
    }

    @Override
    public float partialX()
    {
        return 0;
    }

    @Override
    public float partialY()
    {
        return 0;
    }

    private int getAnimationFrame(final int framesTime, final int amountFrames)
    {
        this.animationTimer += (1 / (float) framesTime);
        return ((int) this.animationTimer) % amountFrames;
    }

    public enum Action
    {
        NOTHING, SLASHING, WALKING, ROLLING
    }
}
	