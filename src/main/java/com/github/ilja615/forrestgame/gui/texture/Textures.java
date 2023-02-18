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

public interface Textures
{
    ////// TILES AND ITEMS
    Texture AIR = new PngTexture.Builder("tiles/air/default").build();
    Texture GRASS_0 = new PngTexture.Builder("tiles/grass/0").build();
    Texture GRASS_1 = new PngTexture.Builder("tiles/grass/1").build();
    Texture GRASS_2 = new PngTexture.Builder("tiles/grass/2").build();
    Texture GRASS_3 = new PngTexture.Builder("tiles/grass/3").build();
    Texture BUSH_0 = new PngTexture.Builder("items/bush/0").build();
    Texture BUSH_1 = new PngTexture.Builder("items/bush/1").build();
    Texture BUSH_2 = new PngTexture.Builder("items/bush/2").build();
    Texture BUSH_BERRY = new PngTexture.Builder("items/bush/berry").build();
    Texture[] MUSHROOM_CONFUSING = new Texture[]
            {
                    new PngTexture.Builder("items/mushroom/confusing_0").build(),
                    new PngTexture.Builder("items/mushroom/confusing_1").build()
            };
    Texture[] MUSHROOM_EDIBLE = new Texture[]
            {
                    new PngTexture.Builder("items/mushroom/edible_0").build(),
                    new PngTexture.Builder("items/mushroom/edible_1").build()
            };
    Texture SIGN_UP = new PngTexture.Builder("items/sign/up").build();
    Texture SIGN_DOWN = new PngTexture.Builder("items/sign/down").build();
    Texture SIGN_LEFT = new PngTexture.Builder("items/sign/left").build();
    Texture SIGN_RIGHT = new PngTexture.Builder("items/sign/right").build();
    Texture TREE = new PngTexture.Builder("items/tree").setTall(true).build();
    Texture CRATE = new PngTexture.Builder("items/crate").build();
    Texture PATH_DECORATION_0 = new PngTexture.Builder("items/path_decoration/0").build();
    Texture PATH_DECORATION_1 = new PngTexture.Builder("items/path_decoration/1").build();
    Texture PATH_DECORATION_2 = new PngTexture.Builder("items/path_decoration/2").build();
    Texture PATH_DECORATION_3 = new PngTexture.Builder("items/path_decoration/3").build();
    Texture PATH_DECORATION_4 = new PngTexture.Builder("items/path_decoration/4").build();

    Texture[] GROUND_ALTERNATIVES = new Texture[]{GRASS_0, GRASS_1, GRASS_2, GRASS_3};
    Texture[] PATH_DECORATION_ALTERNATIVES = new Texture[]{PATH_DECORATION_0, PATH_DECORATION_1, PATH_DECORATION_2, PATH_DECORATION_3, PATH_DECORATION_4};
    Texture[] LILY_PAD = new Texture[]
            {
                    new PngTexture.Builder("items/water/lily_pad_0").build(),
                    new PngTexture.Builder("items/water/lily_pad_1").build(),
                    new PngTexture.Builder("items/water/lily_pad_2").build()
            };
    Texture REEDS = new PngTexture.Builder("items/water/reeds").setTall(true).build();

    ////// PIECES FOR CONNECTED TEXTURES
    Texture AIR_PIECE = new PngTexture.Builder("tiles/air/small").build();

    // wall
    Texture DARK_TREE_FULL_PIECE = new PngTexture.Builder("tiles/dark_tree/full").build();
    Texture[] DARK_TREE_TOP_STRAIGHT_PIECE = new Texture[]
            {
                    new PngTexture.Builder("tiles/dark_tree/top_straight_0").setTall(true).build(),
                    new PngTexture.Builder("tiles/dark_tree/top_straight_1").setTall(true).build(),
                    new PngTexture.Builder("tiles/dark_tree/top_straight_2").setTall(true).build()

            };
    Texture DARK_TREE_BOTTOM_STRAIGHT_LEFT_PIECE = new PngTexture.Builder("tiles/dark_tree/bottom_straight_left").build();
    Texture DARK_TREE_BOTTOM_STRAIGHT_RIGHT_PIECE = new PngTexture.Builder("tiles/dark_tree/bottom_straight_right").build();
    Texture DARK_TREE_STRAIGHT_VERTICAL_LEFT_PIECE = new PngTexture.Builder("tiles/dark_tree/straight_vertical_left").build();
    Texture DARK_TREE_STRAIGHT_VERTICAL_RIGHT_PIECE = new PngTexture.Builder("tiles/dark_tree/straight_vertical_right").build();
    Texture DARK_TREE_BOTTOM_INNER_CORNER_LEFT_PIECE = new PngTexture.Builder("tiles/dark_tree/bottom_inner_corner_left").build();
    Texture DARK_TREE_BOTTOM_INNER_CORNER_RIGHT_PIECE = new PngTexture.Builder("tiles/dark_tree/bottom_inner_corner_right").build();
    Texture DARK_TREE_TOP_INNER_CORNER_LEFT_PIECE = new PngTexture.Builder("tiles/dark_tree/top_inner_corner_left").build();
    Texture DARK_TREE_TOP_INNER_CORNER_RIGHT_PIECE = new PngTexture.Builder("tiles/dark_tree/top_inner_corner_right").build();
    Texture DARK_TREE_BOTTOM_OUTER_CORNER_LEFT_PIECE = new PngTexture.Builder("tiles/dark_tree/bottom_outer_corner_left").build();
    Texture DARK_TREE_BOTTOM_OUTER_CORNER_RIGHT_PIECE = new PngTexture.Builder("tiles/dark_tree/bottom_outer_corner_right").build();
    Texture DARK_TREE_TOP_OUTER_CORNER_LEFT_PIECE = new PngTexture.Builder("tiles/dark_tree/top_outer_corner_left").setTall(true).build();
    Texture DARK_TREE_TOP_OUTER_CORNER_RIGHT_PIECE = new PngTexture.Builder("tiles/dark_tree/top_outer_corner_right").setTall(true).build();
    Texture WALL_SINGLE = new PngTexture.Builder("tiles/wall/single").build();


    // dirt
    Texture DIRT_FULL_PIECE = new PngTexture.Builder("tiles/dirt/full").build();
    Texture DIRT_STRAIGHT_PIECE = new PngTexture.Builder("tiles/dirt/straight").build();
    Texture DIRT_STRAIGHT_PIECE_MIRRORED = new PngTexture.Builder("tiles/dirt/straight").setVerticallyMirrored(true).build();
    Texture DIRT_STRAIGHT_VERTICAL_PIECE = new PngTexture.Builder("tiles/dirt/straight_vertical").build();
    Texture DIRT_STRAIGHT_VERTICAL_PIECE_MIRRORED = new PngTexture.Builder("tiles/dirt/straight_vertical").setHorizontallyMirrored(true).build();
    Texture DIRT_INNER_CORNER_PIECE = new PngTexture.Builder("tiles/dirt/inner_corner").build();
    Texture DIRT_INNER_CORNER_PIECE_HM = new PngTexture.Builder("tiles/dirt/inner_corner").setHorizontallyMirrored(true).build();
    Texture DIRT_INNER_CORNER_PIECE_VM = new PngTexture.Builder("tiles/dirt/inner_corner").setVerticallyMirrored(true).build();
    Texture DIRT_INNER_CORNER_PIECE_HVM = new PngTexture.Builder("tiles/dirt/inner_corner").setHorizontallyMirrored(true).setVerticallyMirrored(true).build();
    Texture DIRT_OUTER_CORNER_PIECE = new PngTexture.Builder("tiles/dirt/outer_corner").build();
    Texture DIRT_OUTER_CORNER_PIECE_HM = new PngTexture.Builder("tiles/dirt/outer_corner_other").build();
    Texture DIRT_OUTER_CORNER_PIECE_VM = new PngTexture.Builder("tiles/dirt/outer_corner_other").setHorizontallyMirrored(true).setVerticallyMirrored(true).build();
    Texture DIRT_OUTER_CORNER_PIECE_HVM = new PngTexture.Builder("tiles/dirt/outer_corner").setHorizontallyMirrored(true).setVerticallyMirrored(true).build();

    Texture WATER_FULL_PIECE_NORMAL = new PngTexture.Builder("tiles/water/full_normal").build();
    Texture[] WATER_FULL_PIECE = new Texture[]
            {
                    new PngTexture.Builder("tiles/water/full_0").build(),
                    new PngTexture.Builder("tiles/water/full_1").build(),
                    new PngTexture.Builder("tiles/water/full_2").build(),
                    new PngTexture.Builder("tiles/water/full_3").build()
            };
    Texture WATER_STRAIGHT_PIECE = new PngTexture.Builder("tiles/water/top_straight").build();
    Texture WATER_STRAIGHT_PIECE_MIRRORED = new PngTexture.Builder("tiles/water/bottom_straight").build();
    Texture[] WATER_STRAIGHT_VERTICAL_PIECE = new Texture[]
            {
                    new PngTexture.Builder("tiles/water/straight_vertical").build(),
                    new PngTexture.Builder("tiles/water/straight_vertical_other").build()
            };
    Texture[] WATER_STRAIGHT_VERTICAL_PIECE_MIRRORED = new Texture[]
            {
                    new PngTexture.Builder("tiles/water/straight_vertical").setHorizontallyMirrored(true).build(),
                    new PngTexture.Builder("tiles/water/straight_vertical_other").setHorizontallyMirrored(true).build()
            };
    Texture WATER_INNER_CORNER_PIECE = new PngTexture.Builder("tiles/water/bottom_inner_corner").build();
    Texture WATER_INNER_CORNER_PIECE_HM = new PngTexture.Builder("tiles/water/bottom_inner_corner").setHorizontallyMirrored(true).build();
    Texture WATER_INNER_CORNER_PIECE_VM = new PngTexture.Builder("tiles/water/top_inner_corner").setHorizontallyMirrored(true).build();
    Texture WATER_INNER_CORNER_PIECE_HVM = new PngTexture.Builder("tiles/water/top_inner_corner").build();
    Texture WATER_OUTER_CORNER_PIECE = new PngTexture.Builder("tiles/water/bottom_outer_corner").build();
    Texture WATER_OUTER_CORNER_PIECE_HM = new PngTexture.Builder("tiles/water/bottom_outer_corner").setHorizontallyMirrored(true).build();
    Texture WATER_OUTER_CORNER_PIECE_VM = new PngTexture.Builder("tiles/water/top_outer_corner").build();
    Texture WATER_OUTER_CORNER_PIECE_HVM = new PngTexture.Builder("tiles/water/top_outer_corner").setHorizontallyMirrored(true).build();

    ////// ENTITIES
    // player
    Texture PLAYER_UP = new PngTexture.Builder("entity/player/up").setPlayerTexture(true).build();
    Texture PLAYER_DOWN = new PngTexture.Builder("entity/player/down").setPlayerTexture(true).build();
    Texture PLAYER_LEFT = new PngTexture.Builder("entity/player/side").setHorizontallyMirrored(true).setPlayerTexture(true).build();
    Texture PLAYER_RIGHT = new PngTexture.Builder("entity/player/side").setPlayerTexture(true).build();

    Texture[] PLAYER_UP_WALK = new Texture[]
            {
                    new PngTexture.Builder("entity/player/up_walking_0").setPlayerTexture(true).build(),
                    new PngTexture.Builder("entity/player/up_walking_1").setPlayerTexture(true).build(),
                    new PngTexture.Builder("entity/player/up_walking_2").setPlayerTexture(true).build(),
                    new PngTexture.Builder("entity/player/up_walking_3").setPlayerTexture(true).build()
            };
    Texture[] PLAYER_DOWN_WALK = new Texture[]
            {
                    new PngTexture.Builder("entity/player/down_walking_0").setPlayerTexture(true).build(),
                    new PngTexture.Builder("entity/player/down_walking_1").setPlayerTexture(true).build(),
                    new PngTexture.Builder("entity/player/down_walking_2").setPlayerTexture(true).build(),
                    new PngTexture.Builder("entity/player/down_walking_3").setPlayerTexture(true).build()
            };
    Texture[] PLAYER_LEFT_WALK = new Texture[]
            {
                    new PngTexture.Builder("entity/player/side_walking_0").setHorizontallyMirrored(true).setPlayerTexture(true).build(),
                    new PngTexture.Builder("entity/player/side_walking_1").setHorizontallyMirrored(true).setPlayerTexture(true).build(),
                    new PngTexture.Builder("entity/player/side_walking_2").setHorizontallyMirrored(true).setPlayerTexture(true).build(),
                    new PngTexture.Builder("entity/player/side_walking_3").setHorizontallyMirrored(true).setPlayerTexture(true).build()
            };
    Texture[] PLAYER_RIGHT_WALK = new Texture[]
            {
                    new PngTexture.Builder("entity/player/side_walking_0").setPlayerTexture(true).build(),
                    new PngTexture.Builder("entity/player/side_walking_1").setPlayerTexture(true).build(),
                    new PngTexture.Builder("entity/player/side_walking_2").setPlayerTexture(true).build(),
                    new PngTexture.Builder("entity/player/side_walking_3").setPlayerTexture(true).build()
            };

    // Tangeling
    Texture TANGELING = new PngTexture.Builder("entity/tangeling").build();

    // Skeleton
    Texture SKELETON = new PngTexture.Builder("entity/skeleton").setTall(true).build();

    // Ghost
    Texture GHOST = new PngTexture.Builder("entity/ghost").build();

////// GUI

    // particles
    Texture[] CHOP_PARTICLE = new Texture[]
            {
                    new PngTexture.Builder("particles/chop/0").build(),
                    new PngTexture.Builder("particles/chop/1").build(),
                    new PngTexture.Builder("particles/chop/2").build(),
                    new PngTexture.Builder("particles/chop/3").build()
            };
    Texture[] CONFUSION_PARTICLE = new Texture[]
            {
                    new PngTexture.Builder("particles/confusion/0").build(),
                    new PngTexture.Builder("particles/confusion/1").build(),
                    new PngTexture.Builder("particles/confusion/2").build(),
                    new PngTexture.Builder("particles/confusion/3").build()
            };

    // other gui
    Texture VIEWPORT = new PngTexture.Builder("gui/viewport").build();

    // stat and effect gui
    Texture HEALTH = new PngTexture.Builder("gui/health").build();
    Texture ENERGY = new PngTexture.Builder("gui/energy").build();
    Texture CONFUSED = new PngTexture.Builder("gui/confused").build();

    // time gui
    Texture SUNRISE = new PngTexture.Builder("gui/time/sunrise").build();
    Texture MORNING = new PngTexture.Builder("gui/time/morning").build();
    Texture AFTERNOON = new PngTexture.Builder("gui/time/afternoon").build();
    Texture SUNSET = new PngTexture.Builder("gui/time/sunset").build();
    Texture EVENING = new PngTexture.Builder("gui/time/evening").build();
    Texture NIGHT = new PngTexture.Builder("gui/time/night").build();
}