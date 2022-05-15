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

import org.lwjgl.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class PngTexture implements Texture
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PngTexture.class);
    private final int id;
    private final String name;
    private final boolean tall;
    private final boolean horizontallyMirrored;
    private final boolean verticallyMirrored;
    private final boolean playerTexture;

    private PngTexture(final Builder builder)
    {
        this.name = builder.fileName;
        this.tall = builder.tall;
        this.horizontallyMirrored = builder.horizontallyMirrored;
        this.verticallyMirrored = builder.verticallyMirrored;
        this.playerTexture = builder.playerTexture;

        final IntBuffer width = BufferUtils.createIntBuffer(1);
        final IntBuffer height = BufferUtils.createIntBuffer(1);
        final IntBuffer comp = BufferUtils.createIntBuffer(1);

        try
        {
            final Path textureFile = Paths.get(System.getProperty("java.io.tmpdir"), "forrestgame")
                    .resolve(this.name + ".png");

            if (Files.notExists(textureFile))
            {
                final Path forrestGameFolder = textureFile.getParent();

                if (Files.notExists(forrestGameFolder))
                {
                    LOGGER.debug("Creating cached textures folder located at {}.", forrestGameFolder);
                }

                Files.createDirectories(forrestGameFolder);

                try (final InputStream inputStream = Objects.requireNonNull(this.getClass()
                        .getResourceAsStream("/textures/" + this.name + ".png")))
                {
                    Files.copy(inputStream, textureFile, StandardCopyOption.REPLACE_EXISTING);
                    LOGGER.debug("Creating cached texture file located at {}.", textureFile);
                }
            }

            final ByteBuffer data = stbi_load(
                    textureFile.toString(),
                    width,
                    height,
                    comp,
                    4
            );

            LOGGER.debug("Loaded the texture file {}.", textureFile);

            this.id = glGenTextures();

            glBindTexture(GL_TEXTURE_2D, id);

            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(), height.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
            stbi_image_free(Objects.requireNonNull(data));
        } catch (final IOException exception)
        {
            throw new IllegalStateException("Could not read the texture file " + this.name + ".", exception);
        }
    }

    @Override
    public void bind()
    {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    @Override
    public boolean isTall()
    {
        return tall;
    }

    @Override
    public boolean isHorizontallyMirrored()
    {
        return horizontallyMirrored;
    }

    @Override
    public boolean isVerticallyMirrored()
    {
        return verticallyMirrored;
    }

    @Override
    public boolean isPlayerTexture()
    {
        return playerTexture;
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    public static class Builder
    {
        private final String fileName;
        private boolean tall;
        private boolean horizontallyMirrored;
        private boolean verticallyMirrored;
        private boolean playerTexture;

        public Builder(final String fileName)
        {
            this.fileName = fileName;
        }

        public PngTexture.Builder setTall(final boolean tall)
        {
            this.tall = tall;
            return this;
        }

        public PngTexture.Builder setHorizontallyMirrored(final boolean horizontallyMirrored)
        {
            this.horizontallyMirrored = horizontallyMirrored;
            return this;
        }

        public PngTexture.Builder setVerticallyMirrored(final boolean verticallyMirrored)
        {
            this.verticallyMirrored = verticallyMirrored;
            return this;
        }


        public PngTexture.Builder setPlayerTexture(final boolean playerTexture)
        {
            this.playerTexture = playerTexture;
            return this;
        }

        public Texture build()
        {
            return new PngTexture(this);
        }
    }
}
