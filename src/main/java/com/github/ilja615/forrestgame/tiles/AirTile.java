package com.github.ilja615.forrestgame.tiles;

import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.gui.texture.Textures;

public class AirTile implements Tile
{
    @Override
    public Texture getTexture()
    {
        return Textures.AIR;
    }
}
