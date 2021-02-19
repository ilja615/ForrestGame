package main.java.tiles;

import main.java.Main;
import main.java.Player;
import main.java.Texture;
import main.java.util.Coord;

public class SignTile extends Tile
{

	public SignTile(Texture texture) 
	{
		super(texture);
	}
	
	@Override
	public boolean playerTryWalk(Player player, Coord from, Coord to)
	{
		try {
			Thread.sleep(100);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		Main.world.generate();
		return false;
	}
}
