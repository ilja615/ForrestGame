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

package com.github.ilja615.forrestgame;

public interface Game
{
    /**
     * Returns the handle of the GLFW window.
     *
     * @return the handle of the GLFW window
     */
    long getWindow();

    /**
     * Ends the game.
     *
     * @param reason the reason why the game was ended
     */
    void end(final EndReason reason);

    enum EndReason
    {
        MANUAL_EXIT("manually exited game"),
        DIED("died"),
        NO_ENERGY("no energy");

        private final String message;

        EndReason(final String message)
        {
            this.message = message;
        }

        public String getMessage()
        {
            return message;
        }
    }
}