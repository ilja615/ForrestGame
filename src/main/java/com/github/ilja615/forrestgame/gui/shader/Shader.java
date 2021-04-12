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

package com.github.ilja615.forrestgame.gui.shader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Shader.class);
    private final int program;
    private final int vertexShader;
    private final int fragmentShader;

    public Shader(final String name)
    {
        this.vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, readSource(name + ".vertex"));
        glCompileShader(vertexShader);

        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE)
        {
            LOGGER.error("Vertex shader {} failed to compile!", name);
            LOGGER.info("Vertex shader info log is:\n{}", glGetShaderInfoLog(vertexShader));
            throw new IllegalStateException("Vertex shader failed to compile.");
        }

        this.fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, readSource(name + ".fragment"));
        glCompileShader(fragmentShader);

        if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE)
        {
            LOGGER.error("Fragment shader {} failed to compile!", name);
            LOGGER.info("Fragment shader info log is:\n{}", glGetShaderInfoLog(fragmentShader));
            throw new IllegalStateException("Fragment shader failed to compile.");
        }

        this.program = glCreateProgram();
        glAttachShader(getProgram(), vertexShader);
        glAttachShader(getProgram(), fragmentShader);
        glLinkProgram(getProgram());

        if (glGetProgrami(getProgram(), GL_LINK_STATUS) == GL_FALSE)
        {
            LOGGER.error("Program for shader {} failed to link!", name);
            LOGGER.error("Program info log is:\n{}" + glGetProgramInfoLog(getProgram()));
            throw new IllegalStateException("Program linking failed.");
        }

        glValidateProgram(getProgram());

        if (glGetProgrami(getProgram(), GL_VALIDATE_STATUS) == GL_FALSE)
        {
            LOGGER.error("Program for shader {} failed validation!", name);
            LOGGER.error("Program info log is:\n{}" + glGetProgramInfoLog(getProgram()));
            throw new IllegalStateException("Program validation failed.");
        }
    }

    public int getProgram()
    {
        return program;
    }

    public void destroy()
    {
        glDetachShader(program, vertexShader);
        glDetachShader(program, fragmentShader);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        glDeleteProgram(program);
    }

    public void use()
    {
        glUseProgram(getProgram());
    }

    private String readSource(final String file)
    {
        try (final InputStream inputStream = Objects.requireNonNull(Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("shaders/" + file)))
        {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (final IOException exception)
        {
            LOGGER.error("Could not read file {}", file);

            throw new RuntimeException(exception);
        }
    }
}