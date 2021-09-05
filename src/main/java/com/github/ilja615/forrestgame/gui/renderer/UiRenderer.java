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

package com.github.ilja615.forrestgame.gui.renderer;

import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.entity.Player;
import com.github.ilja615.forrestgame.entity.StatTracker;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;
import com.github.ilja615.forrestgame.world.TimeTracker;
import com.github.ilja615.forrestgame.world.World;

import static org.lwjgl.opengl.GL11.*;

public class UiRenderer
{
    public void renderHealth(final Entity player)
    {
//        for (int i = 0; i < player.getStatTracker().get(StatTracker.Stat.HEALTH); i++)
//        {
//            renderTexture(Textures.HEALTH, -1.1f + i * 0.0835f, 0.8f, 0.167f);
//        }
        renderTexture(Textures.HEALTH, 0.05f, 0.8f, 0.167f);
        player.getWorld().getTextRenderer().drawString("x" + player.getStatTracker().get(StatTracker.Stat.HEALTH).toString(), 0.35f, 0.81f, 0.5f);
    }

    public void renderEnergy(final Entity player)
    {
//        for (int i = 0; i < player.getStatTracker().get(StatTracker.Stat.HUNGER); i++)
//        {
//            renderTexture(Textures.ENERGY, -0.1f + i * 0.0835f, 0.8f, 0.167f);
//        }
        renderTexture(Textures.ENERGY, 0.4f, 0.8f, 0.167f);
        player.getWorld().getTextRenderer().drawString("x" + player.getStatTracker().get(StatTracker.Stat.HUNGER).toString(), 0.7f, 0.81f, 0.5f);

    }

    public void renderTimeIcon(TimeTracker.Period period)
    {
        renderTexture(period.getTexture(), -1.08f, 0.74f, 0.167f);
    }

    public void renderTexture(final Texture texture, float x, float y, float size)
    {
        texture.bind();
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
