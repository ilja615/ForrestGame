package main.java;

import static org.lwjgl.glfw.GLFW.*;

import main.java.util.Coord;

public class Player 
{
	final float scrollSpeed = 0.06f;
	enum Direction {
		UP, DOWN, LEFT, RIGHT
	}
	
	public Player(Coord startPos, World world)
	{
		this.playerCoord = startPos;
		this.world = world;
	}
	
	private Texture texture = new Texture("player_idle");
	private World world;
	public Coord playerCoord;
	private Coord scheduledCoord;
	public boolean upKey;public boolean downKey;public boolean leftKey;public boolean rightKey;
	public boolean playerCanMove = true;
	public int hungerStat = 10;
	public int healthStat = 10;

	public Texture getTexture()
	{
		return this.texture;
	}
	
	public void tick()
	{
		if (TextureRenderer.partialX > 0 && TextureRenderer.partialX < 1)
		{
			TextureRenderer.partialX += scrollSpeed;
//			TextureRenderer.playerAngle = (int)(Math.sin(TextureRenderer.partialX * 6.2f)*10);
		} else if (TextureRenderer.partialX < 0 && TextureRenderer.partialX > -1)
		{
			TextureRenderer.partialX -= scrollSpeed;
//			TextureRenderer.playerAngle = (int)(Math.sin(TextureRenderer.partialX * 6.2f)*10);
		} else if (TextureRenderer.partialX <= -1 || TextureRenderer.partialX >= 1)
		{
			TextureRenderer.partialX = 0;
//			TextureRenderer.playerAngle = 0;
			if (this.scheduledCoord != null)
				this.playerCoord = this.scheduledCoord;
		}

		if (TextureRenderer.partialY > 0 && TextureRenderer.partialY < 1)
		{
			TextureRenderer.partialY += scrollSpeed;
//			TextureRenderer.playerAngle = (int)(Math.sin(TextureRenderer.partialY * 6.2f)*10);
		} else if (TextureRenderer.partialY < 0 && TextureRenderer.partialY > -1)
		{
			TextureRenderer.partialY -=scrollSpeed;
//			TextureRenderer.playerAngle = (int)(Math.sin(TextureRenderer.partialY * 6.2f)*10);
		} else if (TextureRenderer.partialY <= -1 || TextureRenderer.partialY >= 1)
		{
			TextureRenderer.partialY = 0;
//			TextureRenderer.playerAngle = 0;
			if (this.scheduledCoord != null)
				this.playerCoord = this.scheduledCoord;
		}

		if (playerCanMove && TextureRenderer.partialX == 0 && TextureRenderer.partialY == 0)
		{
			World.worldPlayer.upKey = (glfwGetKey(Main.window, GLFW_KEY_UP) == GLFW_TRUE);
			World.worldPlayer.downKey = (glfwGetKey(Main.window, GLFW_KEY_DOWN) == GLFW_TRUE);
			World.worldPlayer.leftKey = (glfwGetKey(Main.window, GLFW_KEY_LEFT) == GLFW_TRUE);
			World.worldPlayer.rightKey = (glfwGetKey(Main.window, GLFW_KEY_RIGHT) == GLFW_TRUE);
			if (this.upKey)
			{
				playerCanMove = false;
				moveTo(new Coord(this.playerCoord.x, this.playerCoord.y+1), Direction.UP);
			}
			else if (this.downKey)
			{
				playerCanMove = false;
				moveTo(new Coord(this.playerCoord.x, this.playerCoord.y-1), Direction.DOWN);
			}
			else if (this.rightKey)
			{
				playerCanMove = false;
				moveTo(new Coord(this.playerCoord.x+1, this.playerCoord.y), Direction.RIGHT);
			}
			else if (this.leftKey)
			{
				playerCanMove = false;
				moveTo(new Coord(this.playerCoord.x-1, this.playerCoord.y), Direction.LEFT);
			}
		}
	}

	private void moveTo(Coord coord, Direction dir)
	{
		if (this.world.worldTileList[coord.x+(coord.y*this.world.worldWidth)].playerTryWalk(this, this.playerCoord, coord))
		{
			if (World.isValidLocation(this.playerCoord.x, this.playerCoord.y, coord.x, coord.y)) {
				this.scheduledCoord = coord;
				//this.hungerStat -= 1;
				Main.world.enemyTurn();
				if (dir == Direction.UP) TextureRenderer.partialY = -scrollSpeed;
				if (dir == Direction.DOWN) TextureRenderer.partialY = scrollSpeed;
				if (dir == Direction.RIGHT) TextureRenderer.partialX = -scrollSpeed;
				if (dir == Direction.LEFT) TextureRenderer.partialX = scrollSpeed;
				return;
			}
		}
		this.playerCanMove = true;
	}
}
	