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
    private static final char[] supportedCharacters = "abcdefghijklmnopqrstuvwxyz1234567890 :?!-".toCharArray();
    private final Map<String, Texture> charactersToTextures;

    public TextRenderer()
    {
        final ImmutableMap.Builder<String, Texture> builder = ImmutableMap.builder();

        for (final char character : supportedCharacters)
        {
            final String textureName = getTextureName(character);
            final Texture texture = new PngTexture(textureName);
            LOGGER.debug("Mapping texture name {} to {}", character, textureName);
            builder.put(textureName, texture);
        }

        charactersToTextures = builder.build();
    }

    private static String getTextureName(final char character)
    {
        if (Character.isDigit(character)) return "font/digits/" + character;
        else if (Character.isLetter(character)) return "font/letters/" + character;
        else return switch (character)
                    {
                        case ':' -> "font/punctuation/colon";
                        case '-' -> "font/punctuation/dash";
                        case '?' -> "font/punctuation/question_mark";
                        case '!' -> "font/punctuation/exclamation_point";
                        case ' ' -> "font/other/space";
                        default -> throw new IllegalStateException("Unknown character '" + character + "'");
                    };
    }

    public void drawString(final String string, float x, final float y, float size)
    {
        size /= 10;

        for (int index = 0; index < string.length(); index++)
        {
            final String textureName = TextRenderer.getTextureName(string.charAt(index));

            if (charactersToTextures.get(textureName) == null)
            {
                LOGGER.error("Unable to draw the texture for the character '" + string.charAt(index) + "'");
            } else
            {
                charactersToTextures.get(textureName).bind();

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
