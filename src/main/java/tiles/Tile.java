package main.java.tiles;

import main.java.Player;
import main.java.Texture;
import main.java.util.Coord;

abstract public class Tile 
{
	private Texture texture;
	public Tile(Texture texture)
	{
		this.texture = texture;
	}

	public Tile() {}
	
	public Texture getTexture()
	{
		return this.texture;
	}

	public boolean playerTryWalk(Player player, Coord from, Coord to)
	{
		return true;
	}

}
