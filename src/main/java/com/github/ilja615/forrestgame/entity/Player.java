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

package com.github.ilja615.forrestgame.entity;

import com.github.ilja615.forrestgame.Game;
import com.github.ilja615.forrestgame.entity.StatTracker.Stat;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Direction;
import com.github.ilja615.forrestgame.world.World;
import com.google.common.collect.ImmutableMap;

import static com.github.ilja615.forrestgame.util.KeyInput.isKeyDown;
import static org.lwjgl.glfw.GLFW.*;

public class Player implements Entity
{
    private static final float SCROLL_SPEED = 0.03f;
    private final World world;
    private final StatTracker statTracker;
    private Coordinate coordinate;
    private Coordinate scheduledCoordinate;
    private boolean mobile = true;

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
    public void tick()
    {
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
            final Game game = world.getGame();

            if (isKeyDown(game, GLFW_KEY_W) || isKeyDown(game, GLFW_KEY_UP)) moveUp();
            else if (isKeyDown(game, GLFW_KEY_S) || isKeyDown(game, GLFW_KEY_DOWN)) moveDown();
            else if (isKeyDown(game, GLFW_KEY_A) || isKeyDown(game, GLFW_KEY_LEFT)) moveLeft();
            else if (isKeyDown(game, GLFW_KEY_D) || isKeyDown(game, GLFW_KEY_RIGHT)) moveRight();
        }
    }

    private void moveUp()
    {
        this.mobile = false;
        move(coordinate.up(), Direction.UP);
    }

    private void moveDown()
    {
        this.mobile = false;
        move(coordinate.down(), Direction.DOWN);
    }

    private void moveLeft()
    {
        this.mobile = false;
        move(coordinate.left(), Direction.LEFT);
    }

    private void moveRight()
    {
        this.mobile = false;
        move(coordinate.right(), Direction.RIGHT);
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
}
	