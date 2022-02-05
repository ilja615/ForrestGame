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

package com.github.ilja615.forrestgame.utility;

import com.github.ilja615.forrestgame.Game;
import org.lwjgl.glfw.GLFW;

public final class KeyInput
{
    private KeyInput()
    {
    }

    /**
     * Returns if the key is pressed.
     *
     * @param game the game to get the window handle from
     * @param key  the key to check for
     * @return if the key is pressed
     */
    public static boolean isKeyDown(final Game game, final int key)
    {
        return GLFW.glfwGetKey(game.getWindow(), key) == GLFW.GLFW_TRUE;
    }
}
