package com.github.ilja615.forrestgame.tiles.items;

import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.gui.renderer.TextureRenderer;
import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.util.Coordinate;

import java.util.Map;

public class DecorationItem implements Item
{
    private final Texture texture;

    public DecorationItem(final Texture t)
    {
        this.texture = t;
    }

    @Override
    public Texture getCurrentTexture()
    {
        return this.texture;
    }

    @Override
    public boolean isObstacle(Entity incomingEntity)
    {
        return false;
    }

    @Override
    public boolean onPlayerAttemptingWalk(Entity player, Coordinate coordinate)
    {
        return true;
    }

    @Override
    public Map<Coordinate, Texture> whichLayer(TextureRenderer tr)
    {
        return tr.LAYER_BACK;
    }
}
