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
import com.github.ilja615.forrestgame.gui.renderer.TextureRenderer;
import com.github.ilja615.forrestgame.gui.texture.PngTexture;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Direction;
import com.github.ilja615.forrestgame.world.World;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;

import static com.github.ilja615.forrestgame.util.KeyInput.isKeyDown;
import static org.lwjgl.glfw.GLFW.*;

public class Player implements Entity
{
    private static final float SCROLL_SPEED = 0.008f;
    private final World world;
    private final StatTracker statTracker;
    private Coordinate coordinate;
    private Coordinate scheduledCoordinate;
    private boolean mobile = true;
    public Direction facing = Direction.DOWN;
    private float animationTimer = 0.0f;
    public int wait = 0;
    public Action currentDoingAction = Action.NOTHING;

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
            if (this.scheduledCoordinate != null) this.setCoordinate(this.scheduledCoordinate);
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
            if (this.scheduledCoordinate != null) this.setCoordinate(this.scheduledCoordinate);
        }

        world.getTextureRenderer().setPartialY(partialY);

        if (mobile && partialX == 0 && partialY == 0)
        {
            // The player is not doing anything currently and can now do something
            this.currentDoingAction = Action.NOTHING;

            final Game game = world.getGame();

            if (isKeyDown(game, GLFW_KEY_UP) || isKeyDown(game, GLFW_KEY_W)) { moveUp(); }
            else if (isKeyDown(game, GLFW_KEY_DOWN) || isKeyDown(game, GLFW_KEY_S)) { moveDown(); }
            else if (isKeyDown(game, GLFW_KEY_LEFT) || isKeyDown(game, GLFW_KEY_A)) { moveLeft(); }
            else if (isKeyDown(game, GLFW_KEY_RIGHT) || isKeyDown(game, GLFW_KEY_D)) { moveRight(); }
        }
    }

    private void moveUp()
    {
        final Game game = world.getGame();
        if (!isKeyDown(game, GLFW_KEY_LEFT_SHIFT) && !isKeyDown(game, GLFW_KEY_RIGHT_SHIFT))
        {
            this.mobile = false;
            this.facing = Direction.UP;
            this.currentDoingAction = Action.WALKING;
            move(coordinate.up(), Direction.UP);
        } else {
            this.facing = Direction.UP;
            waitMoment();
        }
    }

    private void moveDown()
    {
        final Game game = world.getGame();
        if (!isKeyDown(game, GLFW_KEY_LEFT_SHIFT) && !isKeyDown(game, GLFW_KEY_RIGHT_SHIFT))
        {
            this.mobile = false;
            this.facing = Direction.DOWN;
            this.currentDoingAction = Action.WALKING;
            move(coordinate.down(), Direction.DOWN);
        } else {
            this.facing = Direction.DOWN;
            waitMoment();
        }
    }

    private void moveLeft()
    {
        final Game game = world.getGame();
        if (!isKeyDown(game, GLFW_KEY_LEFT_SHIFT) && !isKeyDown(game, GLFW_KEY_RIGHT_SHIFT))
        {
            this.mobile = false;
            this.facing = Direction.LEFT;
            this.currentDoingAction = Action.WALKING;
            move(coordinate.left(), Direction.LEFT);
        } else {
            this.facing = Direction.LEFT;
            waitMoment();
        }
    }

    private void moveRight()
    {
        final Game game = world.getGame();
        if (!isKeyDown(game, GLFW_KEY_LEFT_SHIFT) && !isKeyDown(game, GLFW_KEY_RIGHT_SHIFT))
        {
            this.mobile = false;
            this.facing = Direction.RIGHT;
            this.currentDoingAction = Action.WALKING;
            move(coordinate.right(), Direction.RIGHT);
        } else {
            this.facing = Direction.RIGHT;
            waitMoment();
        }
    }

    private void waitMoment()
    {
        wait = 1;
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
        } else {
            // Walking
            return switch (this.facing)
            {
                case UP -> Textures.PLAYER_UP_WALK[getAnimationFrame(200, 4)];
                case DOWN -> Textures.PLAYER_DOWN_WALK[getAnimationFrame(200, 4)];
                case LEFT -> Textures.PLAYER_LEFT_WALK[getAnimationFrame(200, 4)];
                case RIGHT -> Textures.PLAYER_RIGHT_WALK[getAnimationFrame(200, 4)];
            };
        }
    }

    private int getAnimationFrame(int framesTime, int amountFrames)
    {
        this.animationTimer += (1/(float)framesTime);
        return ((int)this.animationTimer) % amountFrames;
    }

    public enum Action
    {
        NOTHING, SLASHING, WALKING, ROLLING
    }
}
	