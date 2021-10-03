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

package com.github.ilja615.forrestgame;

import com.github.ilja615.forrestgame.gui.shader.Shader;
import com.github.ilja615.forrestgame.world.World;
import org.lwjgl.glfw.GLFWVidMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;

public class ForrestGame implements Game
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ForrestGame.class);
    private final long window;

    private ForrestGame(final long window)
    {
        this.window = window;
    }

    public static void main(final String[] args)
    {
        if (!glfwInit()) throw new IllegalStateException("Failed to initialize GLFW!");

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        final long window = glfwCreateWindow(710, 710, "Forrest Game", 0, 0);

        if (window == 0) throw new IllegalStateException("Failed to create window!");

        final GLFWVidMode videoMode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()), "Could not get video mode");
        glfwSetWindowPos(window, (videoMode.width() - 710) / 2, (videoMode.height() - 710) / 2);

        glfwShowWindow(window);
        glfwMakeContextCurrent(window);
        createCapabilities();
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Create the shaders
        final Shader shader = new Shader();
        shader.create("basic");

        final Game game = new ForrestGame(window);
        final World world = new World(game, shader);

        while (!glfwWindowShouldClose(window))
        {
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT);

            // Use the shaders
            shader.useShader();

            world.tick();

            glfwSwapBuffers(window);
        }

        // Destroy the shaders
        shader.destroy();

        game.end(EndReason.MANUAL_EXIT);
    }

    @Override
    public long getWindow()
    {
        return window;
    }

    @Override
    public void end(final EndReason reason)
    {
        glfwTerminate();
        LOGGER.info("exit reason: " + reason.getMessage());
        LOGGER.info("thx for playing");
        System.exit(0);
    }
}