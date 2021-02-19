package main.java;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import main.java.tiles.*;
import main.java.util.Coord;
import main.java.util.ShortPathFinder;

public class World 
{
	public static int worldWidth = 20; public static int worldHight = 16;
	public static Tile[] worldTileList = new Tile[worldWidth*worldHight];
	public ArrayList path;
	Texture texture;
	Random random = new Random();
	int r;
	private int endSignX; private int endSignY; private int startPosX; private int startPosY;
	public static Player worldPlayer;
	
	public void generate()
	{
		// Make the square world
		for (int x=0; x<worldWidth; x++)
		{
			for (int y=0; y<worldHight; y++)
			{
				r = random.nextInt(4);
				if (x==0 || y==0 || x==worldWidth-1 || y==worldHight-1)
				{
					worldTileList[x+(y*worldWidth)] = new BorderRockTile(Main.textures.wall);
				} else {
					worldTileList[x+(y*worldWidth)] = new FloorTile(Main.textures.ground);
				}
			}
		}
		
		// Make the holes at side and corners
		ArrayList<Integer> canPlaceList = new ArrayList<Integer>();
		ArrayList<Integer> shouldPlaceList = new ArrayList<Integer>();
		for (int i : new int[]{0,1,2,3,4,5,6,7})
		{
			canPlaceList.add(i);
		}
		while(canPlaceList.size() > 0)
		{
			int i = canPlaceList.get(random.nextInt(canPlaceList.size()));
			if ((i&1) !=1)
			{
				canPlaceList.removeAll(Arrays.asList(i-1 >= 0 ? i-1 : i+7));
				canPlaceList.removeAll(Arrays.asList(i));
				if (random.nextFloat() > 0.3f) shouldPlaceList.add(i);
				canPlaceList.removeAll(Arrays.asList(i+1 <= 7 ? i+1 : i-7));
			}
			else
			{	
				canPlaceList.removeAll(Arrays.asList(i-2 >= 0 ? i-2 : i+6));
				canPlaceList.removeAll(Arrays.asList(i-1 >= 0 ? i-1 : i+7));
				canPlaceList.removeAll(Arrays.asList(i));
				if (random.nextFloat() > 0.3f) shouldPlaceList.add(i);
				canPlaceList.removeAll(Arrays.asList(i+1 <= 7 ? i+1 : i-7));
				canPlaceList.removeAll(Arrays.asList(i+2 <= 7 ? i+2 : i-6));
			}
//			System.out.println(i);
//			System.out.println(canPlaceList);
//			System.out.println(shouldPlaceList);
		}
		if (shouldPlaceList.size() == 0)
			middleHoleCarve();
		for (int i : shouldPlaceList)
		{
			if ((i&1) !=1)
				cornerHoleCarve(i);
			else
				sideHoleCarve(i);
		}
		
		// Adds bush and rock obstacles scattered around the world
		placeSimpleObstacles();
		
		// Adds start and end
		placeStart();
		placeEndSign();
		
		// A check if there exists a valid path , if not , the entire world must be re-created
		ShortPathFinder spf = new ShortPathFinder();
		path = spf.findPath(startPosX,startPosY,endSignX,endSignY);
		if (path == null) { System.out.println("No valid path could got made"); boardFailedToCreate(); }
		if (path != null) { System.out.println(pathToString(path)); }
	}
	
	public void cornerHoleCarve(int corner)
	{
		int holeWidth = random.nextInt(5)+3;
		int holeHight = random.nextInt(5)+3;
		// Select the good corner for the hole to go
		int xOffSet = 0;
		int yOffSet = 0;
		if(corner == 2) 
		{ 	
			xOffSet = 0;							
			yOffSet = worldHight - holeHight; 
		}
		if(corner == 4) 
		{ 	
			xOffSet = worldWidth - holeWidth;		
			yOffSet = worldHight - holeHight; 
		}
		if(corner == 6) 
		{ 	
			xOffSet = worldWidth - holeWidth;		
			yOffSet = 0; 
		}
		// Set a square of tiles in the selected corner and with the selected size to air tiles
		for (int x=xOffSet; x<xOffSet+holeWidth; x++)
		{
			for (int y=yOffSet; y<yOffSet+holeHight; y++)
			{
				if 
				(		
						(xOffSet==0 && x==holeWidth-1) 
					 || (yOffSet==0 && y==holeHight-1)
					 || (xOffSet==worldWidth - holeWidth && x==xOffSet)
					 || (yOffSet==worldHight - holeHight && y==yOffSet)	
				) 
				{
					r = random.nextInt(4);
					worldTileList[x+(y*worldWidth)] = new BorderRockTile(Main.textures.wall);
				} 
				else 
				{
					worldTileList[x+(y*worldWidth)] = new AirTile(Main.textures.air);
				}
			}
		}
	}
	
	public void sideHoleCarve(int side)
	{
		int holeWidth = random.nextInt(5)+3;
		int holeHight = random.nextInt(5)+3;
		int xOffSet = 0;
		int yOffSet = 0;
		if(side == 1) 
		{ 
			xOffSet = 0;
			yOffSet = 3+random.nextInt(2);
		}
		if(side == 3) 
		{ 
			xOffSet = 3+random.nextInt(6);
			yOffSet = worldHight-holeHight;
		}
		if(side == 5) 
		{ 
			xOffSet = worldWidth-holeWidth;
			yOffSet = 3+random.nextInt(2);
		}
		if(side == 7) 
		{ 
			xOffSet = 3+random.nextInt(6);
			yOffSet = 0;
		}
		// Set a square of tiles in the selected corner and with the selected size to air tiles
		for (int x=xOffSet; x<xOffSet+holeWidth; x++)
		{
			for (int y=yOffSet; y<yOffSet+holeHight; y++)
			{
				if 
				(		
						(side==1 && (x==holeWidth-1 || y==yOffSet || y==yOffSet+holeHight-1)) 
					||	(side==3 && (y==worldHight-holeHight || x==xOffSet || x==xOffSet+holeWidth-1))
					|| 	(side==5 && (x==worldWidth-holeWidth || y==yOffSet || y==yOffSet+holeHight-1))
					||	(side==7 && (y==holeHight-1 || x==xOffSet || x==xOffSet+holeWidth-1))
				) 
				{
//					r = random.nextInt(4);
					worldTileList[x+(y*worldWidth)] = new BorderRockTile(Main.textures.wall);
				} 
				else 
				{
					worldTileList[x+(y*worldWidth)] = new AirTile(Main.textures.air);
				}
			}
		}
	}
	
	private void middleHoleCarve()
	{
		int holeWidth = random.nextInt(5)+3;
		int holeHight = random.nextInt(5)+3;
		int xOffSet = 5+random.nextInt(6);
		int yOffSet = 5+random.nextInt(2);
		for (int x=xOffSet; x<xOffSet+holeWidth; x++)
		{
			for (int y=yOffSet; y<yOffSet+holeHight; y++)
			{
				if 
				(
					x==xOffSet || x==xOffSet+holeWidth-1 || y==yOffSet || y==yOffSet+holeHight-1
				) 
				{
//					r = random.nextInt(4);
					worldTileList[x+(y*worldWidth)] = new BorderRockTile(Main.textures.wall);
				} 
				else 
				{
					worldTileList[x+(y*worldWidth)] = new AirTile(Main.textures.air);
				}
			}
		}
	}
	
	public void placeSimpleObstacles()
	{ // Note this for loop is a bit smaller because it skips the very outer edge
		for (int x=1; x<worldWidth-1; x++)
		{
			for (int y=1; y<worldHight-1; y++)
			{
				if (worldTileList[x+(y*worldWidth)] instanceof FloorTile)
				{
					r = random.nextInt(24);
					if (r == 0 || r == 1 || r == 2 || r == 3)
						worldTileList[x+(y*worldWidth)] = new BushTile();
					if (r == 4 || r == 5)
						worldTileList[x+(y*worldWidth)] = new RockTile(Main.textures.wall);
					if (r == 6)
						worldTileList[x+(y*worldWidth)] = new MushroomTile(Main.textures.mushroom);
				}
			}
		}
	}
	
	public void placeEndSign()
	{
		int pos=worldWidth*worldHight-1; 
		while(true)
		{
			if (pos>0)
			{
				if (!(worldTileList[pos-1] instanceof FloorTile))
					pos -= worldWidth;
				else
				{
					worldTileList[pos] = new SignTile(Main.textures.ground); // TODO: maybe sign texture idk
					endSignX=worldWidth-1;
					endSignY=(pos+1)/worldWidth-1;
					System.out.println("placed end sign at: "+endSignX+","+endSignY);
					break;
				}
			} 
			else 
			{
				System.out.println("Failed to create end sign");
				boardFailedToCreate();
				break;
			}
		}
	}
	
	public void placeStart()
	{
		int pos=worldWidth; 
		while(true)
		{
			if (pos<worldTileList.length)
			{
				if (!(worldTileList[pos+1] instanceof FloorTile))
					pos += worldWidth;
				else
				{
					worldTileList[pos] = new FloorTile(Main.textures.ground);
					startPosX=0;
					startPosY=(pos+1)/worldWidth;
					System.out.println("placed start at: "+startPosX+","+startPosY);
					if (worldPlayer == null)
						worldPlayer = new Player(new Coord(startPosX,startPosY), this);
					else
					{
						worldPlayer.playerCoord = new Coord(startPosX,startPosY);
					}
					break;
				}
			} 
			else 
			{
				System.out.println("Failed to create player start pos");
				boardFailedToCreate();
				break;
			}
		}
	}
	
	
	
	public static boolean isValidLocation(int sx, int sy, int tx, int ty)
	{
		if 
		(	 	
			worldTileList[tx+(ty*worldWidth)] instanceof BorderRockTile
			|| worldTileList[tx+(ty*worldWidth)] instanceof RockTile
		)
		{
			return false;
		} else if (tx<0 || ty<0 || tx>= worldWidth || ty>=worldHight) {
			return false;
		} else {
			return true;
		}
	}
	
	public String pathToString(ArrayList path)
	{
		String s = "[";
		for (int i = 0; i < path.size() ; i++)
		{
			Object coord = path.get(i);
			if (coord instanceof Coord)
			{
				s += ((Coord)coord).x+","+((Coord)coord).y;
			}
			s += "; ";
		}
		s += "]";
		return s;
	}
	
	public void boardFailedToCreate()
	{
		System.out.println("Failed board detected, retrying... (Don't worry, it can happen sometimes)");
		generate();
	}
	
	public void enemyTurn()
	{
		try {
			Thread.sleep(200);
			worldPlayer.playerCanMove = true;
		} catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
}