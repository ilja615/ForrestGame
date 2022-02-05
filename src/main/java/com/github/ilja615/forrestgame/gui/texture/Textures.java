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

package com.github.ilja615.forrestgame.gui.texture;

public final class Textures
{
    // tiles and items
    public static final Texture AIR = new PngTexture("air");
    public static final Texture GRASS_0 = new PngTexture("grass_0");
    public static final Texture GRASS_1 = new PngTexture("grass_1");
    public static final Texture GRASS_2 = new PngTexture("grass_2");
    public static final Texture GRASS_3 = new PngTexture("grass_3");
    public static final Texture[] MUSHROOM = new Texture[]{new PngTexture("mushroom_0"), new PngTexture("mushroom_1")};
    public static final Texture SIGN = new PngTexture("sign");
    public static final Texture TREE = new PngTexture("tree").setTall(true);
    public static final Texture CRATE = new PngTexture("crate");
    public static final Texture[] GROUND_ALTERNATIVES = new Texture[]{GRASS_0, GRASS_1, GRASS_2, GRASS_3};
    // wall
    public static final Texture WALL_AIR_PIECE = new PngTexture("wall_air");
    public static final Texture WALL_STRAIGHT_PIECE = new PngTexture("wall_straight");
    public static final Texture WALL_STRAIGHT_PIECE_MIRRORED = new PngTexture("wall_straight").setVerticallyMirrored(true);
    public static final Texture WALL_STRAIGHT_VERTICAL_PIECE = new PngTexture("wall_straight_vertical");
    public static final Texture WALL_STRAIGHT_VERTICAL_PIECE_MIRRORED = new PngTexture("wall_straight_vertical").setHorizontallyMirrored(true);
    public static final Texture WALL_INNER_CORNER_PIECE = new PngTexture("wall_inner_corner");
    public static final Texture WALL_INNER_CORNER_PIECE_HM = new PngTexture("wall_inner_corner").setHorizontallyMirrored(true);
    public static final Texture WALL_INNER_CORNER_PIECE_VM = new PngTexture("wall_inner_corner").setVerticallyMirrored(true);
    public static final Texture WALL_INNER_CORNER_PIECE_HVM = new PngTexture("wall_inner_corner").setHorizontallyMirrored(true).setVerticallyMirrored(true);
    public static final Texture WALL_OUTER_CORNER_PIECE = new PngTexture("wall_outer_corner");
    public static final Texture WALL_OUTER_CORNER_PIECE_HM = new PngTexture("wall_outer_corner_other");
    public static final Texture WALL_OUTER_CORNER_PIECE_VM = new PngTexture("wall_outer_corner_other").setHorizontallyMirrored(true).setVerticallyMirrored(true);
    public static final Texture WALL_OUTER_CORNER_PIECE_HVM = new PngTexture("wall_outer_corner").setHorizontallyMirrored(true).setVerticallyMirrored(true);
    // player
    public static final Texture PLAYER_UP = new PngTexture("player_up").setPlayerTexture(true);
    public static final Texture PLAYER_DOWN = new PngTexture("player_down").setPlayerTexture(true);
    public static final Texture PLAYER_LEFT = new PngTexture("player_side").setHorizontallyMirrored(true).setPlayerTexture(true);
    public static final Texture PLAYER_RIGHT = new PngTexture("player_side").setPlayerTexture(true);
    public static final Texture[] PLAYER_UP_WALK = new Texture[]
            {
                    new PngTexture("player_up_walking_0").setPlayerTexture(true),
                    new PngTexture("player_up_walking_1").setPlayerTexture(true),
                    new PngTexture("player_up_walking_2").setPlayerTexture(true),
                    new PngTexture("player_up_walking_3").setPlayerTexture(true)
            };
    public static final Texture[] PLAYER_DOWN_WALK = new Texture[]
            {
                    new PngTexture("player_down_walking_0").setPlayerTexture(true),
                    new PngTexture("player_down_walking_1").setPlayerTexture(true),
                    new PngTexture("player_down_walking_2").setPlayerTexture(true),
                    new PngTexture("player_down_walking_3").setPlayerTexture(true)
            };
    public static final Texture[] PLAYER_LEFT_WALK = new Texture[]
            {
                    new PngTexture("player_side_walking_0").setHorizontallyMirrored(true).setPlayerTexture(true),
                    new PngTexture("player_side_walking_1").setHorizontallyMirrored(true).setPlayerTexture(true),
                    new PngTexture("player_side_walking_2").setHorizontallyMirrored(true).setPlayerTexture(true),
                    new PngTexture("player_side_walking_3").setHorizontallyMirrored(true).setPlayerTexture(true)
            };
    public static final Texture[] PLAYER_RIGHT_WALK = new Texture[]
            {
                    new PngTexture("player_side_walking_0").setPlayerTexture(true),
                    new PngTexture("player_side_walking_1").setPlayerTexture(true),
                    new PngTexture("player_side_walking_2").setPlayerTexture(true),
                    new PngTexture("player_side_walking_3").setPlayerTexture(true)
            };
    // scamperer
    public static final Texture SCAMPERER = new PngTexture("scamperer");
    // particles
    public static final Texture[] CHOP_PARTICLE = new Texture[]
            {
                    new PngTexture("chop_particle_0"),
                    new PngTexture("chop_particle_1"),
                    new PngTexture("chop_particle_2"),
                    new PngTexture("chop_particle_3")
            };
    // other gui
    public static final Texture VIEWPORT = new PngTexture("viewport");
    public static final Texture HEALTH = new PngTexture("ui_health");
    public static final Texture ENERGY = new PngTexture("ui_energy");
    // time gui
    public static final Texture SUNRISE = new PngTexture("ui_time_sunrise");
    public static final Texture MORNING = new PngTexture("ui_time_morning");
    public static final Texture AFTERNOON = new PngTexture("ui_time_afternoon");
    public static final Texture SUNSET = new PngTexture("ui_time_sunset");
    public static final Texture EVENING = new PngTexture("ui_time_evening");
    public static final Texture NIGHT = new PngTexture("ui_time_night");

    private Textures()
    {
        throw new IllegalStateException("This will be reported to the authorities.");
    }
}