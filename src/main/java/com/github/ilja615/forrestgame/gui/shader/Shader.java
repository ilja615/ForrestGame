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

import com.github.ilja615.forrestgame.gui.texture.PngTexture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PngTexture.class);
    public int program;
    private int vertexShader, fragmentShader;

    public Shader()
    {
    }

    public boolean create(String shader)
    {
        int success;

        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, readSource(shader + ".vertex"));
        glCompileShader(vertexShader);

        success = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        if (success == GL_FALSE)
        {
            System.err.println("Vertex: \n" + glGetShaderInfoLog(vertexShader));
            return false;
        }

        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, readSource(shader + ".fragment"));
        glCompileShader(fragmentShader);

        success = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
        if (success == GL_FALSE)
        {
            LOGGER.error("Fragment: \n" + glGetShaderInfoLog(fragmentShader));
            return false;
        }

        program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);

        glLinkProgram(program);
        success = glGetProgrami(program, GL_LINK_STATUS);
        if (success == GL_FALSE)
        {
            LOGGER.error("Program Link: \n" + glGetProgramInfoLog(program));
            return false;
        }
        glValidateProgram(program);
        success = glGetProgrami(program, GL_VALIDATE_STATUS);
        if (success == GL_FALSE)
        {
            LOGGER.error("Program Validate: \n" + glGetProgramInfoLog(program));
            return false;
        }
        return true;
    }

    public void destroy()
    {
        glDetachShader(program, vertexShader);
        glDetachShader(program, fragmentShader);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        glDeleteProgram(program);
    }

    public void useShader()
    {
        glUseProgram(program);
    }

    private String readSource(String file)
    {
        BufferedReader reader = null;
        StringBuilder sourceBuilder = new StringBuilder();

        try
        {
            reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/shaders/" + file)));

            String line;

            while ((line = reader.readLine()) != null)
            {
                sourceBuilder.append(line + "\n");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                reader.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return sourceBuilder.toString();
    }
}