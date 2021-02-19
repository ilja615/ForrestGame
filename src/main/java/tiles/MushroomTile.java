package main.java.tiles;

import main.java.Main;
import main.java.Player;
import main.java.Texture;
import main.java.util.Coord;

public class MushroomTile extends Tile
{
	public MushroomTile(Texture texture)
	{
		super(texture);
	}

	@Override
	public boolean playerTryWalk(Player player, Coord from, Coord to)
	{
		player.hungerStat = Math.min(10, player.hungerStat+1);
		Main.world.worldTileList[to.x+(to.y*Main.world.worldWidth)] = new FloorTile(Main.textures.ground);
		return true;
	}
}
