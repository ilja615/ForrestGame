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

import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.entity.related.EffectTracker;
import com.github.ilja615.forrestgame.entity.related.StatTracker;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;
import com.github.ilja615.forrestgame.world.TimeTracker;

import static com.github.ilja615.forrestgame.gui.texture.Textures.*;
import static org.lwjgl.opengl.GL11.*;

public class UiRenderer
{
    private static float TEXTURE_SIZE = 1/13f*2f;
    private static float TEXT_SIZE = TEXTURE_SIZE/16.0f*10.0f;

    public void renderHealth(final Entity player)
    {
        /*
        for (int i = 0; i < player.getStatTracker().get(StatTracker.Stat.HEALTH); i++)
        {
            renderTexture(Textures.HEALTH, -1.1f + i * 0.0835f, 0.8f, 0.167f);
        }
         */
        renderTexture(Textures.HEALTH, 0.5f*TEXTURE_SIZE, 1-TEXTURE_SIZE, TEXTURE_SIZE);
        player.getWorld()
                .getTextRenderer()
                .drawString(player.getStatTracker().get(StatTracker.Stat.HEALTH).toString(), 2.5f*TEXTURE_SIZE, 1-TEXTURE_SIZE, TEXT_SIZE);
    }

    public void renderEnergy(final Entity player)
    {
        /*
        for (int i = 0; i < player.getStatTracker().get(StatTracker.Stat.HUNGER); i++)
        {
            renderTexture(Textures.ENERGY, -0.1f + i * 0.0835f, 0.8f, 0.167f);
        }
         */
        renderTexture(Textures.ENERGY, 3*TEXTURE_SIZE, 1-TEXTURE_SIZE, TEXTURE_SIZE);
        player.getWorld()
                .getTextRenderer()
                .drawString(player.getStatTracker().get(StatTracker.Stat.HUNGER).toString(), 5f*TEXTURE_SIZE, 1-TEXTURE_SIZE, TEXT_SIZE);
    }

    public void renderEffects(final Entity player)
    {
        int i = 0;
        for (EffectTracker.Effect effect : player.getEffectTracker().getAllEffects())
        {
            if (effect.isActive())
            {
                renderTexture(EffectTracker.getTextureFromEffect(effect), -TEXTURE_SIZE - 1.5f*TEXTURE_SIZE*i, 1-TEXTURE_SIZE, TEXTURE_SIZE);
                player.getWorld()
                        .getTextRenderer()
                        .drawString(String.valueOf(effect.getTurnsLeft()),  0.5f*TEXTURE_SIZE- 1.5f*TEXTURE_SIZE*i, 1-TEXTURE_SIZE, TEXT_SIZE);
                i++;
            }
        }
    }

    public void renderStress(final Entity player)
    {
        if (player.getWorld().playerTurnTimer > 0 && player.getWorld().playerTurnTimer <= 25)
        {
            (switch((int) (Math.ceil(player.getWorld().playerTurnTimer/5.0d)))
                {
                    case 5 -> STRESS_55;
                    case 4 -> STRESS_45;
                    case 3 -> STRESS_35;
                    case 2 -> STRESS_25;
                    case 1 -> STRESS_15;
                    default -> STRESS_05;
                }).bind();

            glBegin(GL_QUADS);
            glTexCoord2f(0, 0);
            glVertex2f(-4.5f*TEXTURE_SIZE, -4*TEXTURE_SIZE);
            glTexCoord2f(1, 0);
            glVertex2f(4.5f*TEXTURE_SIZE, -4*TEXTURE_SIZE);
            glTexCoord2f(1, 1);
            glVertex2f(4.5f*TEXTURE_SIZE, -5*TEXTURE_SIZE);
            glTexCoord2f(0, 1);
            glVertex2f(-4.5f*TEXTURE_SIZE, -5*TEXTURE_SIZE);
            glEnd();
        }
    }

    public void renderTimeIcon(final TimeTracker.Period period)
    {
        renderTexture(period.getTexture(), -1-TEXTURE_SIZE, 1-TEXTURE_SIZE, TEXTURE_SIZE);

    }

    public void renderTexture(final Texture texture, float x, final float y, final float size)
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

    public void renderViewport()
    {
        VIEWPORT.bind();

        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(-1.0f, 1.0f);
        glTexCoord2f(1, 0);
        glVertex2f(1.0f, 1.0f);
        glTexCoord2f(1, 1);
        glVertex2f(1.0f, -1.0f);
        glTexCoord2f(0, 1);
        glVertex2f(-1.0f, -1.0f);
        glEnd();
    }
}
