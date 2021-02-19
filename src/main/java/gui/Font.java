package main.java.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.HashMap;
import java.util.Random;

import main.java.Texture;

public class Font extends HashMap<String, Texture>
{
	String characters = "abcdefghijklmnopqrstuvwxyz1234567890 :?!";

	private static final Random random = new Random();
	public Font(String filename)
	{
		super();
		initialization();
	}
	
	public void drawString(String string, float x, float y, float size) 
	{
		size = size/10;
		for(int i = 0; i < string.length(); i++)
		{
			get(getTextureNameFromChar(string.substring(i,i+1))).bind();
			x += size;
		
			glBegin(GL_QUADS);
				glTexCoord2f(0,0);
				glVertex2f(x, size+y);
				glTexCoord2f(1,0);
				glVertex2f(size+x, size+y);
				glTexCoord2f(1,1);
				glVertex2f(size+x, y);
				glTexCoord2f(0,1);
				glVertex2f(x, y);
			glEnd();
		}
	}

	private void initialization()
	{
		for(int i = 0; i < characters.length(); i++)
		{
			String textureName = getTextureNameFromChar(characters.substring(i,i+1));
			System.out.println(characters.substring(i,i+1)+" -> "+textureName);
			put(textureName, new Texture("src/main/font/", textureName));
		}
	}

	private String getTextureNameFromChar(String character)
	{
		String textureName;
		switch (character)
		{
			case " ": textureName = "space"; break;
			case ":": textureName = "colon"; break;
			case "?": textureName = "questionmark"; break;
			case "!": textureName = "exclamationmark"; break;
			default: textureName = character; break;
		}
		return textureName;
	}
}
