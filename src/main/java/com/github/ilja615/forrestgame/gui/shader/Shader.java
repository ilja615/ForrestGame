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

package com.github.ilja615.forrestgame.gui.shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL20.*;

public class Shader
{
    private final int vertexShader, fragmentShader;
    public int program;

    public Shader(final String shader)
    {
        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, readSource(shader + ".vertex"));
        glCompileShader(vertexShader);
        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE)
        {
            throw new IllegalStateException("Vertex failed to compile:\n" + glGetShaderInfoLog(vertexShader));
        }

        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, readSource(shader + ".fragment"));
        glCompileShader(fragmentShader);
        if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE)
        {
            throw new IllegalStateException("Fragment failed to compile:\n" + glGetShaderInfoLog(fragmentShader));
        }

        program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE)
        {
            throw new IllegalStateException("Program failed to link:\n" + glGetProgramInfoLog(program));
        }

        glValidateProgram(program);
        if (glGetProgrami(program, GL_VALIDATE_STATUS) == GL_FALSE)
        {
            throw new IllegalStateException("Program failed to validate:\n" + glGetProgramInfoLog(program));
        }
    }

    public void use()
    {
        glUseProgram(program);
    }

    public void destroy()
    {
        glDetachShader(program, vertexShader);
        glDetachShader(program, fragmentShader);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        glDeleteProgram(program);
    }

    private String readSource(final String file)
    {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                this.getClass().getResourceAsStream("/shaders/" + file),
                "The shader file " + file + " could not be found."
        ))))
        {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (final IOException exception)
        {
            throw new IllegalStateException("Could not load the shader file " + file + ".", exception);
        }
    }

}