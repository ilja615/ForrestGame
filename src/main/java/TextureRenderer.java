package main.java;

import static org.lwjgl.opengl.GL11.*;

import java.util.Random;

import main.java.tiles.BorderRockTile;
import main.java.tiles.FloorTile;

public class TextureRenderer 
{
	public static float partialX = 0f;
	public static float partialY = 0f;
	public void renderBoard()
	{	
		for (int x=World.worldPlayer.playerCoord.x - 4; x<=World.worldPlayer.playerCoord.x + 4; x++)
		{
			for (int y=World.worldPlayer.playerCoord.y - 4; y<=World.worldPlayer.playerCoord.y + 4; y++)
			{
				if (x>=0 && x<World.worldWidth && y>=0 && y<World.worldHight)
				{
					Texture texture = World.worldTileList[x+(y*World.worldWidth)].getTexture();
					Main.textureRenderer.renderTextureAt(texture, x, y);
				}
			}
		}
	}
	
	public void renderTextureAt(Texture texture, int x, int y)
	{
		
		x += (World.worldWidth/2 - World.worldPlayer.playerCoord.x);
		y += (World.worldHight/2 - World.worldPlayer.playerCoord.y);
		texture.bind();
	
		glBegin(GL_QUADS);
			glTexCoord2f(0,0);
			glVertex2f(-1.3125f+((float)x+partialX)/8.0f, -0.625f+((float)y+partialY)/8.0f);
			glTexCoord2f(1,0);
			glVertex2f(-1.1875f+((float)x+partialX)/8.0f, -0.625f+((float)y+partialY)/8.0f);
			glTexCoord2f(1,1);
			glVertex2f(-1.1875f+((float)x+partialX)/8.0f, -0.75f+((float)y+partialY)/8.0f);
			glTexCoord2f(0,1);
			glVertex2f(-1.3125f+((float)x+partialX)/8.0f, -0.75f+((float)y+partialY)/8.0f);
		glEnd();
	}
	
	public void renderPlayer(Texture texture)
	{
		texture.bind();
		glTranslatef(0, 0.25f, 0);
		//glRotated(playerAngle,0,0,1);
		glBegin(GL_QUADS);
			glTexCoord2f(0,0);
			glVertex2f(-0.0625f, 0.125f);
			glTexCoord2f(1,0);
			glVertex2f(0.0625f, 0.125f);
			glTexCoord2f(1,1);
			glVertex2f(0.0625f, 0);
			glTexCoord2f(0,1);
			glVertex2f(-0.0625f, 0);
		glEnd();
		//glRotated(-playerAngle,0,0,1);
		glTranslatef(0, -0.25f, 0);
	}
	
	public void renderViewport()
	{
		new Texture("viewport").bind();
	
		glBegin(GL_QUADS);
			glTexCoord2f(0,0);
			glVertex2f(-1.0f, 1.0f);
			glTexCoord2f(1,0);
			glVertex2f(1.0f, 1.0f);
			glTexCoord2f(1,1);
			glVertex2f(1.0f, -1.0f);
			glTexCoord2f(0,1);
			glVertex2f(-1.0f, -1.0f);
		glEnd();
	}
}
