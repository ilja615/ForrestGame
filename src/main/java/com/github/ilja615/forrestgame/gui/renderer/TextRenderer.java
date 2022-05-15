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

package com.github.ilja615.forrestgame.gui.renderer;

import com.github.ilja615.forrestgame.gui.texture.PngTexture;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class TextRenderer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TextRenderer.class);
    private static final char[] characters = "abcdefghijklmnopqrstuvwxyz1234567890 :?!-".toCharArray();
    private final Map<String, Texture> characterToTextureMap;

    public TextRenderer()
    {
        final ImmutableMap.Builder<String, Texture> builder = ImmutableMap.builder();

        for (final char c : characters)
        {
            final String textureName = TextRenderer.getTextureName(c);
            final Texture texture = new PngTexture.Builder("font/" + textureName).build();
            LOGGER.debug("Mapping texture name {} to {}", c, "font/" + textureName);
            builder.put(textureName, texture);
        }

        characterToTextureMap = builder.build();
    }

    private static String getTextureName(final char character)
    {
        return switch (character)
                {
                    case ' ' -> "space";
                    case ':' -> "colon";
                    case '?' -> "question_mark";
                    case '!' -> "exclamation_point";
                    case '-' -> "hyphen";
                    default -> String.valueOf(character);
                };
    }

    public void drawString(final String string, float x, final float y, float size)
    {
        size /= 10;

        for (int i = 0; i < string.length(); i++)
        {
            if (characterToTextureMap.get(TextRenderer.getTextureName(string.charAt(i))) == null)
            {
                LOGGER.error("The texture for the character '{}' could not be found.", string.charAt(i));
            } else
            {
                characterToTextureMap.get(TextRenderer.getTextureName(string.charAt(i))).bind();

                x += size;

                glBegin(GL_QUADS);
                glTexCoord2f(0, 0);
                glVertex2f(x, size + y);
                glTexCoord2f(1, 0);
                glVertex2f(size + x, size + y);
                glTexCoord2f(1, 1);
                glVertex2f(size + x, y);
                glTexCoord2f(0, 1);
                glVertex2f(x, y);
                glEnd();
            }
        }
    }
}
