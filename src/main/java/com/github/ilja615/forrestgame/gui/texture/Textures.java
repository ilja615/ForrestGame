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

import com.github.ilja615.forrestgame.world.TimeTracker;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

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
    Texture TREE = new PngTexture("textures/tree").setTall(true);

    Texture[] GROUND_ALTERNATIVES = new Texture[]{GROUND_0, GROUND_1, GROUND_2, GROUND_3};

    // player
    Texture PLAYER_UP = new PngTexture("textures/player_up").setPlayerTexture(true);
    Texture PLAYER_DOWN = new PngTexture("textures/player_down").setPlayerTexture(true);
    Texture PLAYER_LEFT = new PngTexture("textures/player_side").setHorizontallyMirrored(true).setPlayerTexture(true);
    Texture PLAYER_RIGHT = new PngTexture("textures/player_side").setPlayerTexture(true);

    Texture[] PLAYER_UP_WALK = new Texture[]
    {
        new PngTexture("textures/player_up_walking_0").setPlayerTexture(true),
        new PngTexture("textures/player_up_walking_1").setPlayerTexture(true),
        new PngTexture("textures/player_up_walking_2").setPlayerTexture(true),
        new PngTexture("textures/player_up_walking_3").setPlayerTexture(true)
    };
    Texture[] PLAYER_DOWN_WALK = new Texture[]
    {
        new PngTexture("textures/player_down_walking_0").setPlayerTexture(true),
        new PngTexture("textures/player_down_walking_1").setPlayerTexture(true),
        new PngTexture("textures/player_down_walking_2").setPlayerTexture(true),
        new PngTexture("textures/player_down_walking_3").setPlayerTexture(true)
    };
    Texture[] PLAYER_LEFT_WALK = new Texture[]
    {
        new PngTexture("textures/player_side_walking_0").setHorizontallyMirrored(true).setPlayerTexture(true),
        new PngTexture("textures/player_side_walking_1").setHorizontallyMirrored(true).setPlayerTexture(true),
        new PngTexture("textures/player_side_walking_2").setHorizontallyMirrored(true).setPlayerTexture(true),
        new PngTexture("textures/player_side_walking_3").setHorizontallyMirrored(true).setPlayerTexture(true)
    };
    Texture[] PLAYER_RIGHT_WALK = new Texture[]
    {
        new PngTexture("textures/player_side_walking_0").setPlayerTexture(true),
        new PngTexture("textures/player_side_walking_1").setPlayerTexture(true),
        new PngTexture("textures/player_side_walking_2").setPlayerTexture(true),
        new PngTexture("textures/player_side_walking_3").setPlayerTexture(true)
    };

    // particles
    Texture[] CHOP_PARTICLE = new Texture[]
            {
                    new PngTexture("textures/chop_particle_0"),
                    new PngTexture("textures/chop_particle_1"),
                    new PngTexture("textures/chop_particle_2"),
                    new PngTexture("textures/chop_particle_3")
            };

    // other gui
    Texture VIEWPORT = new PngTexture("textures/viewport");
    Texture HEALTH = new PngTexture("textures/ui_health");
    Texture ENERGY = new PngTexture("textures/ui_energy");

    // time gui
    Texture SUNRISE = new PngTexture("textures/ui_time_sunrise");
    Texture MORNING = new PngTexture("textures/ui_time_morning");
    Texture AFTERNOON = new PngTexture("textures/ui_time_afternoon");
    Texture SUNSET = new PngTexture("textures/ui_time_sunset");
    Texture EVENING = new PngTexture("textures/ui_time_evening");
    Texture NIGHT = new PngTexture("textures/ui_time_night");
}