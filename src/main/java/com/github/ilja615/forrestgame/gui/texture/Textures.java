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

package com.github.ilja615.forrestgame.gui.texture;

import java.util.ArrayList;

public interface Textures
{
    // tiles
    Texture AIR = new PngTexture("textures/air");
    Texture GROUND_0 = new PngTexture("textures/ground_0");
    Texture GROUND_1 = new PngTexture("textures/ground_1");
    Texture GROUND_2 = new PngTexture("textures/ground_2");
    Texture GROUND_3 = new PngTexture("textures/ground_3");
    Texture WALL = new PngTexture("textures/wall");
    Texture MUSHROOM = new PngTexture("textures/mushroom");
    Texture SIGN = new PngTexture("textures/sign");
    Texture TREE = new PngTexture("textures/tree", true);

    Texture[] GROUND_ALTERNATIVES = new Texture[]{GROUND_0, GROUND_1, GROUND_2, GROUND_3};

    // player
    Texture PLAYER_UP = new PngTexture("textures/player_up");
    Texture PLAYER_DOWN = new PngTexture("textures/player_down");
    Texture PLAYER_LEFT = new PngTexture("textures/player_left");
    Texture PLAYER_RIGHT = new PngTexture("textures/player_right");

    Texture[] PLAYER_UP_WALK = new Texture[]
    {
        new PngTexture("textures/player_up_walking_0"),
        new PngTexture("textures/player_up_walking_1"),
        new PngTexture("textures/player_up_walking_2"),
        new PngTexture("textures/player_up_walking_3")
    };
    Texture[] PLAYER_DOWN_WALK = new Texture[]
    {
        new PngTexture("textures/player_down_walking_0"),
        new PngTexture("textures/player_down_walking_1"),
        new PngTexture("textures/player_down_walking_2"),
        new PngTexture("textures/player_down_walking_3")
    };
    Texture[] PLAYER_LEFT_WALK = new Texture[]
    {
        new PngTexture("textures/player_left_walking_0"),
        new PngTexture("textures/player_left_walking_1"),
        new PngTexture("textures/player_left_walking_2"),
        new PngTexture("textures/player_left_walking_3")
    };
    Texture[] PLAYER_RIGHT_WALK = new Texture[]
    {
        new PngTexture("textures/player_right_walking_0"),
        new PngTexture("textures/player_right_walking_1"),
        new PngTexture("textures/player_right_walking_2"),
        new PngTexture("textures/player_right_walking_3")
    };

    Texture[] PLAYER_UP_SLASH = new Texture[]
    {
        new PngTexture("textures/player_up_slash_0"),
        new PngTexture("textures/player_up_slash_1"),
        new PngTexture("textures/player_up_slash_2")
    };
    Texture[] PLAYER_DOWN_SLASH = new Texture[]
    {
        new PngTexture("textures/player_down_slash_0"),
        new PngTexture("textures/player_down_slash_1"),
        new PngTexture("textures/player_down_slash_2")
    };
    Texture[] PLAYER_LEFT_SLASH = new Texture[]
    {
        new PngTexture("textures/player_left_slash_0"),
        new PngTexture("textures/player_left_slash_1"),
        new PngTexture("textures/player_left_slash_2")
    };
    Texture[] PLAYER_RIGHT_SLASH = new Texture[]
    {
        new PngTexture("textures/player_right_slash_0"),
        new PngTexture("textures/player_right_slash_1"),
        new PngTexture("textures/player_right_slash_2")
    };

    // other
    Texture VIEWPORT = new PngTexture("textures/viewport");
}
