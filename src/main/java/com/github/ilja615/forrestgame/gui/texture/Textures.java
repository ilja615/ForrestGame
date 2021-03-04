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

public interface Textures
{
    // tiles
    Texture AIR = new PngTexture("textures/air");
    Texture GROUND = new PngTexture("textures/ground");
    Texture WALL = new PngTexture("textures/wall");
    Texture MUSHROOM = new PngTexture("textures/mushroom");
    Texture SIGN = new PngTexture("textures/sign");
    Texture TREE = new PngTexture("textures/tree", true);

    // player
    Texture PLAYER_DOWN = new PngTexture("textures/player_down");

    // other
    Texture VIEWPORT = new PngTexture("textures/viewport");
}
