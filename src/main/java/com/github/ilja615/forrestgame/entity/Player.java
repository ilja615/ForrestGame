/*
 * Copyright (c) 2021 xf8b, ilja615.
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

package com.github.ilja615.forrestgame.entity;

import com.github.ilja615.forrestgame.Game;
import com.github.ilja615.forrestgame.entity.StatTracker.Stat;
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
    private static final float SCROLL_SPEED = 0.008f;
    private final World world;
    private final StatTracker statTracker;
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
        this.statTracker = new StatTracker(world.getGame(), ImmutableMap.of(Stat.HEALTH, 10, Stat.HUNGER, 10));
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
    public void setMobile(final boolean mobile)
    {
        this.mobile = mobile;
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
        } else if (this.currentDoingAction == Action.SLASHING)
        {
            // Slashing against bush or enemy
            return switch (this.facing)
                    {
                        case UP -> Textures.PLAYER_UP_SLASH[getAnimationFrame(30, 3)];
                        case DOWN -> Textures.PLAYER_DOWN_SLASH[getAnimationFrame(30, 3)];
                        case LEFT -> Textures.PLAYER_LEFT_SLASH[getAnimationFrame(30, 3)];
                        case RIGHT -> Textures.PLAYER_RIGHT_SLASH[getAnimationFrame(30, 3)];
                    };
        } else
        {
            // Walking
            return switch (this.facing)
                    {
                        case UP -> Textures.PLAYER_UP_WALK[getAnimationFrame(60, 4)];
                        case DOWN -> Textures.PLAYER_DOWN_WALK[getAnimationFrame(60, 4)];
                        case LEFT -> Textures.PLAYER_LEFT_WALK[getAnimationFrame(60, 4)];
                        case RIGHT -> Textures.PLAYER_RIGHT_WALK[getAnimationFrame(60, 4)];
                    };
        }
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
            //  world.getTextureRenderer().getPlayerAngle = (int)(Math.sin(world.getTextureRenderer().getPartialX * 6.2f)*10);
        } else if (partialX < 0 && partialX > -1)
        {
            partialX -= SCROLL_SPEED;
            //  world.getTextureRenderer().getPlayerAngle = (int)(Math.sin(world.getTextureRenderer().getPartialX * 6.2f)*10);
        } else if (partialX <= -1 || partialX >= 1)
        {
            partialX = 0;
            //  world.getTextureRenderer().getPlayerAngle = 0;
            if (this.scheduledCoordinate != null) this.setCoordinate(this.scheduledCoordinate);
        }

        world.getTextureRenderer().setPartialX(partialX);

        if (partialY > 0 && partialY < 1)
        {
            partialY += SCROLL_SPEED;
            //  world.getTextureRenderer().getPlayerAngle = (int)(Math.sin(partialY * 6.2f)*10);
        } else if (partialY < 0 && partialY > -1)
        {
            partialY -= SCROLL_SPEED;
            //  world.getTextureRenderer().getPlayerAngle = (int)(Math.sin(partialY * 6.2f)*10);
        } else if (partialY <= -1 || partialY >= 1)
        {
            partialY = 0;
            //  world.getTextureRenderer().getPlayerAngle = 0;
            if (this.scheduledCoordinate != null) this.setCoordinate(this.scheduledCoordinate);
        }

        world.getTextureRenderer().setPartialY(partialY);

        if (mobile && partialX == 0 && partialY == 0)
        {
            // The player is not doing anything currently and can now do something
            this.currentDoingAction = Action.NOTHING;

            final Game game = world.getGame();

            if (isKeyDown(game, GLFW_KEY_W) || isKeyDown(game, GLFW_KEY_UP)) moveUp();
            else if (isKeyDown(game, GLFW_KEY_S) || isKeyDown(game, GLFW_KEY_DOWN)) moveDown();
            else if (isKeyDown(game, GLFW_KEY_A) || isKeyDown(game, GLFW_KEY_LEFT)) moveLeft();
            else if (isKeyDown(game, GLFW_KEY_D) || isKeyDown(game, GLFW_KEY_RIGHT)) moveRight();
        }
    }

    private void moveUp()
    {
        if (this.facing == Direction.UP)
        {
            this.mobile = false;
            this.currentDoingAction = Action.WALKING;
            move(coordinate.up(), Direction.UP);
        } else
        {
            this.facing = Direction.UP;
            waitMoment();
        }
    }

    private void moveDown()
    {
        if (this.facing == Direction.DOWN)
        {
            this.mobile = false;
            this.currentDoingAction = Action.WALKING;
            move(coordinate.down(), Direction.DOWN);
        } else
        {
            this.facing = Direction.DOWN;
            waitMoment();
        }
    }

    private void moveLeft()
    {
        if (this.facing == Direction.LEFT)
        {
            this.mobile = false;
            this.currentDoingAction = Action.WALKING;
            move(coordinate.left(), Direction.LEFT);
        } else
        {
            this.facing = Direction.LEFT;
            waitMoment();
        }
    }

    private void moveRight()
    {
        if (this.facing == Direction.RIGHT)
        {
            this.mobile = false;
            this.currentDoingAction = Action.WALKING;
            move(coordinate.right(), Direction.RIGHT);
        } else
        {
            this.facing = Direction.RIGHT;
            waitMoment();
        }
    }

    private void waitMoment()
    {
    }

    private void move(final Coordinate coordinate, final Direction direction)
    {
        if (world.getTiles()[coordinate.getX() + (coordinate.getY() * World.WORLD_WIDTH)].onPlayerAttemptingWalk(this, coordinate))
        {
            if (world.isValidLocation(coordinate))
            {
                this.scheduledCoordinate = coordinate;
                world.onEnemyTurn();

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

        this.mobile = true;
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
	