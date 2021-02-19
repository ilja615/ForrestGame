package main.java.tiles;

import main.java.Main;
import main.java.Player;
import main.java.Texture;
import main.java.util.Coord;

public class BushTile extends Tile
{
	private int stage = 2;
	private Texture texture2 = new Texture("bush2");
	private Texture texture1 = new Texture("bush1");
	private Texture texture0 = new Texture("bush0");

	public BushTile() {}

	@Override
	public Texture getTexture()
	{
		if (stage == 2) return texture2;
		else if (stage == 1) return texture1;
		else return texture0;
	}
	
	@Override
	public boolean playerTryWalk(Player player, Coord from, Coord to)
	{
		if (stage > 0)
		{
			stage -= 1;
			player.playerCanMove = false;
			player.hungerStat -= 1;
			Main.world.enemyTurn();
			return false;
		} else {
			return true;
		}
	}
}
