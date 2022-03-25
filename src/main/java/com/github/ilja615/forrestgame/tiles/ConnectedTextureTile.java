package com.github.ilja615.forrestgame.tiles;

import com.github.ilja615.forrestgame.gui.texture.Texture;
import com.github.ilja615.forrestgame.util.Coordinate;
import com.github.ilja615.forrestgame.util.Direction;
import com.github.ilja615.forrestgame.world.World;

import java.util.EnumMap;

public interface ConnectedTextureTile
{
    void adaptQuadrantTexturesList(World world, Coordinate thisPos);

    EnumMap<Direction.Secondary, Texture> getQuadrantTextures();
}
